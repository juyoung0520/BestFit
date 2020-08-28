package com.example.bestfit.viewmodel

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

class DibsFragmentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUid = FirebaseAuth.getInstance().currentUser!!.uid

    private val _itemDTOs = MutableLiveData<ArrayList<ItemDTO>>()
    val itemDTOs: LiveData<ArrayList<ItemDTO>> = _itemDTOs

    init {
        getItemDTOs()
    }

    private fun getItemDTOs() {
        viewModelScope.launch(Dispatchers.IO) {
            _itemDTOs.value!!.clear()

            val document = db.collection("accounts").document(currentUid).get().await()
            val dibsItems = document.toObject(AccountDTO::class.java)!!.dibsItems

            val tasks = dibsItems!!.map { itemId ->
                db.collection("items").document(itemId).get()
            }

            val result = Tasks.whenAllComplete(tasks).await()
            for (task in result.reversed()) {
                val doc = task.result as DocumentSnapshot
                val itemDTO = doc.toObject(ItemDTO::class.java)!!

                _itemDTOs.value!!.add(itemDTO)
            }

            notifyItemDTOsChanged()
        }
    }

    private fun notifyItemDTOsChanged() {
        viewModelScope.launch(Dispatchers.Main) {
            _itemDTOs.value = _itemDTOs.value
        }
    }

}