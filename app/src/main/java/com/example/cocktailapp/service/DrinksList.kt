package com.example.cocktailapp.service

import com.squareup.moshi.Json

class DrinksList {
    @Json(name = "drinks")
    private var drinks: List<Cocktail?>? = null

    fun getDrinks(): List<Cocktail?>? {
        return drinks
    }

    fun setDrinks(drinks: List<Cocktail?>?) {
        this.drinks = drinks
    }
}