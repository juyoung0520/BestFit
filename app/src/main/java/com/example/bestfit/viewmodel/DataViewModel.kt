package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.model.SizeFormatDTO
import com.example.bestfit.util.InitData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DataViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private val _isInitialized = MutableLiveData(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    private val _allItemDTOs = MutableLiveData<ArrayList<ArrayList<ItemDTO>>>(arrayListOf())
    val allItemDTOs: LiveData<ArrayList<ArrayList<ItemDTO>>> = _allItemDTOs

    private val _dibsItemDTOs = MutableLiveData<ArrayList<ItemDTO>>(arrayListOf())
    val dibsItemDTOs: LiveData<ArrayList<ItemDTO>> = _dibsItemDTOs

    init {
        getInitialData()
    }

    // initialData
    private fun getInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = arrayListOf<Task<QuerySnapshot>>()
            tasks.add(db.collection("categories").orderBy("index").get())
            tasks.add(db.collection("sizes").orderBy("index").get())

            val result = Tasks.whenAllComplete(tasks).await()
            for ((idx, task) in result.withIndex()) {
                val snapshot = task.result as QuerySnapshot
                for (doc in snapshot) {
                    when (idx) {
                        0 -> {
                            val categoryDTO = doc.toObject(CategoryDTO::class.java)
                            InitData.categoryDTOs.add(categoryDTO)
                        }
                        1 -> {
                            val sizeFormatDTO = doc.toObject(SizeFormatDTO::class.java)
                            InitData.sizeFormatDTOs.add(sizeFormatDTO)
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                _isInitialized.value = true
            }
        }
    }

    private fun notifyItemDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _allItemDTOs.value = _allItemDTOs.value
        }
    }

    // accountDTO만 다시 로딩할 때 호출
    fun getAccountDTO(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val document = db.collection("accounts").document(uid).get().await()
            val accountDTO = document.toObject(AccountDTO::class.java)!!

            _accountDTO.value = accountDTO
        }
    }

    // itemDTOs 로딩할 때 호출
    fun getItemDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            _allItemDTOs.value!!.clear()

            for (i in 0 until InitData.categoryDTOs.size)
                _allItemDTOs.value!!.add(arrayListOf())

            val doc = db.collection("accounts").document(currentUid).get().await()
            val accountDTO = doc.toObject(AccountDTO::class.java)!!

            withContext(Dispatchers.Main) {
                _accountDTO.value = accountDTO
            }

            if (accountDTO.items.isNullOrEmpty()) {
                notifyItemDTOsChanged()
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

                _allItemDTOs.value!![0].add(itemDTO)
                _allItemDTOs.value!![categoryIndex].add(itemDTO)
            }

            _allItemDTOs.value!!.map { itemDTOs -> itemDTOs.reverse() }
            notifyItemDTOsChanged()
        }
    }

    fun changeItemDTO(previousItemDTO: ItemDTO, newItemDTO: ItemDTO) {
        val previousCategoryIndex = InitData.getCategoryIndex(previousItemDTO.categoryId!!)
        val newCategoryIndex = InitData.getCategoryIndex(newItemDTO.categoryId!!)

        if (previousCategoryIndex == newCategoryIndex) {
            var itemIndex = -1
            _allItemDTOs.value!![previousCategoryIndex].forEachIndexed { index, itemDTO ->
                if (itemDTO.id == previousItemDTO.id) {
                    itemIndex = index
                    return@forEachIndexed
                }
            }

            println("itemIndex = $itemIndex, ${_allItemDTOs.value!![previousCategoryIndex].indexOf(previousItemDTO)}")
            if (itemIndex == -1) return

            // itemIndex == -1 일 때 예외 처리 필요 (그럴 일은 없겠지만?)
            _allItemDTOs.value!![previousCategoryIndex][itemIndex] = newItemDTO
            return
        }

        val result = _allItemDTOs.value!![previousCategoryIndex].remove(previousItemDTO)
        println("remove!!! $result")
        _allItemDTOs.value!![newCategoryIndex].add(newItemDTO)
        _allItemDTOs.value!![newCategoryIndex].sortByDescending { itemDTO -> itemDTO.timestamps!![0] }
    }

    fun addItemDTO(itemDTO: ItemDTO) {
        val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

        _allItemDTOs.value!![0].add(0, itemDTO)
        _allItemDTOs.value!![categoryIndex].add(0, itemDTO)

        notifyItemDTOsChanged()
    }

    // 구현 예정
    fun removeItemDTO(itemDTO: ItemDTO) {
        val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

        _allItemDTOs.value!![0].add(itemDTO)
        _allItemDTOs.value!![categoryIndex].add(itemDTO)

        notifyItemDTOsChanged()
    }

    // dibsItems
    private fun notifyDibsItemDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _dibsItemDTOs.value = _dibsItemDTOs.value
        }
    }

    fun getDibsItemDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            _dibsItemDTOs.value!!.clear()

            if (accountDTO.value!!.dibsItems.isNullOrEmpty()) {
                notifyDibsItemDTOsChanged()
                return@launch
            }

            val tasks = accountDTO.value!!.dibsItems!!.map { itemId ->
                db.collection("items").document(itemId).get()
            }

            val result = Tasks.whenAllComplete(tasks).await()
            for (task in result) {
                val doc = task.result as DocumentSnapshot
                val itemDTO = doc.toObject(ItemDTO::class.java)!!

                _dibsItemDTOs.value!!.add(itemDTO)
            }

            _dibsItemDTOs.value!!.reverse()
            notifyDibsItemDTOsChanged()
        }
    }

    fun addDibsItem(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayUnion(itemId)).await()
            val doc = db.collection("items").document(itemId).get().await()
            val itemDTO = doc.toObject(ItemDTO::class.java)!!

            _accountDTO.value!!.dibsItems!!.add(itemId)
            _dibsItemDTOs.value!!.add(0, itemDTO)

            notifyDibsItemDTOsChanged()
        }
    }

    fun removeDibsItem(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayRemove(itemId)).await()
            _accountDTO.value!!.dibsItems!!.remove(itemId)

            _dibsItemDTOs.value!!.forEachIndexed { index, itemDTO ->
                if (itemDTO.id == itemId) {
                    _dibsItemDTOs.value!!.removeAt(index)
                    notifyDibsItemDTOsChanged()

                    return@forEachIndexed
                }
            }
        }
    }

    fun containsDibsItem(itemId: String): Boolean {
        if (_accountDTO.value!!.dibsItems.isNullOrEmpty())
            return false

        return _accountDTO.value!!.dibsItems!!.contains(itemId)
    }
}
