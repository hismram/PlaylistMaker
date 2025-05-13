package com.example.playlistmaker.library.ui

import com.example.playlistmaker.library.presentation.LibraryPagerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.library.presentation.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabsMediator: TabLayoutMediator
    private val viewModel: LibraryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pager.adapter = LibraryPagerAdapter(
            supportFragmentManager, lifecycle
        )

        tabsMediator = TabLayoutMediator(binding.tabLayout, binding.pager) {
            tab, position ->
            when (position) {
                0 -> tab.text = application.getString(R.string.favorite_tracks)
                1 -> tab.text = application.getString(R.string.playlists)
            }
        }

        tabsMediator.attach()

        binding.toolbar.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabsMediator.detach()
    }
}