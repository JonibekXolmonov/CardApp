package com.example.networkingexam

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = Color.parseColor("#393939")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
    }
}