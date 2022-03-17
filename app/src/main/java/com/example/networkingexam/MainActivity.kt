package com.example.networkingexam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.networkingexam.model.Card
import com.example.networkingexam.networking.ApiClient
import com.example.networkingexam.networking.service.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//1- Create card list page
//2- Add credit card page
//3- Mock Api as a backend
//4- In case of Offline, use database

class MainActivity : AppCompatActivity() {

    private lateinit var service: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = ApiClient.createService(Service::class.java)

        getCards()
    }

    private fun getCards() {
        service.getCards().enqueue(object :Callback<List<Card>>{
            override fun onResponse(call: Call<List<Card>>, response: Response<List<Card>>) {

            }

            override fun onFailure(call: Call<List<Card>>, t: Throwable) {

            }

        })
    }
}