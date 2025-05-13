package com.example.playlistmaker.search.domain.model

sealed interface ListState {
    data object Loading : ListState
    data object NotFound : ListState
    data object Error : ListState
    data class Loaded(val tracks: List<Track>) : ListState
}