package com.example.pixabay.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

const val RV_ITEM_WIDTH = 180

fun calculateNoOfColumns(context: Context): Int {
    val resources: Resources = context.resources
    val columnWidth =
        (RV_ITEM_WIDTH * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels.toFloat()
    return (screenWidth / columnWidth).toInt()
}





