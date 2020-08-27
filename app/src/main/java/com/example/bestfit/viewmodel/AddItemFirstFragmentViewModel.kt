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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddItemFirstFragmentViewModel : ViewModel() {

    private val _initialized = SingleLiveEvent<Boolean>()
    val initialized: SingleLiveEvent<Boolean> = _initialized

    init {
        _initialized.value = true
    }
}
