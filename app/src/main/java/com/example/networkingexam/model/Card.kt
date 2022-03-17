package com.example.networkingexam.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cards")
data class Card(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val cardNumber: Long,
    val cardHolder: String?,
    val expireDate: String?,
    val cvv: Int,
    var isAvailable: Boolean = false
):Serializable
