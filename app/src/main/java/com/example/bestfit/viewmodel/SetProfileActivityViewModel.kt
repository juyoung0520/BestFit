package com.example.bestfit.viewmodel

import android.accounts.Account
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestfit.model.AccountDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SetProfileActivityViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private val _initialized = SingleLiveEvent<Boolean>()
    val initialized: SingleLiveEvent<Boolean> = _initialized

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: MutableLiveData<AccountDTO> = _accountDTO

    private val _tempAccountDTO = MutableLiveData<AccountDTO>()
    val tempAccountDTO: MutableLiveData<AccountDTO> = _tempAccountDTO

    init {
        _initialized.value = true
    }

    fun getTempAccountDTO(): AccountDTO? {
        return _tempAccountDTO.value
    }

    fun setTempAccountDTO(accountDTO: AccountDTO) {
        _tempAccountDTO.value = accountDTO
    }

    fun submitSetProfile(accountDTO: AccountDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("accounts").document(currentUid).set(accountDTO).await()

            withContext(Dispatchers.Main) {
                _accountDTO.value = accountDTO
            }
        }
    }

    fun submitModifyProfile(accountDTO: AccountDTO) {
        viewModelScope.launch(Dispatchers.IO) {
           val updateData = mutableMapOf<String, Any>()
            updateData["nickname"] = accountDTO.nickname!!
            updateData["sex"] = accountDTO.sex!!
            updateData["birth"] = accountDTO.birth!!
            updateData["height"] = accountDTO.height!!
            updateData["weight"] = accountDTO.weight!!
            updateData["topId"] = accountDTO.topId!!
            updateData["bottomId"] = accountDTO.bottomId!!
            updateData["shoesId"] = accountDTO.shoesId!!
            updateData["message"] = accountDTO.message!!

            db.collection("accounts").document(currentUid).update(updateData).await()

            withContext(Dispatchers.Main) {
                _accountDTO.value = accountDTO
            }

        }
    }
}