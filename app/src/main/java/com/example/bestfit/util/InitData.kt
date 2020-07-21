package com.example.bestfit.util

import android.util.Size
import android.util.SizeF
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.SizeFormatDTO
import com.google.firebase.firestore.FirebaseFirestore

object InitData {
    private val db = FirebaseFirestore.getInstance()
    var initialization = false
    var initializationCategory = false
    var initializationBrand = false
    var initializationSizeFormat = false
    val categoryDTOs = arrayListOf<CategoryDTO>()
    val categories = arrayListOf<String>()
    val brands = arrayListOf<String>()
    val sizeFormatDTOs = arrayListOf<SizeFormatDTO>()

    fun initData() {
        initCategory()
        initBrand()
        initSizeFormat()
    }

    private fun initCategory() {
        db.collection("categories").orderBy("index").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (snapshot in task.result!!) {
                    val categoryDTO = snapshot.toObject(CategoryDTO::class.java)
                    categoryDTOs.add(categoryDTO)
                    categories.add(categoryDTO.name!!)
                }

                initializationCategory = true
                if (initializationBrand)
                    initialization = true
            }
        }
    }

    private fun initBrand() {
        db.collection("brands").document("brands").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                brands.addAll(task.result!!["list"] as ArrayList<String>)

                initializationBrand = true
                if (initializationCategory)
                    initialization = true
            }
        }
    }

    private fun initSizeFormat() {
        db.collection("sizes").orderBy("index").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (snapshot in task.result!!) {
                    val sizeFormatDTO = snapshot.toObject(SizeFormatDTO::class.java)
                    sizeFormatDTOs.add(sizeFormatDTO)
                }
            }
        }

    }

    fun getCategoryIndex(categoryId: String): Int {
        for (categoryDTO in categoryDTOs) {
            if (categoryId == categoryDTO.id)
                return categoryDTO.index!!
        }

        return -1
    }

    fun getSizeFormatIndex(sizeFormatId: String): Int {
        for (format in sizeFormatDTOs) {
            if (sizeFormatId == format.id)
                return format.index!!
        }

        return -1
    }

    fun getSizeIndex(sizeFormatId: String, sizeId: String): Int {
        val sizeIdList = sizeFormatDTOs[getSizeFormatIndex(sizeFormatId)].listId

        for ((idx, id) in sizeIdList.withIndex()) {
            if (sizeId == id)
                return idx
        }

        return -1
    }

    fun getSizeId(sizeFormatId: String, sizeString: String): String? {
        val sizeList = sizeFormatDTOs[getSizeFormatIndex(sizeFormatId)].list
        val result = sizeList.indexOf(sizeString)

        if (result != -1)
            return sizeFormatDTOs[getSizeFormatIndex(sizeFormatId)].listId[result]

        return null
    }
}