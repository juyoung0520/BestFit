package com.example.bestfit.viewmodel

import android.accounts.Account
import android.content.ClipData
import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
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

    private val _followItemDTOs = MutableLiveData<ArrayList<ItemDTO>>(arrayListOf())
    val followerItemDTOs: LiveData<ArrayList<ItemDTO>> = _followItemDTOs

    private val _itemDTOs = MutableLiveData<ArrayList<ItemDTO>>(arrayListOf())
    val itemDTOs: LiveData<ArrayList<ItemDTO>> = _itemDTOs

    private var lastItemDTO: DocumentSnapshot? = null

    private val allFollowItemDTOs = mutableMapOf<String, ArrayList<ItemDTO>>()

    init {
        getAllItemDTOs()
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

                for (doc in task.result!!) {
                    val itemDTO = doc.toObject(ItemDTO::class.java)

                    _itemDTOs.value!!.add(itemDTO)

                    if (doc == task.result!!.last() ) {
                        lastItemDTO = doc
                        println(itemDTO)
                    }
                }
            }.await()

            println("get")
            println(_itemDTOs.value)
            notifyItemDTOs()
        }
    }

    private fun buildQuery(): Query {
        if (lastItemDTO == null)
            return db.collection("items").orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(QUERY_ITEM_CNT)
        else
            return db.collection("items").orderBy("timstamp", Query.Direction.DESCENDING)
                .startAfter(lastItemDTO)
                .limit(QUERY_ITEM_CNT)
    }
}