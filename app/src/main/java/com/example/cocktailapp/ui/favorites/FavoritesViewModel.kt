package com.example.cocktailapp.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cocktailapp.models.Cocktail
import com.example.cocktailapp.repository.CocktailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application, val repository: CocktailRepository) : AndroidViewModel(app) {

    var cocktailList = repository.getCocktailsFromDB()

    private val _navigateToSelectedDrink = MutableLiveData<String>()
    val navigateToSelectedDrink: LiveData<String> get() = _navigateToSelectedDrink

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun displayCocktailDetails(drinkId: String) {
        _navigateToSelectedDrink.value = drinkId
    }

    fun displayCocktailDetailsComplete() {
        _navigateToSelectedDrink.value = null
    }

    fun deleteCocktail(cocktail: Cocktail) = viewModelScope.launch {
        repository.delete(cocktail)
    }

    fun saveDeletedCocktail(cocktail: Cocktail) = viewModelScope.launch {
        repository.upsert(cocktail)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}