package com.example.cocktailapp.ui.cocktailDetails

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailapp.R
import com.example.cocktailapp.models.Cocktail
import com.example.cocktailapp.repository.CocktailRepository
import com.example.cocktailapp.ui.InternetConnection
import com.example.cocktailapp.util.Utils
import kotlinx.coroutines.*
import java.lang.Exception

enum class CocktailApiStatus { LOADING, ERROR, DONE }

enum class ViewVisibilityStatus { VIEW_VISIBLE,  VIEW_INVISIBLE }

class CocktailDetailsViewModel(
    private var drinkId: String,
    app: Application,
    private val repository: CocktailRepository
) : AndroidViewModel(app) {

    private val _cocktail = MutableLiveData<Cocktail>()
    val cocktail: LiveData<Cocktail> get() = _cocktail

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _apiStatus = MutableLiveData<CocktailApiStatus>()
    // The external immutable LiveData for the request status String
    val loadingStatus: LiveData<CocktailApiStatus> get() = _apiStatus

    // The internal MutableLiveData String that stores the internet device status
    private val _internetStatus = MutableLiveData<InternetConnection>()
    // The external immutable LiveData for the internet device status
    val internetStatus: LiveData<InternetConnection> get() = _internetStatus

    private val _viewVisibility = MutableLiveData<ViewVisibilityStatus>()
    val viewVisibility: LiveData<ViewVisibilityStatus> get() = _viewVisibility

    private val _insertedToBDStatus = MutableLiveData<Boolean>()
    val insertedToBDStatus: LiveData<Boolean> get() = _insertedToBDStatus

    private val _deletedFromBDStatus = MutableLiveData<Boolean>()
    val deletedFromBDStatus: LiveData<Boolean> get() = _deletedFromBDStatus

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val priceFormatted = Transformations.map(cocktail) {
        it?.let { app.applicationContext.getString(
            R.string.detailsPrice,
            getPrice(it)
        )}
    }

    private fun getPrice(cocktail: Cocktail): String {
        //I´m using the drink id as a price because the api does not not include this value
        val aux: String = cocktail.cocktailPrice.subSequence(0, 2).toString()
        return aux + "." + cocktail.cocktailPrice.subSequence(2, 4)
    }

    val dateFormatted = Transformations.map(cocktail) {
        it?.let {  app.applicationContext.getString(
            R.string.detailsDate,
            when (it.cocktailDateModified != null) {
                true -> it.cocktailDateModified.substringBefore(" ")
                else -> "Unknown"
            }
        )}
    }

    val ingredientsAndMeasure = Transformations.map(cocktail) {
        it?.let { app.applicationContext.getString(
            R.string.measure_and_ingredient, it.cocktailIngredient1, when (it.haveFirstMeasure) {
                true -> ": " + it.cocktailMeasure1
                else -> ""
            },
            when (it.haveSecondIngredient) {
                true -> "\n" + app.applicationContext.getString(
                    R.string.next_measure_and_ingredient,
                    it.cocktailIngredient2,
                    when (it.haveSecondMeasure) {
                        true -> ": " + it.cocktailMeasure2
                        else -> ""
                    }
                )
                else -> ""
            },
            when (it.haveThirdIngredient) {
                true -> "\n" + app.applicationContext.getString(
                    R.string.next_measure_and_ingredient,
                    it.cocktailIngredient3,
                    when (it.haveThirdMeasure) {
                        true -> ": " + it.cocktailMeasure3
                        else -> ""
                    }
                )
                else -> ""
            }
            ,
            when (it.haveFourthIngredient) {
                true -> "\n" + app.applicationContext.getString(
                    R.string.next_measure_and_ingredient,
                    it.cocktailIngredient4,
                    when (it.haveFourthMeasure) {
                        true -> ": " + it.cocktailMeasure4
                        else -> ""
                    }
                )
                else -> ""
            },
            when (it.haveFiveIngredient) {
                true -> "\n" + app.applicationContext.getString(
                    R.string.next_measure_and_ingredient,
                    it.cocktailIngredient5,
                    when (it.haveFiveMeasure) {
                        true -> ": " + it.cocktailMeasure5
                        else -> ""
                    }
                )
                else -> ""
            }
        )}
    }

    init {
        initializeUI()
    }

    private fun initializeUI() {
        _viewVisibility.value = ViewVisibilityStatus.VIEW_INVISIBLE
        coroutineScope.launch {
            _cocktail.value = getCocktailIfWasSavedToDB(drinkId)
            if (_cocktail.value == null) {
                getCocktailResponse()
            } else {
                _internetStatus.value = null
                _viewVisibility.value = ViewVisibilityStatus.VIEW_VISIBLE
            }
        }
    }

    private fun getCocktailResponse() {
        if (Utils.hasInternetConnection()) {
            _internetStatus.value = InternetConnection.HAS_INTERNET_CONNECTION
            coroutineScope.launch {
                //This list only have 1 element
                try {
                    _apiStatus.value = CocktailApiStatus.LOADING
                    val cocktailResult = repository.getCocktailDetails(drinkId)
                    if (cocktailResult.drinks?.size!! > 0) {
                        _cocktail.value = cocktailResult.drinks!!.toList()[0]
                    }
                    _apiStatus.value = CocktailApiStatus.DONE
                    _viewVisibility.value = ViewVisibilityStatus.VIEW_VISIBLE
                } catch (e: Exception) {
                    _apiStatus.value = CocktailApiStatus.ERROR
                    Log.d("lilian", "ERROR getCocktailResponse in VM " + e.message)
                }
            }
        } else {
            _apiStatus.value = CocktailApiStatus.ERROR
            _internetStatus.value = InternetConnection.NO_INTERNET_CONNECTION
        }
    }

    //To send the email
    fun getCocktailDetailsString(): String {
        return "Cocktail Name: " + cocktail.value!!.cocktailName +
                "\nGlass Type: " + cocktail.value!!.cocktailGlass +
                "\nCocktail Instructions: " + cocktail.value!!.cocktailInstructions + "\n" +
                "Last day were instructions were modified for this Cocktail: " + cocktail.value!!.cocktailDateModified
    }

    fun saveOrDeleteCocktailInFavorites() {
        coroutineScope.launch {
            val cocktailInDB = getCocktailIfWasSavedToDB(drinkId)
            if (cocktailInDB == null) {
                _cocktail.value?.let {
                    repository.upsert(it)
                    _insertedToBDStatus.value = true
                }

            } else {
                _insertedToBDStatus.value = false
                deleteFavoriteCocktailFromDB(cocktailInDB)
            }
        }
    }

    private suspend fun getCocktailIfWasSavedToDB(drinkId: String) : Cocktail? {
        return withContext(Dispatchers.IO) {
                repository.getCocktailById(drinkId)
        }
    }

    private fun deleteFavoriteCocktailFromDB(cocktail: Cocktail) = viewModelScope.launch {
        repository.delete(cocktail)
        _deletedFromBDStatus.value = true
    }

    fun deletedFromBDMessageCompleted() {
        _deletedFromBDStatus.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}