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

        //appDatabase.cardDao().delete()
        initViews()
    }

    private fun initViews() {
        rvCards = findViewById(R.id.rvCards)
        ivAddCard = findViewById(R.id.ivAddCard)
        cardAdapter = CardAdapter()

        ivAddCard.setOnClickListener {
            startCardAddActivity()
        }

        //we use database data to show in ui cause it is much faster
        getCardsFromDatabase()

        //this function saves cards to server whose isAvailable is false
        if (isInternetAvailable()) {
            saveCardsToServer()
        }
    }

    private fun saveCardsToServer() {
        val cards = appDatabase.cardDao().getCardsToSaveToServer()
        if (cards.isNotEmpty())
            savaToServer(0, cards)
    }

    private fun savaToServer(index: Int, cards: List<Card>) {
        var indexOfCard = index
        val card = cards[indexOfCard]
        card.isAvailable = true
        service.addCard(card).enqueue(object : Callback<Card> {
            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                Log.d("TAG", "onResponse: ${response.body()}")
                updateInDatabase(card)

                indexOfCard++
                if (indexOfCard < cards.size) {
                    savaToServer(indexOfCard, cards)
                }
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {

            }
        })
    }

    private val detailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            getCardsFromDatabase()
        }
    }

    private fun getCardsFromDatabase() {
        val cardsInDatabase = appDatabase.cardDao().getCards()
        refreshAdapter(cardsInDatabase)
    }

    private fun updateInDatabase(card: Card) {
        appDatabase.cardDao().addCard(card)
    }

    private fun startCardAddActivity() {
        val intent = Intent(this, AddCardActivity::class.java)
        detailLauncher.launch(intent)
    }

    private fun refreshAdapter(cards: List<Card>) {
        cardAdapter.submitData(cards)
        rvCards.adapter = cardAdapter
    }

    private fun isInternetAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return infoMobile!!.isConnected || infoWifi!!.isConnected
    }
}