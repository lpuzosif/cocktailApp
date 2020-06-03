package com.example.cocktailapp.ui.favorites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cocktailapp.repository.CocktailRepository

class FavoritesViewModelFactory(val app: Application, private val repository: CocktailRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown Favorites ViewModel class")
    }
}