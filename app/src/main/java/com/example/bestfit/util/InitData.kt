package com.example.bestfit.util

import com.example.bestfit.model.CategoryDTO
import com.google.firebase.firestore.FirebaseFirestore

object InitData {
    private val db = FirebaseFirestore.getInstance()
    var initialization = false
    val categoryDTOs = arrayListOf<CategoryDTO>()
    val categories = arrayListOf<String>()
    val brands = arrayListOf<String>()

    fun initData() {
        initCategory()
    }

    private fun initCategory() {
        db.collection("categories").orderBy("index").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (snapshot in task.result!!) {
                    val categoryDTO = snapshot.toObject(CategoryDTO::class.java)
                    categoryDTOs.add(categoryDTO)
                    categories.add(categoryDTO.name!!)
                }

                initBrand()
            }
        }
    }

    private fun initBrand() {
        db.collection("brands").document("brands").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                brands.addAll(task.result!!["list"] as ArrayList<String>)
                initialization = true
            }
        }
    }
}