package com.example.cocktailapp.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cocktailapp.models.Cocktail
import com.example.cocktailapp.repository.CocktailRepository
import com.example.cocktailapp.ui.cocktailDetails.CocktailApiStatus
import com.example.cocktailapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class InternetConnection { HAS_INTERNET_CONNECTION, NO_INTERNET_CONNECTION }

class CocktailListViewModel(
    cocktailTypeParameter: String,
    app: Application,
    private val repository: CocktailRepository
) : AndroidViewModel(app) {

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

    // The internal MutableLiveData String that stores the internet device status
    private val _internetStatus = MutableLiveData<InternetConnection>()
    // The external immutable LiveData for the internet device status
    val internetStatus: LiveData<InternetConnection> get() = _internetStatus

    private val _showClearedSnackBar = MutableLiveData<Boolean>()
    val showClearedSnackBar: LiveData<Boolean> get() = _showClearedSnackBar

    // The current word on the search editText
    private val _word = MutableLiveData<String>()
    val word: LiveData<String> get() = _word

    fun showClearedSnackBarComplete() {
        _showClearedSnackBar.value = false
    }

    private var originalCocktailListSize: Int

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call get, so we can display status immediately
     */
    init {
        getCocktailListResponse(cocktailTypeParameter)
        originalCocktailListSize = _cocktailList.value?.size ?: 0
    }

    private fun getCocktailListResponse(cocktailTypeParam: String) {
        if (Utils.hasInternetConnection()) {
            _internetStatus.value = InternetConnection.HAS_INTERNET_CONNECTION
            coroutineScope.launch {
                //val getCocktailListDeferred = repository.getCocktails(cocktailTypeParam)
                try {
                    _status.value = CocktailApiStatus.LOADING
                    val resultList = repository.getCocktails(cocktailTypeParam)
                    _status.value = CocktailApiStatus.DONE
                    if (resultList.drinks?.size!! > 0) {
                        _cocktailList.value = resultList.drinks as List<Cocktail>?
                    }
                } catch (e: Exception) {
                    _status.value = CocktailApiStatus.ERROR
                    Log.d("lilian", "ERROR getCocktailListResponse " + e.message)
                    //si quito el if pongo _propertyList.value = ArrayList()
                }
            }
        } else {
            _internetStatus.value = InternetConnection.NO_INTERNET_CONNECTION
        }
    }

    fun displayCocktailDetails(drinkId: String) {
        _navigateToSelectedDrink.value = drinkId
    }

    fun displayCocktailDetailsComplete() {
        _navigateToSelectedDrink.value = null
    }

    fun getCocktailByNameWhenButtonIsPressed(name: String) {
        _word.value = name
        searchByCocktailNameInCurrentResponse(name)
    }

    fun getCocktailByNameWhenUsersTypes(name: String) {
        _word.value = ""
        searchByCocktailNameInCurrentResponse(name)
    }

    private fun searchByCocktailNameInCurrentResponse(name: String) {
        cocktailListByGivenNameAux = mutableListOf()
        if (!name.isBlank()) {
            _cocktailList.value?.filter {
                it.cocktailName.contains(name, true)
            }?.let {
                cocktailListByGivenNameAux.addAll(it)
            }
            if (cocktailListByGivenNameAux.isEmpty()) {
                _showClearedSnackBar.value = true
            }
            _cocktailListByGivenName.value = cocktailListByGivenNameAux
        } else if (name.isBlank() || name.isEmpty() && originalCocktailListSize != cocktailListByGivenNameAux.size) {
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


//    private val _drinksList : MutableLiveData<Resource<DrinksListResponse>> = MutableLiveData()
//    val drinksList: MutableLiveData<Resource<DrinksListResponse>> get() = _drinksList
//    private var drinksListResponse : DrinksListResponse? = null
//
//    private suspend fun safeCocktailCall(cocktailTypeParam: String) {
//        //val getCocktailListDeferred = repository.getCocktails(cocktailTypeParam)
//        try {
//            _status.value = CocktailApiStatus.LOADING
//            val resultList = repository.getCocktails(cocktailTypeParam)
//            _status.value = CocktailApiStatus.DONE
//            if (resultList.drinks?.size!! > 0){
//                _cocktailList.value = resultList.drinks as List<Cocktail>?
//            }
//        } catch(t: Throwable) {
//            _status.value = CocktailApiStatus.ERROR
//            when(t) {
//                is IOException -> _drinksList.postValue(Resource.Error("Network Failure"))
//                else -> _drinksList.postValue(Resource.Error( "Something is wrong. Sorry for the inconveniences"))
//            }
//        }
//    }
//    private fun handleDrinksListResponse(response: Response<DrinksListResponse>) : Resource<DrinksListResponse> {
//        if(response.isSuccessful) {
//            response.body()?.let { resultResponse ->
//                drinksListResponse = resultResponse
//                _status.value = CocktailApiStatus.DONE
//                return Resource.Success(drinksListResponse ?: resultResponse)
//            }
//        }
//        return Resource.Error(response.message())
//    }
}