package com.example.fud.model

data class Order(
    val userid:String?=null,
    val address:Address?=null,
    val total:String?=null,
    val orderId:String?=null,
    val status:String?=null,
    val time:Long?=null,
    val cart:ArrayList<Cart2>?=null
)
