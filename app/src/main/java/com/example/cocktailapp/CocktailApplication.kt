package com.example.cocktailapp

import android.app.Application
import android.content.Context

class CocktailApplication : Application() {

    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
