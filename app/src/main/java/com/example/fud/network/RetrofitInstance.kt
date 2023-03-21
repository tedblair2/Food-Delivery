package com.example.fud.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val BASE_URL="https://fcm.googleapis.com/v1/projects/chatapp-b923b/"

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api by lazy{
        retrofit.create(ApiService::class.java)
    }
}