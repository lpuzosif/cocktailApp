package com.example.cocktailapp.ui.cocktailDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cocktailapp.repository.CocktailRepository

class CocktailDetailsViewModelFactory(private val drinkId : String, private val application: Application, private val repository: CocktailRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailDetailsViewModel::class.java)) {
            return CocktailDetailsViewModel(drinkId, application, repository) as T
        }
        throw IllegalArgumentException("Unknown CocktailDetails ViewModel class")
    }
}