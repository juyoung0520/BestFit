package com.example.bestfit.model

data class CategoryDTO(
    var id: String? = null,
    var index: Int? = null,
    var name: String? = null,
    var sub: ArrayList<String>? = null,
    var subId: ArrayList<String>? = null
)