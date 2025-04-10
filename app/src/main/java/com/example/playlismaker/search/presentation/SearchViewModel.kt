package com.example.playlismaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlismaker.App
import com.example.playlismaker.creator.Creator
import com.example.playlismaker.search.domain.api.TracksInteractor
import com.example.playlismaker.search.domain.model.ListState
import com.example.playlismaker.search.domain.model.Track

class SearchViewModel(val tracksInteractor: TracksInteractor): ViewModel() {
    private var searchString: String = ""
    private val searchStateLiveData: MutableLiveData<ListState> = MutableLiveData(null)

    init {
        showHistory()
    }

    fun getSearchStateLiveData(): LiveData<ListState> = searchStateLiveData

    fun search(searchString: String) {
        if (this.searchString == searchString) return

        searchStateLiveData.postValue(ListState.Loading)
        this.searchString = searchString
        tracksInteractor.search(searchString,object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                if (tracks.isNotEmpty()) {
                    searchStateLiveData.postValue(ListState.Loaded(tracks))
                } else {
                    searchStateLiveData.postValue(ListState.NotFound)
                }
            }

            override fun error() {
                searchStateLiveData.postValue(ListState.Error)
            }

        })
    }

    fun showHistory() {
        searchString = ""
        tracksInteractor.getHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                searchStateLiveData.postValue(ListState.Loaded(tracks))
            }

            override fun error() {}
        })
    }

    fun addToHistory(track: Track) {
        tracksInteractor.addToHistory(track)
    }

    fun resetHistory() {
        tracksInteractor.clearHistory()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.provideTracksInteractor(this[APPLICATION_KEY] as App)

                SearchViewModel(interactor)
            }
        }
    }
}