package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SettingsFragmentViewModel(uid: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    class Factory(private val uid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SettingsFragmentViewModel(uid) as T
        }
    }

    init {
        getAccountDTO(uid)
    }

    private fun getAccountDTO(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val document = db.collection("accounts").document(uid).get().await()

            withContext(Dispatchers.Main) {
                _accountDTO.value = document.toObject(AccountDTO::class.java)!!
            }
        }
    }
}
