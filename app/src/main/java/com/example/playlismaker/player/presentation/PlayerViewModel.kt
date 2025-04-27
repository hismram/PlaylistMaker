package com.example.playlismaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlismaker.player.domain.api.PlayerInteractor
import com.example.playlismaker.player.domain.model.PlaybackState
import com.example.playlismaker.player.domain.model.PlayerState
import com.example.playlismaker.player.domain.model.TrackData
import com.example.playlismaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    trackId: Int,
    private val playerInteractor: PlayerInteractor
): ViewModel() {
    private lateinit var playerState: PlayerState
    private var playerStateLiveData = MutableLiveData<PlayerState>(null)
    private var timerJob: Job? = null;

    init {
        // Получение данных трека и иницализация плеера
        playerInteractor.loadTrackData(
            trackId,
            object : PlayerInteractor.PlayerConsumer {
                override fun consume(
                    track: Track
                ) {
                    playerState = PlayerState(
                        favorite = false,
                        inLibrary = false,
                        playbackState = PlaybackState.Prepared,
                        timer = TIMER_ZERO,
                        trackData = TrackData(
                            name = track.trackName,
                            artist = track.artistName,
                            album = track.collectionName,
                            genre = track.primaryGenreName,
                            year = track.getReleaseDate(),
                            country = track.country,
                            cover = track.getLargeArtwork(),
                            duraration = track.getTrackTime()
                        )
                    )

                    playerStateLiveData.postValue(playerState)
                }

                override fun error() {}
            },
            onCompleteListener = { pausePlayer(true) }
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel();
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerState.playbackState is PlaybackState.Playing) {
                delay(TIMER_UPDATE_DELAY)
                playerState.timer = playerInteractor.getPlaybackTimer()
                playerStateLiveData.postValue(playerState)
            }
        }
    }

    // Запуск плеера
    private fun startPlayer() {
        playerInteractor.play()
        playerState.playbackState = PlaybackState.Playing
        playerStateLiveData.postValue(playerState)
        startTimer()
    }

    // Остановка плеера
    private fun pausePlayer(resetTimer: Boolean = false) {
        timerJob?.cancel()
        playerInteractor.pause()
        playerState.playbackState = PlaybackState.Paused

        if (resetTimer) {
            playerState.timer = TIMER_ZERO
        }

        playerStateLiveData.postValue(playerState)
    }

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    // Переключение состояния плеера
    fun togglePlay() {
        if (playerState.playbackState is PlaybackState.Paused ||
            playerState.playbackState is PlaybackState.Prepared
        ) {
            startPlayer()
        } else {
            pausePlayer()
        }
    }

    fun toggleFavorite() {
        playerState.favorite = !playerState.favorite
        playerStateLiveData.postValue(playerState)
    }

    fun toggleInLibrary() {
        playerState.inLibrary = !playerState.inLibrary
        playerStateLiveData.postValue(playerState)
    }

    // Освобождает плеер
    fun releaseMediaPlayer() {
        // Останавливем воспроизмедение что-бы избежать обращение к плееру в параллельном потоке
        playerState.playbackState = PlaybackState.Paused
        playerStateLiveData.postValue(playerState)
        playerInteractor.release()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
        private const val TIMER_ZERO = "00:00"
    }
}