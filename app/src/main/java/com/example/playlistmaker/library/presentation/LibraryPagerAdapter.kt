package com.example.playlistmaker.library.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.favorites.ui.FavoritesFragment
import com.example.playlistmaker.library.ui.PlaylistsFragment

class LibraryPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoritesFragment.Companion.newInstance()
            else -> PlaylistsFragment.Companion.newInstance()
        }
    }
}