package com.example.cocktailapp.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.cocktailapp.models.Cocktail

//To update only the item that have changed if it´s on the screen
class CocktailDiffCallback : DiffUtil.ItemCallback<Cocktail>() {
    override fun areItemsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
        return oldItem.cocktailPrice == newItem.cocktailPrice
    }
}