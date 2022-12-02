package com.example.hesapmakinesi.data.model


import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("msg")
    val msg: String?
)