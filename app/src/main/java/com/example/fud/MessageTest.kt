package com.example.fud

import com.google.firebase.messaging.RemoteMessage


class MessageTest {

    private fun sendToToken(token:String){
        val notification= hashMapOf<String,String>()
        notification["title"]="New Order"
        notification["body"]="Order #123849489"
        val builder=RemoteMessage.Builder(token)
            .data["title"]
//        builder.data = notification
//        val msg=builder.build()
//        val response=FirebaseMessaging.getInstance().send(msg)
    }
}