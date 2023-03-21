package com.example.fud.service

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseService:FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val map= hashMapOf<String,Any>()
        map["token"]=token
        Firebase.database.getReference("Users").child(Firebase.auth.currentUser!!.uid).updateChildren(map)
    }
}