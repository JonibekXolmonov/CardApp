package com.example.networkingexam.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val cardNumber: String?,
    val cardHolder: String?,
    val expireDate: String?,
    val cvv: Int,
    var isAvailable: Boolean = false
)
