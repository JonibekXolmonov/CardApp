package com.example.networkingexam.model


data class Card(
    val id: Int,
    val cardNumber: Long,
    val cardHolder: String,
    val expireDate: String,
    val cvv: Int
)
