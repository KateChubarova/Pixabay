package com.example.pixabay.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pixabay.R
import com.example.pixabay.ui.fragment.MainFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openMainFragment(savedInstanceState)
    }

    private fun openMainFragment(savedInstanceState: Bundle? = null) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment())
                .commit()
        }
    }
}