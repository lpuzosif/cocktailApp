package com.example.cocktailapp

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.cocktailapp.ui.cocktailDetails.CocktailApiStatus

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}

@BindingAdapter("priceInList")
fun reformatPriceInList(textView: TextView, price: String?) {
    var priceFormatted = "$" + price?.subSequence(0, 2).toString() + "."  + price?.subSequence(2, 4).toString()
    textView.text = priceFormatted
}

@BindingAdapter("apiStatus")
fun showApiStatusImage(linearLayoutView : LinearLayout, apiStatus : CocktailApiStatus?) {
    when(apiStatus){
        CocktailApiStatus.LOADING -> {
            linearLayoutView.visibility = View.VISIBLE
            //ImageView
            linearLayoutView[0].visibility = View.VISIBLE
            linearLayoutView[0].setBackgroundResource(R.drawable.loading_internet)
            //linearLayoutView[0].setBackgroundColor(Color.LTGRAY)
            //TextView
            linearLayoutView[1].visibility = View.INVISIBLE
        }
        CocktailApiStatus.ERROR -> {
            linearLayoutView.visibility = View.VISIBLE
            //ImageView
            linearLayoutView[0].visibility = View.VISIBLE
            linearLayoutView[0].setBackgroundResource(R.drawable.ic_signal_cellular_null)
            //TextView
            linearLayoutView[1].visibility = View.VISIBLE
        }
        CocktailApiStatus.DONE -> linearLayoutView.visibility = View.GONE
    }
}

@BindingAdapter("apiStatusView")
fun showApiStatusView(view : View, apiStatus : CocktailApiStatus?) {
    when(apiStatus){
        CocktailApiStatus.LOADING -> {
            view.visibility = View.GONE
        }
        CocktailApiStatus.ERROR -> {
            view.visibility = View.GONE
        }
        CocktailApiStatus.DONE -> view.visibility = View.VISIBLE
    }
}