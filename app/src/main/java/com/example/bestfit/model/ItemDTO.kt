package com.example.bestfit.model

import android.os.Parcel
import android.os.Parcelable

data class ItemDTO(
    var timestamp: Long? = null,
    var categoryId: String? = null,
    var subCategoryId: String? = null,
    var brandId: String? = null,
    var name: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(timestamp)
        parcel.writeString(categoryId)
        parcel.writeString(subCategoryId)
        parcel.writeString(brandId)
        parcel.writeString(name)
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