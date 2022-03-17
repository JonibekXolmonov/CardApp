package com.example.networkingexam

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.networkingexam.adapter.CardAdapter
import com.example.networkingexam.database.AppDatabase
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
    private lateinit var ivAddCard: ImageView
    private lateinit var rvCards: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = Color.parseColor("#393939")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = ApiClient.createService(Service::class.java)
        appDatabase = AppDatabase.getInstance(this)

        initViews()
    }

    private fun initViews() {
        rvCards = findViewById(R.id.rvCards)
        ivAddCard = findViewById(R.id.ivAddCard)
        cardAdapter = CardAdapter()
        getCards()
        refreshAdapter()

        ivAddCard.setOnClickListener {
            addCard()
        }
    }

    val detailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent? = it.data
            val cardToAdd = data?.getSerializableExtra("card")
            Log.d("TAG", "details: $cardToAdd")
            saveCard(cardToAdd as Card)
        }
    }

    private fun saveCard(card: Card) {
        if (isInternetAvailable()) {
            service.addCard(card).enqueue(object : Callback<Card> {
                override fun onResponse(call: Call<Card>, response: Response<Card>) {
                    card.isAvailable = true
                    card.id = null
                    saveToDatabase(card)
                    cardAdapter.addCard(response.body()!!)
                    Toast.makeText(this@MainActivity, "Card saved", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Card>, t: Throwable) {

                }
            })
        } else {
            card.isAvailable = false
            card.id = null
            saveToDatabase(card)
            cardAdapter.addCard(card)
        }
    }

    private fun saveToDatabase(card: Card) {
        appDatabase.cardDao().addCard(card)
    }

    private fun addCard() {
        val intent = Intent(this, AddCardActivity::class.java)
        detailLauncher.launch(intent)
    }

    private fun refreshAdapter() {
        rvCards.adapter = cardAdapter
    }

    private fun getCards() {
        if (isInternetAvailable()) {

            val unSavedCards: ArrayList<Card> = ArrayList()
            val savedCards = appDatabase.cardDao().getCards()
            savedCards.forEach {
                if (!it.isAvailable) {
                    unSavedCards.add(it)
                }
            }

            Log.d("TAG", "getCards: $unSavedCards")

            unSavedCards.forEach {
                it.isAvailable = true
                appDatabase.cardDao().addCard(it)
            }

            unSavedCards.forEach {
                service.addCard(it).request()
            }

            service.getCards().enqueue(object : Callback<List<Card>> {
                override fun onResponse(call: Call<List<Card>>, response: Response<List<Card>>) {
                    cardAdapter.submitData(response.body()!!)
                }

                override fun onFailure(call: Call<List<Card>>, t: Throwable) {
                }
            })
        } else {
            cardAdapter.submitData(appDatabase.cardDao().getCards())
        }
    }

    private fun isInternetAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return infoMobile!!.isConnected || infoWifi!!.isConnected
    }
}