package com.example.cocktailapp.ui.cocktailDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CocktailDetailsViewModelFactory(private val drinkId : String, private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailDetailsViewModel::class.java)) {
            return CocktailDetailsViewModel(drinkId, application) as T
        }
        throw IllegalArgumentException("Unknown CocktailDetails ViewModel class")
    }
}