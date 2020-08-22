package com.example.bestfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.SizeFormatDTO
import com.example.bestfit.util.InitData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DataViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = arrayListOf<Task<QuerySnapshot>>()
            tasks.add(db.collection("categories").orderBy("index").get())
            tasks.add(db.collection("sizes").orderBy("index").get())

            val result = Tasks.whenAllComplete(tasks).await()
            for ((idx, task) in result.withIndex()) {
                val snapshot = task.result as QuerySnapshot
                for (doc in snapshot) {
                    when (idx) {
                        0 -> {
                            val categoryDTO = doc.toObject(CategoryDTO::class.java)
                            InitData.categoryDTOs.add(categoryDTO)
                        }
                        1 -> {
                            val sizeFormatDTO = doc.toObject(SizeFormatDTO::class.java)
                            InitData.sizeFormatDTOs.add(sizeFormatDTO)
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                _isInitialized.value = true
            }
        }
    }
}
