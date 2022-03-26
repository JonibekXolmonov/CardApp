package com.example.networkingexam

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.networkingexam.database.AppDatabase
import com.example.networkingexam.model.Card
import com.example.networkingexam.networking.ApiClient
import com.example.networkingexam.networking.service.Service
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddCardActivity : AppCompatActivity() {

    private lateinit var tvCardNumber: TextView
    private lateinit var tvCardHolder: TextView
    private lateinit var tvExpireDate: TextView
    private lateinit var edtNumber: EditText
    private lateinit var edtDay: EditText
    private lateinit var edtMonth: EditText
    private lateinit var edtCvv: EditText
    private lateinit var edtHolder: EditText

    private lateinit var ivCancel: ImageView

    private lateinit var btnAdd: MaterialButton

    private lateinit var service: Service
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = Color.parseColor("#393939")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        service = ApiClient.createService(Service::class.java)
        appDatabase = AppDatabase.getInstance(this)

        initViews()
    }

    private fun initViews() {
        tvCardNumber = findViewById(R.id.tvCardNumber)
        tvCardHolder = findViewById(R.id.tvCardHolder)
        tvExpireDate = findViewById(R.id.tvExpireDate)

        ivCancel = findViewById(R.id.ivCancel)

        edtNumber = findViewById(R.id.edtNumber)
        edtDay = findViewById(R.id.edtDay)
        edtMonth = findViewById(R.id.edtMonth)
        edtCvv = findViewById(R.id.edtCvv)
        edtHolder = findViewById(R.id.edtHolder)

        btnAdd = findViewById(R.id.btnAdd)

        ivCancel.setOnClickListener {
            backToStart()
        }

        controlTypingProcesses()

        btnAdd.setOnClickListener {
            if (allFieldsCorrectlyAdded()) {
                val card = Card(
                    cardNumber = edtNumber.text.toString(),
                    cardHolder = edtHolder.text.toString(),
                    expireDate = "${edtDay.text}/${edtMonth.text}",
                    cvv = edtCvv.text.toString().toInt()
                )
                if (isInternetAvailable()) {
                    card.isAvailable = true
                    saveCardToServer(card)
                } else {
                    saveCardToDatabase(card)
                }
            } else {
                showToast("Not correctly filled")
            }
        }

    }

    private fun controlTypingProcesses() {
        writeCardNumberWhileTyping()
        writeCardHolderWhileTyping()
        writeCardExpireDateWhileTyping()
    }

    private fun writeCardExpireDateWhileTyping(
    ) {
        edtDay.addTextChangedListener { day: Editable? ->
            if (!day.isNullOrBlank())
                tvExpireDate.text = day
        }

        edtMonth.addTextChangedListener { month: Editable? ->
            if (!month.isNullOrBlank())
                tvExpireDate.text = "${edtDay.text}/$month"
        }
    }

    private fun writeCardHolderWhileTyping() {
        edtHolder.addTextChangedListener { cardHolder: Editable? ->
            tvCardHolder.text = cardHolder
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveCardToServer(card: Card) {
        service.addCard(card).enqueue(object : Callback<Card> {
            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                saveCardToDatabase(card)
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {
                card.isAvailable = false
                saveCardToDatabase(card)
            }
        })
    }

    private fun saveCardToDatabase(card: Card) {
        appDatabase.cardDao().addCard(card)
        showToast("Successfully added")
        backToStart()
    }

    private fun allFieldsCorrectlyAdded(): Boolean {
        return edtNumber.text.isNotBlank() && edtNumber.text.length == 16 && edtDay.text.isNotBlank() && edtMonth.text.isNotBlank() && edtCvv.text.isNotBlank() && edtHolder.text.isNotBlank()
    }

    private fun writeCardNumberWhileTyping() {
        edtNumber.addTextChangedListener { cardNumber: Editable? ->
            tvCardNumber.text = cardNumber
            if (cardNumber?.length == 16) {
                tvCardNumber.text = tvCardNumber.text.toString().creditCardFormatted
            }
        }
    }

    private fun backToStart() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun isInternetAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return infoMobile!!.isConnected || infoWifi!!.isConnected
    }

    private val String.creditCardFormatted: String
        get() {
            val preparedString = replace(" ", "").trim()
            val result = StringBuilder()
            for (i in preparedString.indices) {
                if (i % 4 == 0 && i != 0) {
                    result.append(" ")
                }
                result.append(preparedString[i])
            }
            return result.toString()
        }
}