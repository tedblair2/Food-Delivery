package com.example.fud.network

import com.example.fud.model.Message
import com.example.fud.model.SendMessageRequest
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/v1/projects/chatapp-b923b/messages:send")
    fun sendNotification(@Header("Authorization") auth:String, @Body body:SendMessageRequest):Call<Message>

    companion object{
        operator fun invoke():ApiService{
            val URL="https://fcm.googleapis.com"

            val retrofit by lazy{
                Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
            return retrofit
        }
    }
}