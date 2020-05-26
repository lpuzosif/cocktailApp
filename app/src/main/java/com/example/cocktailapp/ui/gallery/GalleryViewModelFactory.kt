package com.example.cocktailapp.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GalleryViewModelFactory(private val cocktailTypeParameter : String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return GalleryViewModel(cocktailTypeParameter) as T
        }
        throw IllegalArgumentException("Unknown Gallery ViewModel class")
    }
}