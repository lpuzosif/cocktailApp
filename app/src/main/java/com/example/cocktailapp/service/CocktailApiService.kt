package com.example.cocktailapp.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://the-cocktail-db.p.rapidapi.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface CocktailApiService {
    @Headers("x-rapidapi-key:e84637c1f7msh8fb797e8ec53340p1d13e2jsnc938abb4627c")
    @GET("filter.php")
    fun getCocktailListAsync(@Query("c") alcohol: String): Deferred<DrinksList>

    @Headers("x-rapidapi-key:e84637c1f7msh8fb797e8ec53340p1d13e2jsnc938abb4627c")
    @GET("lookup.php")
    fun getCocktailDetailsAsync(@Query("i") cocktailId: String): Deferred<DrinksList>

}

//Create a public object called CocktailApi to expose the Retrofit service to the rest of the app. Passing in the service API defined
object CocktailApi {
    val retrofitService: CocktailApiService by lazy {
        retrofit.create(CocktailApiService::class.java)
    }
}

