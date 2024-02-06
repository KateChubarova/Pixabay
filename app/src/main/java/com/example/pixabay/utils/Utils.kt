package com.example.pixabay.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.widget.EditText

const val RV_ITEM_WIDTH = 180

fun calculateNoOfColumns(context: Context, dp: Int = RV_ITEM_WIDTH): Int {
    val resources: Resources = context.resources
    val columnWidth =
        (dp * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels.toFloat()
    return (screenWidth / columnWidth).toInt()
}

fun EditText.setSearchText(text: String) {
    setText(text)
    setSelection(this.length())
}





