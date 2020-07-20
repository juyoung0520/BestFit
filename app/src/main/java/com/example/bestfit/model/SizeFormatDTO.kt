package com.example.bestfit.model

data class SizeFormatDTO(
    var id: String? = null,
    var index: Int? = null,
    var format: String? = null,
    var list: ArrayList<String> = arrayListOf(),
    var listId: ArrayList<String> = arrayListOf()
)