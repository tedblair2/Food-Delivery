package com.example.fud.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("token")
    val token:String,
    @SerializedName("data")
    val data: Map<String,String>
)
