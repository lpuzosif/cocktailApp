package com.example.cocktailapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.cocktailapp.service.Cocktail
import com.example.cocktailapp.service.CocktailApi
import com.example.cocktailapp.ui.cocktailDetails.CocktailApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class CocktailListViewModel(private var cocktailTypeParameter : String) : ViewModel() {

    private val _cocktailList = MutableLiveData<List<Cocktail>>()
    val cocktailList: LiveData<List<Cocktail>> get() = _cocktailList

    private val _navigateToSelectedDrink = MutableLiveData<String>()
    val navigateToSelectedDrink: LiveData<String> get() = _navigateToSelectedDrink

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<CocktailApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<CocktailApiStatus> get() = _status

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call get, so we can display status immediately
     */
    init {
        getCocktailListResponse(cocktailTypeParameter)
    }

    fun getCocktailListResponse(cocktailTypeParam : String) {

        coroutineScope.launch {
            var getCocktailListDeferred = CocktailApi.retrofitService.getCocktailListAsync(cocktailTypeParam)
            try {
                _status.value = CocktailApiStatus.LOADING
                val resultList = getCocktailListDeferred.await()
                _status.value = CocktailApiStatus.DONE
                if (resultList.getDrinks()?.size!! > 0){
                    _cocktailList.value = resultList.getDrinks() as List<Cocktail>?
                    Log.d("lilian", resultList.toString())
                }
            }catch (e : Exception){
                _status.value = CocktailApiStatus.ERROR
                Log.d("lilian", "ERROR getCocktailListResponse " + e.message)
                //si quito el if pongo _propertyList.value = ArrayList()
            }
        }
    }

    fun displayCocktailDetails(drinkId : String) {
        _navigateToSelectedDrink.value = drinkId
    }

    fun displayCocktailDetailsComplete(){
        _navigateToSelectedDrink.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}