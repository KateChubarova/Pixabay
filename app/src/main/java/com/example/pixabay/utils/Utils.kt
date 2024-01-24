package com.example.pixabay.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics


fun calculateNoOfColumns(context: Context): Int {
    val columnWidth = dpToPx(context, 180) // Set your desired column width in dp
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels.toFloat()
    return (screenWidth / columnWidth).toInt()
}

// Convert dp to pixels
private fun dpToPx(context: Context, dp: Int): Int {
    val resources: Resources = context.resources
    return (dp * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
}





