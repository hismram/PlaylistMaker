package com.example.playlismaker.player.domain.api

interface PlayerRepository {
    fun getTrack(id: Int)
}