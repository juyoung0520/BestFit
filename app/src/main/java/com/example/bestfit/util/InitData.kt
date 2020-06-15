package com.example.bestfit.util

import com.example.bestfit.model.CategoryDTO
import com.google.firebase.firestore.FirebaseFirestore

class InitData {
    private val db = FirebaseFirestore.getInstance()
    var categories = arrayListOf<CategoryDTO>()

    fun initCategory() {
        db.collection("categories").orderBy("index").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (snapshot in task.result!!) {
                    val categoryDTO = snapshot.toObject(CategoryDTO::class.java)
                    categories.add(categoryDTO)
                }
            }
        }
    }
}