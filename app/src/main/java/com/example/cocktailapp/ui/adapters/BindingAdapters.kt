package com.example.cocktailapp.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.cocktailapp.CocktailApplication.Companion.context
import com.example.cocktailapp.R
import com.example.cocktailapp.ui.InternetConnection
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
            //TextView
            linearLayoutView[1].visibility = View.INVISIBLE
        }
        CocktailApiStatus.ERROR -> {
            linearLayoutView.visibility = View.VISIBLE
            //ImageView
            linearLayoutView[0].visibility = View.VISIBLE
            linearLayoutView[0].setBackgroundResource(R.drawable.ic_cloud_off)
            //TextView
            val textView = linearLayoutView[1] as TextView
            textView.visibility = View.VISIBLE
            textView.text = context?.getString(R.string.api_call_failed)
        }
        CocktailApiStatus.DONE -> linearLayoutView.visibility = View.GONE
    }
}

@BindingAdapter("apiStatusView")
fun showApiStatusView(view : View, apiStatus : CocktailApiStatus?) {
    //This is for the details screen and the search view visibility based on the api call status
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

@BindingAdapter("internetStatus")
fun showInternetStatusView(connectionLinearLayoutView : LinearLayout, internetStatus : InternetConnection?) {
    when(internetStatus){
        InternetConnection.HAS_INTERNET_CONNECTION -> {
            connectionLinearLayoutView.visibility = View.INVISIBLE
        }
        InternetConnection.NO_INTERNET_CONNECTION -> {
            connectionLinearLayoutView.visibility = View.VISIBLE
            //ImageView
            connectionLinearLayoutView[0].visibility = View.VISIBLE
            connectionLinearLayoutView[0].setBackgroundResource(R.drawable.ic_signal_cellular_null)
            //TextView
            val textView = connectionLinearLayoutView[1] as TextView
            textView.text = context?.getString(R.string.check_internet)
            textView.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("internetStatusViewVisibility")
fun viewVisibilityWithInternetStatus(view : View, internetStatus : InternetConnection?) {
    //This is for the details screen and the search view visibility based on the device internet status
    when(internetStatus){
        InternetConnection.HAS_INTERNET_CONNECTION -> {
            view.visibility = View.VISIBLE
        }
        InternetConnection.NO_INTERNET_CONNECTION -> {
            view.visibility = View.GONE
        }
    }
}