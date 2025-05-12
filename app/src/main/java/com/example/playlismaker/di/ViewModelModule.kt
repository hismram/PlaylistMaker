package com.example.playlismaker.di

import com.example.playlismaker.player.presentation.PlayerViewModel
import com.example.playlismaker.search.presentation.SearchViewModel
import com.example.playlismaker.settings.presentation.SettingsViewModel
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
}