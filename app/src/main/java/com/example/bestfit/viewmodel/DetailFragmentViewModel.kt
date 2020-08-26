package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DetailFragmentViewModel(private val uid: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUid = FirebaseAuth.getInstance().currentUser!!.uid

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _scrollPosition = MutableLiveData<Int>(0)
    val scrollPosition: LiveData<Int> = _scrollPosition

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    private val _dibsItem = MutableLiveData<ArrayList<String>>()
    val dibsItem: LiveData<ArrayList<String>> = _dibsItem

    private val _dibs = MutableLiveData<Int>()
    val dibs: LiveData<Int> = _dibs

    class Factory(private val uid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailFragmentViewModel(uid) as T
        }
    }

    init {
        // 여기다가 dibsitems 가져오는 함수 호출
        getAccountDTO()
    }

    fun setScrollPosition(position: Int) {
        _scrollPosition.value = position
    }

    fun getScrollPosition() : Int {
        return _scrollPosition.value!!
    }

    private fun getAccountDTO() {
        viewModelScope.launch(Dispatchers.IO) {
            val document = db.collection("accounts").document(uid).get().await()
            withContext(Dispatchers.Main) {
                _accountDTO.value = document.toObject(AccountDTO::class.java)!!
            }
        }
    }

    fun getDibsItem() {
        viewModelScope.launch(Dispatchers.IO) {
            val document = db.collection("accounts").document(currentUid).get().await()
            withContext(Dispatchers.Main) {
                _dibsItem.value = document.toObject(AccountDTO::class.java)!!.dibsItems!!
            }
        }
    }

    fun addDibs(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayUnion(itemId)).await()

            val documentRef = db.collection("items").document(itemId)
            var newDibs: Int?= null
            db.runTransaction { transaction ->
                newDibs = transaction.get(documentRef).toObject(ItemDTO::class.java)!!.dibs!!+1
                transaction.update(documentRef, "dibs", newDibs)
            }.await()

            withContext(Dispatchers.Main) {
                _dibs.value = newDibs!!
            }
        }
    }

    fun removeDibs(itemId: String) {
         viewModelScope.launch(Dispatchers.IO) {
             db.collection("accounts").document(currentUid).update("dibsItems", FieldValue.arrayRemove(itemId)).await()

             val documentRef = db.collection("items").document(itemId)
             var newDibs: Int?= null
             db.runTransaction { transaction ->
                newDibs = transaction.get(documentRef).toObject(ItemDTO::class.java)!!.dibs!!-1
                 transaction.update(documentRef, "dibs",newDibs )
             }.await()

             withContext(Dispatchers.Main) {
                 _dibs.value = newDibs!!
             }
         }

    }
}
