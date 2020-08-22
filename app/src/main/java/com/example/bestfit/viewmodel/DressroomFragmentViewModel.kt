package com.example.bestfit.viewmodel

import androidx.lifecycle.*
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

    init {
        getItemDTOs()
    }

    private fun getItemDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            _itemDTOs.value!!.clear()

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
                itemDTO.sortByDescending { itemDTO -> itemDTO.timestamp }

            withContext(Dispatchers.Main) {
                _isInitialized.value = true
            }
        }
    }
}
