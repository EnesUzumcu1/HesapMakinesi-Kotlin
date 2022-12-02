package com.example.hesapmakinesi.data.model


import com.google.gson.annotations.SerializedName

data class CoinsResponseItem(
    @SerializedName("price")
    val price: String?,
    @SerializedName("symbol")
    val symbol: String?
)