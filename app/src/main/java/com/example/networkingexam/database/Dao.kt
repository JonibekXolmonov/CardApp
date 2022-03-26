package com.example.networkingexam.database

import androidx.room.*
import androidx.room.Dao
import com.example.networkingexam.model.Card

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCard(card: Card)

    @Query("SELECT * FROM cards")
    fun getCards(): List<Card>

    @Query("DELETE FROM cards")
    fun delete()

    @Query("SELECT * FROM cards WHERE isAvailable=0")
    fun getCardsToSaveToServer(): List<Card>
}