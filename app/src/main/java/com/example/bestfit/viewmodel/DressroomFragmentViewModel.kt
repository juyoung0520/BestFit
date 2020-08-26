package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.DressroomCategoryFragment
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DressroomFragmentViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _itemDTOs = MutableLiveData<ArrayList<ArrayList<ItemDTO>>>(arrayListOf())
    val itemDTOs: LiveData<ArrayList<ArrayList<ItemDTO>>> = _itemDTOs

    private val _itemRecyclerViewAdapter = MutableLiveData<DressroomCategoryFragment.ItemRecyclerViewAdapter>()
    val itemRecyclerViewAdapter: LiveData<DressroomCategoryFragment.ItemRecyclerViewAdapter> = _itemRecyclerViewAdapter

    init {
        println("init dressroomfragmentviewmodel")
        getItemDTOs()
    }

    fun setItemRecyclerViewAdapter(adapter: DressroomCategoryFragment.ItemRecyclerViewAdapter) {
        _itemRecyclerViewAdapter.value = adapter
    }

    fun getItemRecyclerViewAdapter(): DressroomCategoryFragment.ItemRecyclerViewAdapter {
        return _itemRecyclerViewAdapter.value!!
    }

    private fun notifyItemDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            println("hihihi notify")
            _itemDTOs.value = _itemDTOs.value
        }
    }

    private fun getItemDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            for (i in 0 until InitData.categoryDTOs.size)
                _itemDTOs.value!!.add(arrayListOf())

            val document = db.collection("accounts").document(currentUid).get().await()
            val accountDTO = document.toObject(AccountDTO::class.java)!!

            if (accountDTO.items.isNullOrEmpty()) {
                withContext(Dispatchers.Main) {
                    _isInitialized.value = true
                }

                return@launch
            }

            val tasks = accountDTO.items!!.map { itemId ->
                db.collection("items").document(itemId).get()
            }

            val result = Tasks.whenAllComplete(tasks).await()
            for (task in result) {
                val doc = task.result as DocumentSnapshot
                val itemDTO = doc.toObject(ItemDTO::class.java)!!
                val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

                _itemDTOs.value!![0].add(itemDTO)
                _itemDTOs.value!![categoryIndex].add(itemDTO)
            }

            for (itemDTO in _itemDTOs.value!!)
                itemDTO.sortByDescending { itemDTO -> itemDTO.timestamps!![0] }

//            notifyItemDTOsChanged()
            withContext(Dispatchers.Main) {
                _isInitialized.value = true
            }
        }
    }

    fun addItemDTO(itemDTO: ItemDTO) {
        val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

        _itemDTOs.value!![0].add(0, itemDTO)
        _itemDTOs.value!![categoryIndex].add(0, itemDTO)

        println("in additemdto func")
        notifyItemDTOsChanged()
    }

    fun removeItemDTO(itemDTO: ItemDTO) {
        val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

        _itemDTOs.value!![0].add(itemDTO)
        _itemDTOs.value!![categoryIndex].add(itemDTO)

        notifyItemDTOsChanged()
    }
}
