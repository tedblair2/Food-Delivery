package com.example.fud.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class Cart(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val productId:String,
    val productName:String,
    val quantity:Int,
    val discount:Int,
    val price:Int
)
