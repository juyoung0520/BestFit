package com.example.bestfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailFragmentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _scrollPosition = MutableLiveData<Int>(0)
    val scrollPosition: LiveData<Int> = _scrollPosition

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    fun isInitialized() : Boolean {
        return _isInitialized.value!!
    }

    fun setInitializedState(state: Boolean) {
        _isInitialized.value = state
    }

    fun setScrollPosition(position: Int) {
        _scrollPosition.value = position
    }

    fun getScrollPosition() : Int {
        return _scrollPosition.value!!
    }

    fun getAccountDTO(uid: String) {
        viewModelScope.launch {
            val document = db.collection("accounts").document(uid).get().await()
            _accountDTO.value = document.toObject(AccountDTO::class.java)!!
        }
    }

    fun addDibs(currentUid: String, itemId: String) {
        viewModelScope.launch {
            db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayUnion(itemId)).await()

            val documentRef = db.collection("items").document(itemId)
            db.runTransaction { transaction ->
                val dibs = transaction.get(documentRef).toObject(ItemDTO::class.java)!!.dibs
                transaction.update(documentRef, "dibs",dibs!!+1 )
            }.await()

            getAccountDTO(currentUid)
            _isInitialized.value = true
        }
    }

    fun removeDibs(currentUid: String, itemId: String) {
         viewModelScope.launch {
             db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayRemove(itemId)).await()

             val documentRef = db.collection("items").document(itemId)
             db.runTransaction { transaction ->
                 val dibs = transaction.get(documentRef).toObject(ItemDTO::class.java)!!.dibs
                 transaction.update(documentRef, "dibs",dibs!!-1 )
             }.await()

             getAccountDTO(currentUid)
             _isInitialized.value = true
         }

    }
}
