package com.example.bestfit.model

import android.os.Parcel
import android.os.Parcelable

data class AccountDTO(
    var id: String? = null,
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
    var items: ArrayList<String>? = arrayListOf(),
    var dibsItems: ArrayList<String>? = arrayListOf(),
    var follower: ArrayList<String>? = arrayListOf(),
    var following: ArrayList<String>? = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(photo)
        parcel.writeString(nickname)
        parcel.writeValue(sex)
        parcel.writeValue(birth)
        parcel.writeValue(height)
        parcel.writeValue(weight)
        parcel.writeString(topId)
        parcel.writeString(bottomId)
        parcel.writeString(shoesId)
        parcel.writeString(message)
        parcel.writeList(items)
        parcel.writeList(dibsItems)
        parcel.writeList(follower)
        parcel.writeList(following)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountDTO> {
        override fun createFromParcel(parcel: Parcel): AccountDTO {
            return AccountDTO(parcel)
        }

        override fun newArray(size: Int): Array<AccountDTO?> {
            return arrayOfNulls(size)
        }
    }
}