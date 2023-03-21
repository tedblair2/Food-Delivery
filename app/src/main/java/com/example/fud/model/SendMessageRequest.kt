package com.example.fud.model

data class SendMessageRequest(
    val validate_only:Boolean=false,
    val message: Message
)
