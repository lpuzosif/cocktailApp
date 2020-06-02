package com.example.cocktailapp.models

import com.squareup.moshi.Json

data class DrinksListResponse (
    @Json(name = "drinks")
    var drinks: MutableList<Cocktail?>? = null
)