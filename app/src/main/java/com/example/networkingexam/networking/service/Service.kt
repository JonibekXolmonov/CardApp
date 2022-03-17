package com.example.networkingexam.networking.service

import com.example.networkingexam.model.Card
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

@JvmSuppressWildcards
interface Service {

    @GET("cards")
    fun getCards(): Call<List<Card>>

    @GET("cards")
    fun addCard(@Body card: Card): Call<Card>

}