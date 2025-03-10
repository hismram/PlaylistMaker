package com.example.playlismaker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.playlismaker.ui.library.MediaLibrary
import com.example.playlismaker.R
import com.example.playlismaker.ui.search.SearchActivity
import com.example.playlismaker.ui.settings.SettingsActivity
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
            startActivity(Intent(this, MediaLibrary::class.java))
        }

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
