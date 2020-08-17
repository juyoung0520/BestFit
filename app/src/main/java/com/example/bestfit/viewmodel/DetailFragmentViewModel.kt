package com.example.bestfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestfit.model.AccountDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailFragmentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    fun getAccountDTO(uid: String) {
        viewModelScope.launch {
            val document = db.collection("accounts").document(uid).get().await()
            _accountDTO.value = document.toObject(AccountDTO::class.java)!!
        }
    }

    fun setInitializedState(state: Boolean) {
        _isInitialized.value = state
    }

    fun isInitialized() : Boolean {
        return _isInitialized.value!!
    }
}
