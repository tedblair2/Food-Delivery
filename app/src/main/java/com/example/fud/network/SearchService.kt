package com.example.fud.network

import com.example.fud.model.Address
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/search")
    fun searchLocation(
        @Query("q") query:String,
        @Query("format") format:String="json",
        @Query("addressdetails") includeAddressDetails: Boolean = false,
        @Query("limit") limit: Int = 10
    ):Call<List<Address>>

    companion object{
        operator fun invoke():SearchService{
            val URL="https://nominatim.openstreetmap.org/"

            val retrofit by lazy {
                Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SearchService::class.java)
            }
            return retrofit
        }
    }
}