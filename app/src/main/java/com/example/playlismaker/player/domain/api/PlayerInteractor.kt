package com.example.playlismaker.player.domain.api

import com.example.playlismaker.search.domain.model.Track

interface PlayerInteractor {
    fun loadSomeData(): Boolean
    fun loadTrackData(trackId: Int, consumer: PlayerInteractor.PlayerConsumer)

    interface PlayerConsumer {
        fun consume(track: Track)
        fun error()
    }
}