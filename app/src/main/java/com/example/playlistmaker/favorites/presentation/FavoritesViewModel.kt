package com.example.playlistmaker.favorites.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.favorites.domain.api.FavoritesInteractor
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val favoritesLiveData = MutableLiveData<List<Track>>()

    fun getFavoritesLiveData(): LiveData<List<Track>> = favoritesLiveData

    fun loadFavorites() {
        viewModelScope.launch {
            favoritesInteractor.favoriteTracks().collect { tracks ->
                favoritesLiveData.postValue(tracks)
            }
        }
    }
}