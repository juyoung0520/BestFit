package com.example.bestfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.SizeFormatDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isInitialized = MutableLiveData<Boolean>(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _categoryDTOs = MutableLiveData<ArrayList<CategoryDTO>>(arrayListOf())
    val categoryDTOs: LiveData<ArrayList<CategoryDTO>> = _categoryDTOs

//    private val _brandDTOs = MutableLiveData<ArrayList<BrandDTO>>(arrayListOf())
//    val brandDTOs: LiveData<ArrayList<CategoryDTO>> = _brandDTOs

    private val _sizeFormatDTOs = MutableLiveData<ArrayList<SizeFormatDTO>>(arrayListOf())
    val sizeFormatDTOs: LiveData<ArrayList<SizeFormatDTO>> = _sizeFormatDTOs

    init {
        println("init")
        getCategoryDTO()
        println("fin category")
        getSizeFormatDTO()
        println("fin sizeformat")

        setInitializedState(true)
    }

    fun isInitialized() : Boolean {
        return _isInitialized.value!!
    }

    fun setInitializedState(state: Boolean) {
        _isInitialized.value = state
    }

    fun getCategoryDTO() {
        println("start category")

        viewModelScope.launch(Dispatchers.IO) {
            val snapshots = db.collection("categories").orderBy("index").get().await()
            for (snapshot in snapshots) {
                val categoryDTO = snapshot.toObject(CategoryDTO::class.java)
                _categoryDTOs.value!!.add(categoryDTO)
//                InitData.categories.add(categoryDTO.name!!)
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

    fun getSizeFormatDTO() {
        println("start sizeformat")

        viewModelScope.launch(Dispatchers.IO) {
            val snapshots = db.collection("sizes").orderBy("index").get().await()
            for (snapshot in snapshots) {
                val sizeFormatDTO = snapshot.toObject(SizeFormatDTO::class.java)
                _sizeFormatDTOs.value!!.add(sizeFormatDTO)
            }
            println("real fin sizeformat")
        }
    }

    fun getSizeFormatIndex(sizeFormatId: String): Int {
        println(_sizeFormatDTOs.value!!)
        for (format in _sizeFormatDTOs.value!!) {
            if (sizeFormatId == format.id)
                return format.index!!
        }

        return -1
    }

    fun getSizeIndex(sizeFormatId: String, sizeId: String): Int {
        val sizeIdList = _sizeFormatDTOs.value!![getSizeFormatIndex(sizeFormatId)].listId

        for ((idx, id) in sizeIdList.withIndex()) {
            if (sizeId == id)
                return idx
        }

        return -1
    }

    fun getSizeString(sizeFormatId: String, sizeId: String): String? {
        val sizeIndex = getSizeIndex(sizeFormatId, sizeId)

        if (sizeIndex == -1)
            return null

        return _sizeFormatDTOs.value!![getSizeFormatIndex(sizeFormatId)].list[sizeIndex]
    }
}
