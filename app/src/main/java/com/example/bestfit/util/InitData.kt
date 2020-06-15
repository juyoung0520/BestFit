package com.example.bestfit.util

import com.example.bestfit.model.CategoryDTO
import com.google.firebase.firestore.FirebaseFirestore

object InitData {
    private val db = FirebaseFirestore.getInstance()
    val categoryDTOs = arrayListOf<CategoryDTO>()
    val categories = arrayListOf<String>()

    fun initCategory() {
        db.collection("categories").orderBy("index").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (snapshot in task.result!!) {
                    val categoryDTO = snapshot.toObject(CategoryDTO::class.java)
                    categoryDTOs.add(categoryDTO)
                    categories.add(categoryDTO.name!!)
                }
            }
        }
    }
}