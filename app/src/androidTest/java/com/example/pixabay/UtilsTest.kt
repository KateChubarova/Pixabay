package com.example.pixabay

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.pixabay.utils.calculateNoOfColumns
import junit.framework.TestCase.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun calculateNoOfColumnsTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockResources = context.resources

        mockResources.displayMetrics.densityDpi = 160
        mockResources.displayMetrics.widthPixels = 1080

        assertEquals(5, calculateNoOfColumns(context, dp = 200))
    }

    @Test
    fun calculateNoOfColumnsTest_2() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockResources = context.resources

        mockResources.displayMetrics.densityDpi = 160
        mockResources.displayMetrics.widthPixels = 1080

        assertEquals(6, calculateNoOfColumns(context, dp = 180))
    }

    @Test
    fun calculateNoOfColumnsTest_3() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockResources = context.resources

        mockResources.displayMetrics.densityDpi = 200
        mockResources.displayMetrics.widthPixels = 2000

        assertEquals(11, calculateNoOfColumns(context, dp = 180))
    }
}
