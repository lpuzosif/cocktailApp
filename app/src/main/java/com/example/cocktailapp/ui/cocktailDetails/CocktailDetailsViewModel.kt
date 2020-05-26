package com.example.cocktailapp.ui.cocktailDetails

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.cocktailapp.R
import com.example.cocktailapp.service.Cocktail
import com.example.cocktailapp.service.CocktailApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

enum class CocktailApiStatus { LOADING, ERROR, DONE }

class CocktailDetailsViewModel(private var drinkId: String, app: Application) : ViewModel() {

    private val _cocktail = MutableLiveData<Cocktail>()
    val cocktail: LiveData<Cocktail> get() = _cocktail

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<CocktailApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<CocktailApiStatus> get() = _status

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val priceFormatted = Transformations.map(cocktail) {
        app.applicationContext.getString(
            R.string.detailsPrice,
            getPrice(it)
        )
    }

    private fun getPrice(cocktail: Cocktail) : String {
        var aux: String = cocktail.cocktailPrice.subSequence(0, 2).toString()
        return  aux + "."  + cocktail.cocktailPrice.subSequence(2, 4)
    }

    val dateFormatted = Transformations.map(cocktail) {
        app.applicationContext.getString(
            R.string.detailsDate,
            when(it.cocktailDateModified != null) {
                true -> it.cocktailDateModified.substringBefore(" ")
                else -> "Unknown"
            }
        )
    }

    val ingredientsAndMeasure = Transformations.map(cocktail) {
        app.applicationContext.getString(R.string.measure_and_ingredient, it.cocktailIngredient1, when(it.haveFirstMeasure){ true -> ": " + it.cocktailMeasure1 else -> "" },
            when(it.haveSecondIngredient){
                true -> "\n" + app.applicationContext.getString(R.string.next_measure_and_ingredient, it.cocktailIngredient2, when(it.haveSecondMeasure){ true -> ": " + it.cocktailMeasure2 else -> "" })
                else -> ""
            },
            when(it.haveThirdIngredient){
                true -> "\n" + app.applicationContext.getString(R.string.next_measure_and_ingredient, it.cocktailIngredient3, when(it.haveThirdMeasure){ true -> ": " + it.cocktailMeasure3 else -> "" })
                else -> ""
            }
            ,
            when(it.haveFourthIngredient){
                true -> "\n" + app.applicationContext.getString(R.string.next_measure_and_ingredient, it.cocktailIngredient4, when(it.haveFourthMeasure){ true -> ": " + it.cocktailMeasure4 else -> "" })
                else -> ""
            },
            when(it.haveFiveIngredient){
                true -> "\n" + app.applicationContext.getString(R.string.next_measure_and_ingredient, it.cocktailIngredient5, when(it.haveFiveMeasure){ true -> ": " + it.cocktailMeasure5 else -> "" })
                else -> ""
            }
        )
    }

    /** Call get, so we can display status immediately */
    init {
        getCocktailResponse()
    }

    private fun getCocktailResponse() {

        coroutineScope.launch {
            //This list only have 1 element
            var getCocktailListDeferred =
                CocktailApi.retrofitService.getCocktailDetailsAsync(drinkId)
            try {
                _status.value = CocktailApiStatus.LOADING
                val cocktailResult = getCocktailListDeferred.await()
                _status.value = CocktailApiStatus.DONE
                if (cocktailResult.getDrinks()?.size!! > 0) {
                    _cocktail.value = cocktailResult.getDrinks()!![0]
                }
            } catch (e: Exception) {
                _status.value = CocktailApiStatus.ERROR
                Log.d("lilian", "ERROR getCocktailResponse in VM " + e.message)
            }
        }
    }

    //To send the email
    fun getCocktailDetailsString(): String {
        return "Cocktail Name: " + cocktail.value!!.cocktailName + "\n" +
                "Glass Type: " + cocktail.value!!.cocktailGlass + "\n" +
                "Cocktail Instructions: " + cocktail.value!!.cocktailInstructions + "\n" +
                "Last day were instructions were modified for this Cocktail: " + cocktail.value!!.cocktailDateModified
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}