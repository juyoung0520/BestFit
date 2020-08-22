package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountFragmentViewModel(uid: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    private val _itemDTOs = MutableLiveData<ArrayList<ItemDTO>>(arrayListOf())
    val itemDTOs: LiveData<ArrayList<ItemDTO>> = _itemDTOs

    class Factory(private val uid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AccountFragmentViewModel(uid) as T
        }
    }

    init {
        getItemDTOs(uid)
    }

    private fun getItemDTOs(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _itemDTOs.value!!.clear()

            val document = db.collection("accounts").document(uid).get().await()
            val accountDTO = document.toObject(AccountDTO::class.java)!!

            withContext(Dispatchers.Main) {
                _accountDTO.value = accountDTO
            }

            if (accountDTO.items!!.isEmpty()) {
                _isInitialized.value = true
                return@launch
            }

            for (itemId in accountDTO.items!!) {
                val doc = db.collection("items").document(itemId).get().await()
                val itemDTO = doc.toObject(ItemDTO::class.java)!!

                _itemDTOs.value!!.add(itemDTO)
            }

            _itemDTOs.value!!.sortByDescending { itemDTO -> itemDTO.timestamp }

            withContext(Dispatchers.Main) {
                _isInitialized.value = true
            }
        }
    }
}
