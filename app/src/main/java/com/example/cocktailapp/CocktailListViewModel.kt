package com.example.cocktailapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cocktailapp.service.Cocktail
import com.example.cocktailapp.service.CocktailApi
import com.example.cocktailapp.ui.cocktailDetails.CocktailApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class CocktailListViewModel(cocktailTypeParameter : String) : ViewModel() {

    private val _cocktailList = MutableLiveData<List<Cocktail>>()
    val cocktailList: LiveData<List<Cocktail>> get() = _cocktailList

    private val _cocktailListByGivenName = MutableLiveData<List<Cocktail>>()
    val cocktailListByGivenName: LiveData<List<Cocktail>> get() = _cocktailListByGivenName

    private var cocktailListByGivenNameAux = mutableListOf<Cocktail>()

    private val _navigateToSelectedDrink = MutableLiveData<String>()
    val navigateToSelectedDrink: LiveData<String> get() = _navigateToSelectedDrink

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<CocktailApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<CocktailApiStatus> get() = _status

    private val _showClearedSnackBar = MutableLiveData<Boolean>()
    val showClearedSnackBar : LiveData<Boolean> get() = _showClearedSnackBar

    // The current word on the search editText
    private val _word = MutableLiveData<String>()
    val word : LiveData<String> get() = _word

    fun showClearedSnackBarComplete() {
        _showClearedSnackBar.value = false
    }

    private var originalCocktailListSize : Int

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call get, so we can display status immediately
     */
    init {
        getCocktailListResponse(cocktailTypeParameter)
        originalCocktailListSize = _cocktailList.value?.size ?: 0
    }

    private fun getCocktailListResponse(cocktailTypeParam : String) {

        coroutineScope.launch {
            val getCocktailListDeferred = CocktailApi.retrofitService.getCocktailListAsync(cocktailTypeParam)
            try {
                _status.value = CocktailApiStatus.LOADING
                val resultList = getCocktailListDeferred.await()
                _status.value = CocktailApiStatus.DONE
                if (resultList.getDrinks()?.size!! > 0){
                    _cocktailList.value = resultList.getDrinks() as List<Cocktail>?
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

    fun getCocktailByNameWhenButtonIsPressed(name : String) {
        _word.value = name
        searchByCocktailNameInCurrentResponse(name)
    }

    fun getCocktailByNameWhenUsersTypes(name : String) {
        _word.value = ""
        searchByCocktailNameInCurrentResponse(name)
    }

    private fun searchByCocktailNameInCurrentResponse(name: String) {
        cocktailListByGivenNameAux = mutableListOf<Cocktail>()
        if (!name.isBlank()) {
            var i = 0
            while (i < _cocktailList.value!!.size) {
                val currentCocktail = _cocktailList.value!![i]
                if (currentCocktail.cocktailName.contains(name, true)) {
                    cocktailListByGivenNameAux.add(currentCocktail)
                }
                i++
            }
            if (cocktailListByGivenNameAux.isEmpty()) {
                _showClearedSnackBar.value = true
            }
            _cocktailListByGivenName.value = cocktailListByGivenNameAux
        } else if (name.isBlank() || name.isEmpty() && originalCocktailListSize != cocktailListByGivenNameAux.size) {
            cocktailListByGivenNameAux = mutableListOf<Cocktail>()
            _cocktailList.value?.let {
                //To show the original list again without calling the endpoint
                _cocktailListByGivenName.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}