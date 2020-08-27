package com.example.bestfit.util

import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.SizeFormatDTO

object InitData {
    val categoryDTOs = arrayListOf<CategoryDTO>()
    val brands = arrayListOf<String>()
    val sizeFormatDTOs = arrayListOf<SizeFormatDTO>()

    fun getCategoryIndex(categoryId: String): Int {
        for (categoryDTO in categoryDTOs) {
            if (categoryId == categoryDTO.id)
                return categoryDTO.index!!
        }

        return -1
    }

    fun getCategoryString(categoryId: String): String? {
        val categoryIndex = getCategoryIndex(categoryId)

        if (categoryIndex == -1)
            return null

        return categoryDTOs[categoryIndex].name
    }

    fun getSubCategoryIndex(categoryId: String, subCategoryId: String) : Int {
        val categoryIndex = getCategoryIndex(categoryId)

        if (categoryIndex == -1)
            return -1

        for ((idx, id) in categoryDTOs[categoryIndex].subId!!.withIndex()) {
            if (subCategoryId == id)
                return idx
        }

        return -1
    }

    fun getSubCategoryString(categoryId: String, subCategoryId: String) : String? {
        val categoryIndex = getCategoryIndex(categoryId)
        val subCategoryIndex = getSubCategoryIndex(categoryId, subCategoryId)

        if (subCategoryIndex == -1)
            return null

        return categoryDTOs[categoryIndex].sub!![subCategoryIndex]
    }

    fun getSizeFormatIndex(sizeFormatId: String): Int {
        for (format in sizeFormatDTOs) {
            if (sizeFormatId == format.id)
                return format.index!!
        }

        return -1
    }

    fun getSizeFormatString(sizeFormatId: String): String? {
        val sizeFormatIndex = getSizeFormatIndex(sizeFormatId)

        if (sizeFormatIndex == -1)
            return null

        return sizeFormatDTOs[sizeFormatIndex].format
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

    fun getSizeString(sizeFormatId: String, sizeId: String): String? {
        if (sizeFormatId == "00")
            return "FREE"

        val sizeIndex = getSizeIndex(sizeFormatId, sizeId)

        if (sizeIndex == -1)
            return null

        return sizeFormatDTOs[getSizeFormatIndex(sizeFormatId)].list[sizeIndex]
    }
}