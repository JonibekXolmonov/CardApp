package com.example.networkingexam

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.networkingexam.adapter.CardAdapter
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
    private lateinit var progressBar: ProgressBar
    private lateinit var rvCards:RecyclerView
    private lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = Color.parseColor("#393939")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = ApiClient.createService(Service::class.java)

        initViews()
    }

    private fun initViews() {
        rvCards = findViewById(R.id.rvCards)
        cardAdapter = CardAdapter()

        getCards()
        refreshAdapter()
    }

    private fun refreshAdapter() {
        rvCards.adapter = cardAdapter
    }

    private fun getCards() {
        service.getCards().enqueue(object :Callback<List<Card>>{
            override fun onResponse(call: Call<List<Card>>, response: Response<List<Card>>) {
                cardAdapter.submitData(response.body()!!)
            }

            override fun onFailure(call: Call<List<Card>>, t: Throwable) {

            }
        })
    }
}