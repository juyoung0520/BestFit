package com.example.bestfit.model

import android.os.Parcel
import android.os.Parcelable

data class ItemDTO(
    var timestamp: Long? = null,
    var uid: String? = null,
    var categoryId: String? = null,
    var subCategoryId: String? = null,
    var images: ArrayList<String>? = arrayListOf(),
    var brandId: String? = null,
    var name: String? = null,
    var sizeImage: String? = null,
    var sizeFormatId: String? = null,
    var sizeId: String? = null,
    var sizeReview: Int? = null,
    var ratingReview: Float? = null,
    var review: String? = null,
    var searchKeywords: ArrayList<String> = arrayListOf()

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(timestamp)
        parcel.writeString(uid)
        parcel.writeString(categoryId)
        parcel.writeString(subCategoryId)
        parcel.writeList(images)
        parcel.writeString(brandId)
        parcel.writeString(name)
        parcel.writeString(sizeImage)
        parcel.writeString(sizeFormatId)
        parcel.writeString(sizeId)
        parcel.writeValue(sizeReview)
        parcel.writeValue(ratingReview)
        parcel.writeString(review)
        parcel.writeList(searchKeywords)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDTO> {
        override fun createFromParcel(parcel: Parcel): ItemDTO {
            return ItemDTO(parcel)
        }

        override fun newArray(size: Int): Array<ItemDTO?> {
            return arrayOfNulls(size)
        }
    }

}