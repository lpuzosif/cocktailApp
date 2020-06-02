package com.example.cocktailapp.repository

import com.example.cocktailapp.api.CocktailApi
import com.example.cocktailapp.db.CocktailDatabase
import com.example.cocktailapp.models.Cocktail

class CocktailRepository(private val cocktailDB: CocktailDatabase) {

    suspend fun getCocktails(cocktailTypeParam: String) =
        CocktailApi.retrofitService.getCocktailListAsync(cocktailTypeParam).await()

    suspend fun getCocktailDetails(cocktailId: String) =
        CocktailApi.retrofitService.getCocktailDetailsAsync(cocktailId).await()

    suspend fun upsert(cocktail : Cocktail) =
        cocktailDB.getCocktailDao().upsert(cocktail)

    fun getCocktailsFromDB() =
        cocktailDB.getCocktailDao().getAllCocktails()

    suspend fun delete(cocktail: Cocktail) =
        cocktailDB.getCocktailDao().delete(cocktail)
}