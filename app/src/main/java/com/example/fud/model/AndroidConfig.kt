package com.example.fud.model

import com.google.gson.annotations.SerializedName

data class AndroidConfig(
    @SerializedName("direct_boot_ok")
    val directBootOk: Boolean
)
