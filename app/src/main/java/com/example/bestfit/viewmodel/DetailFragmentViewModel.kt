package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DetailFragmentViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private val _scrollPosition = MutableLiveData(0)

    private val _itemDTO = MutableLiveData<ItemDTO>()
    val itemDTO: LiveData<ItemDTO> = _itemDTO

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    fun getScrollPosition() : Int {
        return _scrollPosition.value!!
    }

    fun setScrollPosition(position: Int) {
        _scrollPosition.value = position
    }

    fun setItemDTO(itemDTO: ItemDTO) {
        _itemDTO.value = itemDTO

        if (_itemDTO.value!!.uid != currentUid)
            getAccountDTO(_itemDTO.value!!.uid!!)
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
