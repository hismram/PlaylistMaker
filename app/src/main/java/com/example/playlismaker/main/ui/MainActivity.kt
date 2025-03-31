package com.example.playlismaker.main.ui

import android.content.Intent
import com.example.playlismaker.search.ui.SearchActivity as SearchActivity
import com.example.playlismaker.library.ui.Activity as LibraryActivity
import com.example.playlismaker.settings.ui.SettingsActivity as SettingsActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.playlismaker.R
import com.google.android.material.button.MaterialButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<MaterialButton>(R.id.search)
        val mediaLib = findViewById<MaterialButton>(R.id.media_lib)
        val settings = findViewById<MaterialButton>(R.id.settings)

        search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        mediaLib.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}