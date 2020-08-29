package com.example.bestfit.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.example.bestfit.model.ItemDTO
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_add_item_fourth.view.*
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddItemActivityViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _initialized = SingleLiveEvent<Boolean>()
    val initialized: SingleLiveEvent<Boolean> = _initialized

    private val _itemDTO = MutableLiveData<ItemDTO>()
    val itemDTO: LiveData<ItemDTO> = _itemDTO

    private val _tempItemDTO = MutableLiveData<ItemDTO>()
    val tempItemDTO: MutableLiveData<ItemDTO> = _tempItemDTO

    init {
        _initialized.value = true
    }

    fun getTempItemDTO(): ItemDTO? {
        return _tempItemDTO.value
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

    fun submitModifyItem(tempItemDTO: ItemDTO) {
        viewModelScope.launch(Dispatchers.IO) {
//            val tasks = imageUris.mapIndexed { index, uri ->
//                storage.reference.child("items").child(docId).child(index.toString())
//                    .putFile(uri)
//            }
//
//            val tasksResult = Tasks.whenAllComplete(tasks).await()
//            val uploadTasks = tasksResult.map { task ->
//                val taskSnapshot = task.result as UploadTask.TaskSnapshot
//                taskSnapshot.storage.downloadUrl
//            }
//
//            val uploadTasksResult = Tasks.whenAllComplete(uploadTasks).await()
//            val uris = uploadTasksResult.map { task ->
//                task.result.toString()
//            }

            // 뭐가 더 빠른지 비교 필요
//            val uris = imageUris.mapIndexed { index, uri ->
//                storage.reference.child("items").child(docId).child(index.toString())
//                    .putFile(uri)
//                    .await()
//                    .storage
//                    .downloadUrl
//                    .await()
//            }

//            itemDTO.images = ArrayList(uris)

            val updateData = mutableMapOf<String, Any>()
            updateData["timestamps"] = tempItemDTO.timestamps!!
            updateData["categoryId"] = tempItemDTO.categoryId!!
            updateData["subCategoryId"] = tempItemDTO.subCategoryId!!
            updateData["name"] = tempItemDTO.name!!
            updateData["sizeFormatId"] = tempItemDTO.sizeFormatId!!
            updateData["sizeId"] = tempItemDTO.sizeId!!
            updateData["sizeReview"] = tempItemDTO.sizeReview!!
            updateData["ratingReview"] = tempItemDTO.ratingReview!!
            updateData["review"] = tempItemDTO.review!!
            updateData["searchKeywords"] = tempItemDTO.searchKeywords

            db.collection("items").document(tempItemDTO.id!!).update(updateData).await()

            withContext(Dispatchers.Main) {
                _itemDTO.value = tempItemDTO
            }
        }
    }
}
