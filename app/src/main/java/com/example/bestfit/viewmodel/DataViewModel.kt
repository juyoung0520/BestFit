package com.example.bestfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.SizeFormatDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.util.InitData.setInitState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DataViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    init {
        println("init")
        getCategoryDTO()
        println("fin category")
        getSizeFormatDTO()
        println("fin sizeformat")
    }

    private fun getCategoryDTO() {
        println("start category")

        viewModelScope.launch(Dispatchers.IO) {
            InitData.categoryDTOs.clear()

            val snapshots = db.collection("categories").orderBy("index").get().await()
            for (snapshot in snapshots) {
                val categoryDTO = snapshot.toObject(CategoryDTO::class.java)
                InitData.categoryDTOs.add(categoryDTO)
            }

            withContext(Dispatchers.Main) {
                val isInit = setInitState(InitData.CATEGORY, true)
                if (isInit)
                    _isInitialized.value = true
            }

            println("real fin category")
        }
    }

//    fun getBrandDTO() {
//        viewModelScope.launch {
//            val document = db.collection("brands").document("brands").get().await()
//            brands.addAll(document["list"] as ArrayList<String>)
//        }
//    }

    private fun getSizeFormatDTO() {
        println("start sizeformat")

        viewModelScope.launch(Dispatchers.IO) {
            InitData.sizeFormatDTOs.clear()

            val snapshots = db.collection("sizes").orderBy("index").get().await()
            for (snapshot in snapshots) {
                val sizeFormatDTO = snapshot.toObject(SizeFormatDTO::class.java)
                InitData.sizeFormatDTOs.add(sizeFormatDTO)
            }

            withContext(Dispatchers.Main) {
                val isInit = setInitState(InitData.SIZEFORMAT, true)
                if (isInit)
                    _isInitialized.value = true
            }

            println("real fin sizeformat")
        }
    }
}
