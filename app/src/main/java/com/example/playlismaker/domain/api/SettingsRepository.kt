package com.example.playlismaker.domain.api

interface SettingsRepository {
    fun setDarkMode(mode: Int)
    fun getDarkMode(): Int?
}