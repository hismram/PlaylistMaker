package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun setDarkMode(mode: Boolean)
    fun getDarkMode(): Boolean?
}