package com.example.bestfit.viewmodel

import android.accounts.Account
import android.content.ClipData
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.bestfit.datasource.ItemDTODataSource
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragmentViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val QUERY_ITEM_CNT: Long = 5
    private lateinit var config: PagedList.Config


//    private lateinit var pagedListBuilder: LivePagedListBuilder<Long, ItemDTO>
//    private lateinit var dataSource: DataSource<Long, ItemDTO>

    private val _pagedList = MutableLiveData<PagedList<ItemDTO>>()
    val pagedList: LiveData<PagedList<ItemDTO>> = _pagedList

    private val _followItemDTOs = MutableLiveData<ArrayList<ItemDTO>>(arrayListOf())
    val followerItemDTOs: LiveData<ArrayList<ItemDTO>> = _followItemDTOs

    private val _itemDTOs = MutableLiveData<ArrayList<ItemDTO>>(arrayListOf())
    val itemDTOs: LiveData<ArrayList<ItemDTO>> = _itemDTOs

    private var lastItemDTO: DocumentSnapshot?= null

    private val allFollowItemDTOs = mutableMapOf<String, ArrayList<ItemDTO>>()

    init {
        initPagedList()
       // getAllItemDTOs()
    }

    private fun initPagedList() {

        config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(5)
            .setPrefetchDistance(4)
            .setPageSize(5)
            .build()

//        val dataSourceFactory = ItemDTODataSource.Factory()
//        pagedListBuilder = LivePagedListBuilder<Long, ItemDTO>(dataSourceFactory, config)
    }

    private fun notifyFollowItemDTOs() {
        viewModelScope.launch(Dispatchers.Main) {
            _followItemDTOs.value = _followItemDTOs.value
        }
    }

    private fun notifyItemDTOs() {
        viewModelScope.launch(Dispatchers.Main) {
            _itemDTOs.value = _itemDTOs.value
        }
    }

    fun getItemDTOs(accountDTO: AccountDTO) {

        viewModelScope.launch(Dispatchers.IO) {
            if (allFollowItemDTOs.containsKey(accountDTO.id)) {
                viewModelScope.launch(Dispatchers.Main) {
                    _followItemDTOs.value = ArrayList(allFollowItemDTOs[accountDTO.id])
                }
                return@launch
            }

            _followItemDTOs.value!!.clear()
            val uids = if (accountDTO.items!!.size <= 2 ) accountDTO.items else accountDTO.items!!.drop(accountDTO.items!!.size-2)

            val tasks = uids!!.map { uid ->
                db.collection("items").document(uid).get()
            }

            val result = Tasks.whenAllComplete(tasks).await()
            for (task in result) {
                val doc = task.result as DocumentSnapshot
                val itemDTO = doc.toObject(ItemDTO::class.java)

                _followItemDTOs.value!!.add(itemDTO!!)
            }

            notifyFollowItemDTOs()
            allFollowItemDTOs.put(accountDTO.id!!, ArrayList(_followItemDTOs.value))
        }
    }

    fun getAllItemDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            val query = buildQuery()

            query.get().addOnCompleteListener { task ->

                println("in addcomplete")

                for (doc in task.result!!) {
                    val itemDTO = doc.toObject(ItemDTO::class.java)

                    _itemDTOs.value!!.add(itemDTO)

                    if (doc == task.result!!.last()) {
                        lastItemDTO = doc
                        println(lastItemDTO)
                    }
                }
            }.await()

            println("get")
            println(_itemDTOs.value!!.size)
            notifyItemDTOs()
        }
    }

    fun getConfing(): PagedList.Config {
        return config
    }

    fun getQuery(): Query {
        return db.collection("items").orderBy("timestamps", Query.Direction.DESCENDING)
    }


    private fun buildQuery(): Query {
        if (lastItemDTO == null)
            return db.collection("items").orderBy("timestamps", Query.Direction.DESCENDING)
                .limit(QUERY_ITEM_CNT)
        else{
            println("not first")
            return db.collection("items").orderBy("timestamps", Query.Direction.DESCENDING)
                .startAfter(lastItemDTO as DocumentSnapshot)
                .limit(QUERY_ITEM_CNT)
        }
    }
}