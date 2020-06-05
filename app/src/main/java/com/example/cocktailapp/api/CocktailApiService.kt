package com.example.cocktailapp.api

import com.example.cocktailapp.models.DrinksListResponse
import com.example.cocktailapp.util.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit by lazy {
    //The logging is only for debug purpose, this can be removed
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    Retrofit.Builder()
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .build()
}

interface CocktailApiService {
    @Headers("x-rapidapi-key:e84637c1f7msh8fb797e8ec53340p1d13e2jsnc938abb4627c")
    @GET("filter.php")
    fun getCocktailListAsync(@Query("c") alcohol: String): Deferred<DrinksListResponse>

    @Headers("x-rapidapi-key:e84637c1f7msh8fb797e8ec53340p1d13e2jsnc938abb4627c")
    @GET("lookup.php")
    fun getCocktailDetailsAsync(@Query("i") cocktailId: String): Deferred<DrinksListResponse>

}

//Public object called CocktailApi to expose the Retrofit service to the rest of the app. Passing in the service API defined
object CocktailApi {
    val retrofitService: CocktailApiService by lazy {
        retrofit.create(CocktailApiService::class.java)
    }
}

