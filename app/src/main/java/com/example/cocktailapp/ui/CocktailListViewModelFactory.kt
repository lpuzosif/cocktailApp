package com.example.cocktailapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cocktailapp.repository.CocktailRepository

class CocktailListViewModelFactory (private val cocktailTypeParameter : String, val app: Application, private val repository: CocktailRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailListViewModel::class.java)) {
            return CocktailListViewModel(
                cocktailTypeParameter, app, repository
            ) as T
        }
        throw IllegalArgumentException("Unknown CocktailList ViewModel class")
    }
}