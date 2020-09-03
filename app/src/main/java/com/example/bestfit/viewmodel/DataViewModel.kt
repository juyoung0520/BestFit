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

    companion object {
        const val REMOVE_CANCEL = 0
        const val REMOVE_START = 1
        const val REMOVE_FINISH = 2
    }

    private val _removeState = MutableLiveData(REMOVE_CANCEL)
    val removeState: LiveData<Int> = _removeState

    private val _dibsItemDTOs = MutableLiveData<ArrayList<ItemDTO>>(arrayListOf())
    val dibsItemDTOs: LiveData<ArrayList<ItemDTO>> = _dibsItemDTOs
    val removedDibsItems: MutableMap<String, Int> = mutableMapOf()

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

    // editMode
    fun getRemoveState(): Int {
        return _removeState.value!!
    }

    fun setRemoveState(state: Int) {
        _removeState.value = state
    }

    private fun notifyAllItemDTOsChanged() {
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

    fun setAccountDTO(accountDTO: AccountDTO) {
        _accountDTO.value = accountDTO
    }

    // itemDTOs 로딩할 때 호출
    fun getAllItemDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            _allItemDTOs.value!!.clear()

            for (i in 0 until InitData.categoryDTOs.size)
                _allItemDTOs.value!!.add(arrayListOf())

            val doc = db.collection("accounts").document(currentUid).get().await()
            val accountDTO = doc.toObject(AccountDTO::class.java)

            if (accountDTO == null) {
                withContext(Dispatchers.Main) {
                    _accountDTO.value = AccountDTO()
                }
                return@launch
            } else {
                withContext(Dispatchers.Main) {
                    _accountDTO.value = accountDTO!!
                }
            }

            if (accountDTO!!.items.isNullOrEmpty()) {
                notifyAllItemDTOsChanged()

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
            notifyAllItemDTOsChanged()
        }
    }

    fun changeItemDTO(previousItemDTO: ItemDTO, newItemDTO: ItemDTO) {
        val previousCategoryIndex = InitData.getCategoryIndex(previousItemDTO.categoryId!!)
        val newCategoryIndex = InitData.getCategoryIndex(newItemDTO.categoryId!!)

        var itemIndex = -1
        _allItemDTOs.value!![0].forEachIndexed { index, itemDTO ->
            if (itemDTO.id == previousItemDTO.id) {
                itemIndex = index
                return@forEachIndexed
            }
        }

        _allItemDTOs.value!![0][itemIndex] = newItemDTO

        itemIndex = -1
        _allItemDTOs.value!![previousCategoryIndex].forEachIndexed { index, itemDTO ->
            if (itemDTO.id == previousItemDTO.id) {
                itemIndex = index
                return@forEachIndexed
            }
        }

        if (previousCategoryIndex == newCategoryIndex) {
            _allItemDTOs.value!![previousCategoryIndex][itemIndex] = newItemDTO
            notifyAllItemDTOsChanged()

            return
        }

        val result = _allItemDTOs.value!![previousCategoryIndex].removeAt(itemIndex)
        println("remove!!! $result")
        _allItemDTOs.value!![newCategoryIndex].add(newItemDTO)
        _allItemDTOs.value!![newCategoryIndex].sortByDescending { itemDTO -> itemDTO.timestamps!![0] }

        notifyAllItemDTOsChanged()
    }

    fun addItemDTO(itemDTO: ItemDTO) {
        val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

        _allItemDTOs.value!![0].add(0, itemDTO)
        _allItemDTOs.value!![categoryIndex].add(0, itemDTO)

        notifyAllItemDTOsChanged()
    }

    fun removeItemDTOs(itemIds: ArrayList<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            itemIds.forEach { itemId ->
                val itemDTO = _allItemDTOs.value!![0].first { itemDTO -> itemDTO.id == itemId }
                val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

                _allItemDTOs.value!![0].remove(itemDTO)
                _allItemDTOs.value!![categoryIndex].remove(itemDTO)
            }

            val tasks = itemIds.map { itemId ->
                db.collection("items").document(itemId).delete()
            }

            Tasks.whenAllComplete(tasks).await()
            updateAccountItems(_allItemDTOs.value!![0].map { itemDTO -> itemDTO.id } as ArrayList<String>)

            withContext(Dispatchers.Main) {
                setRemoveState(REMOVE_FINISH)
            }

            notifyAllItemDTOsChanged()
        }
    }

    private fun updateAccountItems(items: ArrayList<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("accounts").document(currentUid).update("items", items).await()
        }
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
                val itemDTO = doc.toObject(ItemDTO::class.java) ?: ItemDTO(id = "removed item", name = "삭제된 아이템입니다.")

                _dibsItemDTOs.value!!.add(itemDTO)
            }

            _dibsItemDTOs.value!!.reverse()
            notifyDibsItemDTOsChanged()
        }
    }

    fun addDibsItem(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val documentRef = db.collection("items").document(itemId)
            var itemDTO: ItemDTO? = null
            var newDibs: Int? = 0

            db.runTransaction { transaction ->
                itemDTO = transaction.get(documentRef).toObject(ItemDTO::class.java)
                newDibs = itemDTO!!.dibs!! + 1
                transaction.update(documentRef, "dibs", newDibs)
            }.await()

            itemDTO!!.dibs = newDibs
            db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayUnion(itemId)).await()
            _accountDTO.value!!.dibsItems!!.add(itemId)

            _dibsItemDTOs.value!!.add(0, itemDTO!!)

            removedDibsItems.remove(itemId)
            notifyDibsItemDTOsChanged()

            if (itemDTO!!.uid == currentUid) {
                var index = _allItemDTOs.value!![0].indexOfFirst { itemDTO -> itemDTO.id == itemId }
                _allItemDTOs.value!![0][index].dibs = newDibs

                val categoryIndex = InitData.getCategoryIndex(itemDTO!!.categoryId!!)
                index = _allItemDTOs.value!![categoryIndex].indexOfFirst { itemDTO -> itemDTO.id == itemId }
                _allItemDTOs.value!![categoryIndex][index].dibs = newDibs
            }
        }
    }

    fun removeDibsItem(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val documentRef = db.collection("items").document(itemId)
            var itemDTO: ItemDTO? = null
            var newDibs: Int? = 0

            db.runTransaction { transaction ->
                itemDTO = transaction.get(documentRef).toObject(ItemDTO::class.java)
                newDibs = itemDTO!!.dibs!! - 1
                transaction.update(documentRef, "dibs", newDibs)
            }.await()

            db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayRemove(itemId)).await()
            _accountDTO.value!!.dibsItems!!.remove(itemId)

            val index = _dibsItemDTOs.value!!.indexOfFirst { itemDTO -> itemDTO.id == itemId }
            _dibsItemDTOs.value!!.removeAt(index)

            removedDibsItems[itemId] = newDibs!!
            notifyDibsItemDTOsChanged()

            if (itemDTO!!.uid == currentUid) {
                var index = _allItemDTOs.value!![0].indexOfFirst { itemDTO -> itemDTO.id == itemId }
                _allItemDTOs.value!![0][index].dibs = newDibs

                val categoryIndex = InitData.getCategoryIndex(itemDTO!!.categoryId!!)
                index = _allItemDTOs.value!![categoryIndex].indexOfFirst { itemDTO -> itemDTO.id == itemId }
                _allItemDTOs.value!![categoryIndex][index].dibs = newDibs
            }
        }
    }

    fun removeDibsItem(itemIds: ArrayList<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            itemIds.forEach { itemId ->
                val documentRef = db.collection("items").document(itemId)
                var itemDTO: ItemDTO? = null
                var newDibs: Int? = 0

                db.runTransaction { transaction ->
                    itemDTO = transaction.get(documentRef).toObject(ItemDTO::class.java)
                    newDibs = itemDTO!!.dibs!! - 1
                    transaction.update(documentRef, "dibs", newDibs)
                }.await()

                val index = _dibsItemDTOs.value!!.indexOfFirst { itemDTO -> itemDTO.id == itemId }
                _dibsItemDTOs.value!!.removeAt(index)

                removedDibsItems[itemId] = newDibs!!

                if (itemDTO!!.uid == currentUid) {
                    var index = _allItemDTOs.value!![0].indexOfFirst { itemDTO -> itemDTO.id == itemId }
                    _allItemDTOs.value!![0][index].dibs = newDibs

                    val categoryIndex = InitData.getCategoryIndex(itemDTO!!.categoryId!!)
                    index = _allItemDTOs.value!![categoryIndex].indexOfFirst { itemDTO -> itemDTO.id == itemId }
                    _allItemDTOs.value!![categoryIndex][index].dibs = newDibs
                }
            }

            _accountDTO.value!!.dibsItems!!.removeAll(itemIds)
            db.collection("accounts").document(currentUid).update("dibsItems", _accountDTO.value!!.dibsItems).await()

            withContext(Dispatchers.Main) {
                setRemoveState(REMOVE_FINISH)
            }

            notifyDibsItemDTOsChanged()
        }
    }

    fun containsDibsItem(itemId: String): Boolean {
        if (_accountDTO.value!!.dibsItems.isNullOrEmpty())
            return false

        return _accountDTO.value!!.dibsItems!!.contains(itemId)
    }
}
