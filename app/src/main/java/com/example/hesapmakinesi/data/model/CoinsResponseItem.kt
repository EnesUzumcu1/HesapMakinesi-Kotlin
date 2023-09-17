package com.example.hesapmakinesi.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoinsResponseItem(
    @SerializedName("price")
    val price: String?,
    @SerializedName("symbol")
    val symbol: String?
) : Parcelable