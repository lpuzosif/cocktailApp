package com.example.cocktailapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "cocktails")
data class Cocktail (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @Json(name = "strDrink") val cocktailName : String,
    @Json(name="strDrinkThumb") val cocktailImgSrcUrl : String,
    @Json(name="idDrink") val cocktailPrice : String,
    @Json(name="strGlass") val cocktailGlass : String?,
    @Json(name="strInstructions") val cocktailInstructions : String?,
    @Json(name="strIngredient1") val cocktailIngredient1 : String?,
    @Json(name="strIngredient2") val cocktailIngredient2 : String?,
    @Json(name="strIngredient3") val cocktailIngredient3 : String?,
    @Json(name="strIngredient4") val cocktailIngredient4 : String?,
    @Json(name="strIngredient5") val cocktailIngredient5 : String?,
    @Json(name="strMeasure1") val cocktailMeasure1 : String?,
    @Json(name="strMeasure2") val cocktailMeasure2 : String?,
    @Json(name="strMeasure3") val cocktailMeasure3 : String?,
    @Json(name="strMeasure4") val cocktailMeasure4 : String?,
    @Json(name="strMeasure5") val cocktailMeasure5 : String?,
    @Json(name="dateModified") val cocktailDateModified : String?
) : Serializable {
    val haveFirstMeasure get() = !cocktailMeasure1.isNullOrEmpty()
    val haveSecondIngredient get() = !cocktailIngredient2.isNullOrEmpty()
    val haveSecondMeasure get() = !cocktailMeasure2.isNullOrEmpty()
    val haveThirdIngredient get() = !cocktailIngredient3.isNullOrEmpty()
    val haveThirdMeasure get() = !cocktailMeasure3.isNullOrEmpty()
    val haveFourthIngredient get() = !cocktailIngredient4.isNullOrEmpty()
    val haveFourthMeasure get() = !cocktailMeasure4.isNullOrEmpty()
    val haveFiveIngredient get() = !cocktailIngredient5.isNullOrEmpty()
    val haveFiveMeasure get() = !cocktailMeasure5.isNullOrEmpty()
}
