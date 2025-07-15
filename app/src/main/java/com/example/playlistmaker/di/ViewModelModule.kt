package com.example.playlistmaker.di

import com.example.playlistmaker.favorites.presentation.FavoritesViewModel
import com.example.playlistmaker.library.presentation.LibraryViewModel
import com.example.playlistmaker.library.presentation.PlaylistsViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (trackId: Int) ->
        PlayerViewModel(trackId, get())
    }

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        LibraryViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

    viewModel {
        FavoritesViewModel(get())
    }
}