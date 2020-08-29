package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountFragmentViewModel(uid: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

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

    private fun notifyItemDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _itemDTOs.value = _itemDTOs.value
        }
    }

    private fun getItemDTOs(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _itemDTOs.value!!.clear()

            val doc = db.collection("accounts").document(uid).get().await()
            val accountDTO = doc.toObject(AccountDTO::class.java)!!

            withContext(Dispatchers.Main) {
                _accountDTO.value = accountDTO
            }

            if (accountDTO.items!!.isNullOrEmpty()) {
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

                _itemDTOs.value!!.add(itemDTO)
            }

            _itemDTOs.value!!.reverse()
            notifyItemDTOsChanged()
        }
    }
}
