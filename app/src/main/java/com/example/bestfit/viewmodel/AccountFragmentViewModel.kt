package com.example.bestfit.viewmodel

import android.accounts.Account
import androidx.lifecycle.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountFragmentViewModel(uid: String) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private val _isExpanded = MutableLiveData(true)

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
        if (uid != currentUid)
            getItemDTOs(uid)
    }

    fun isExpanded(): Boolean {
        return _isExpanded.value!!
    }

    fun setExpanded(isExpanded: Boolean) {
        _isExpanded.value = isExpanded
    }

    private fun notifyItemDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _itemDTOs.value = _itemDTOs.value
        }
    }

    private fun notifyAccountDTOChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _accountDTO.value = _accountDTO.value
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

    fun addFollower(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val docRef = db.collection("accounts").document(uid)

            db.runTransaction { transaction ->
                val accountDTO = transaction.get(docRef).toObject(AccountDTO::class.java)
                accountDTO!!.follower!!.add(currentUid)
                transaction.update(docRef, "follower", accountDTO!!.follower)
            }.await()

            _accountDTO.value!!.follower!!.add(currentUid)
            notifyAccountDTOChanged()
        }
    }

    fun removeFollower(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val docRef = db.collection("accounts").document(uid)

            db.runTransaction { transaction ->
                val accountDTO = transaction.get(docRef).toObject(AccountDTO::class.java)
                accountDTO!!.follower!!.remove(currentUid)
                transaction.update(docRef, "follower", accountDTO!!.follower)
            }.await()

            _accountDTO.value!!.follower!!.remove(currentUid)
            notifyAccountDTOChanged()
        }
    }

}
