package com.example.cocktailapp.ui.gallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cocktailapp.models.Cocktail
import com.example.cocktailapp.api.CocktailApi
import com.example.cocktailapp.ui.cocktailDetails.CocktailApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class GalleryViewModel(cocktailTypeParameter : String) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

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

    private fun getCocktailListResponse(cocktailTypeParam : String) {
        //TODO("Check internet connection, this is just to fake the api call failing")
        coroutineScope.launch {
            val getCocktailListDeferred = CocktailApi.retrofitService.getCocktailListAsync(cocktailTypeParam)
            try {
                _status.value = CocktailApiStatus.LOADING
                val resultList = getCocktailListDeferred.await()
                _status.value = CocktailApiStatus.DONE
                if (resultList.drinks?.size!! > 0){
                    _cocktailList.value = resultList.drinks as List<Cocktail>?
                }
            }catch (e : Exception){
                _status.value = CocktailApiStatus.ERROR
                Log.d("lilian", "ERROR getCocktailListResponse " + e.message)
            }
        }
    }

    fun displayCocktailDetailsFromGrid(drinkId : String) {
        _navigateToSelectedDrink.value = drinkId
    }

    fun displayCocktailDetailsCompleteFromGrid(){
        _navigateToSelectedDrink.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}