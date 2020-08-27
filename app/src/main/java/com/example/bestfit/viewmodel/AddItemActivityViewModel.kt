package com.example.bestfit.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.example.bestfit.model.ItemDTO
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddItemActivityViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _itemDTO = MutableLiveData<ItemDTO>()
    val itemDTO: LiveData<ItemDTO> = _itemDTO

    private val _tempItemDTO = MutableLiveData<ItemDTO>()
    val tempItemDTO: LiveData<ItemDTO> = _tempItemDTO

    init {
//        getAccountDTO()
    }

    fun setTempItemDTO(itemDTO: ItemDTO) {
        _tempItemDTO.value = itemDTO
    }

    fun submitAddItem(itemDTO: ItemDTO, imageUris: ArrayList<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            val doc = db.collection("items").add(itemDTO).await()
            val docId = doc.id

            val tasks = imageUris.mapIndexed { index, uri ->
                storage.reference.child("items").child(docId).child(index.toString())
                    .putFile(uri)
            }

            val tasksResult = Tasks.whenAllComplete(tasks).await()
            val uploadTasks = tasksResult.map { task ->
                val taskSnapshot = task.result as UploadTask.TaskSnapshot
                taskSnapshot.storage.downloadUrl
            }

            val uploadTasksResult = Tasks.whenAllComplete(uploadTasks).await()
            val uris = uploadTasksResult.map { task ->
                task.result.toString()
            }

            // 뭐가 더 빠른지 비교 필요
//            val uris = imageUris.mapIndexed { index, uri ->
//                storage.reference.child("items").child(docId).child(index.toString())
//                    .putFile(uri)
//                    .await()
//                    .storage
//                    .downloadUrl
//                    .await()
//            }

            itemDTO.id = docId
            itemDTO.images = ArrayList(uris)

            db.collection("items").document(docId).update("id", itemDTO.id).await()
            db.collection("items").document(docId).update("images", itemDTO.images).await()
            db.collection("accounts").document(currentUid).update("items", FieldValue.arrayUnion(docId)).await()

            withContext(Dispatchers.Main) {
                _itemDTO.value = itemDTO
            }
        }
    }
}
