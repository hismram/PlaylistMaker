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
import com.example.playlismaker.search.domain.model.Track

class SearchViewModel(val tracksInteractor: TracksInteractor): ViewModel() {
    private var searchString: String = ""
    private val trackListLiveData = MutableLiveData<List<Track>>(null)
    private val searchStateLiveData = MutableLiveData<Int>(STATE_LIST)

    init {
        showHistory()
    }

    fun getTrackListLiveData(): LiveData<List<Track>> = trackListLiveData
    fun getSearchStateLiveData(): LiveData<Int> = searchStateLiveData

    fun search(searchString: String) {
        if (this.searchString == searchString) return

        searchStateLiveData.value = STATE_LOADING
        this.searchString = searchString
        tracksInteractor.search(searchString,object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                trackListLiveData.postValue(tracks)
                if (tracks.isNotEmpty()) {
                    searchStateLiveData.postValue(STATE_LIST)
                } else {
                    searchStateLiveData.postValue(STATE_EMPTY)
                }
            }

            override fun error() {
                searchStateLiveData.postValue(STATE_NETWORK_ERROR)
            }

        })
    }

    fun showHistory() {
        searchString = ""
        tracksInteractor.getHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                trackListLiveData.postValue(tracks)
                searchStateLiveData.postValue(STATE_LIST)
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

        const val STATE_LIST = 0
        const val STATE_LOADING = 1
        const val STATE_EMPTY = 2
        const val STATE_NETWORK_ERROR = 3
    }
}