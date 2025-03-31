package com.example.playlismaker.player.domain.api

import com.example.playlismaker.search.domain.model.Track

interface PlayerInteractor {
    fun loadTrackData(trackId: Int, consumer: PlayerConsumer)

    interface PlayerConsumer {
        fun consume(track: Track)
        fun error()
    }
}