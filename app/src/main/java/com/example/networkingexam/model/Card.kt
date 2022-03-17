package com.example.networkingexam.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(

    @PrimaryKey
    val id: Int,
    val cardNumber: Long,
    val cardHolder: String,
    val expireDate: String,
    val cvv: Int,
    var isAvailable: Boolean = false
)
