package com.example.playlistmaker.search.domain.model

import com.example.playlistmaker.domain.model.Track

sealed interface ListState {
    data object Loading : ListState
    data object NotFound : ListState
    data object Error : ListState
    data class Loaded(val tracks: List<Track>) : ListState
}