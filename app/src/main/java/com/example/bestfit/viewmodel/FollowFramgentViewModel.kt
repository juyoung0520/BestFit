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

class FollowFramgentViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _accountDTO = MutableLiveData<AccountDTO>()
    val accountDTO: LiveData<AccountDTO> = _accountDTO

    private val _followerAccountDTOs = MutableLiveData<ArrayList<AccountDTO>>(arrayListOf())
    val followerAccountDTOs: LiveData<ArrayList<AccountDTO>> = _followerAccountDTOs

    private val _followingAccountDTOs = MutableLiveData<ArrayList<AccountDTO>>(arrayListOf())
    val followingAccountDTOs: LiveData<ArrayList<AccountDTO>> = _followingAccountDTOs

    fun setAccountDTO(accountDTO: AccountDTO) {
        _accountDTO.value = accountDTO
    }

    fun getAccountDTO(): AccountDTO {
        return _accountDTO.value!!
    }

    private fun notifyFollowerAccountDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _followerAccountDTOs.value = _followerAccountDTOs.value
        }
    }

    private fun notifyFollowingAccountDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _followingAccountDTOs.value = _followingAccountDTOs.value
        }
    }

    fun getFollowerAccountDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = _accountDTO.value!!.follower!!.map { uid ->
                db.collection("accounts").document(uid).get()
            }

            val result = Tasks.whenAllComplete(tasks).await()
            for (task in result) {
                val doc = task.result as DocumentSnapshot
                val accountDTO = doc.toObject(AccountDTO::class.java)!!

                _followerAccountDTOs.value!!.add(accountDTO)
            }

            notifyFollowerAccountDTOsChanged()
        }
    }

    fun getFollowingAccountDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = _accountDTO.value!!.following!!.map { uid ->
                db.collection("accounts").document(uid).get()
            }

            val result = Tasks.whenAllComplete(tasks).await()
            for (task in result) {
                val doc = task.result as DocumentSnapshot
                val accountDTO = doc.toObject(AccountDTO::class.java)!!

                _followingAccountDTOs.value!!.add(accountDTO)
            }

            notifyFollowingAccountDTOsChanged()
        }
    }

    fun addFollower(accountDTO: AccountDTO) {
        if (_accountDTO.value!!.follower!!.contains(accountDTO.id))
            return

        _followerAccountDTOs.value!!.add(accountDTO)
        _accountDTO.value!!.follower!!.add(accountDTO.id!!)

        notifyFollowerAccountDTOsChanged()
    }

    fun removeFollower(accountDTO: AccountDTO) {
        if (!(_accountDTO.value!!.follower!!.contains(accountDTO.id)))
            return

        println("remove in FollowFragmentViewModel")
        _followerAccountDTOs.value!!.remove(accountDTO)
        _accountDTO.value!!.follower!!.remove(accountDTO.id!!)

        notifyFollowerAccountDTOsChanged()
    }
}
