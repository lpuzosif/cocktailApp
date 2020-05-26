package com.example.cocktailapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CocktailListViewModelFactory (private val cocktailTypeParameter : String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailListViewModel::class.java)) {
            return CocktailListViewModel(cocktailTypeParameter) as T
        }
        throw IllegalArgumentException("Unknown CocktailList ViewModel class")
    }
}