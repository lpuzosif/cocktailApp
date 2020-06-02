package com.example.cocktailapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cocktailapp.models.Cocktail

@Dao
interface CocktailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cocktail : Cocktail) : Long

    @Query("SELECT * FROM cocktails")
    fun getAllCocktails() : LiveData<List<Cocktail>>

    @Delete
    suspend fun delete(cocktail: Cocktail)
}