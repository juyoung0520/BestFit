package com.example.bestfit.model

data class AccountDTO(
    var photo: String? = null,
    var nickname: String? = null,
    var sex: Boolean? = null,
    var birth: Int? = null,
    var height: Int? = null,
    var weight: Int? = null,
    var topId: String? = null,
    var bottomId: String? = null,
    var shoesId: String? = null,
    var message: String? = null,
    var items: ArrayList<String>? = null
)