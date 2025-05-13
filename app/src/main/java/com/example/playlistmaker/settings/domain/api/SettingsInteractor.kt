package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun setDarkMode(value: Boolean)
    fun getDarkMode(): Boolean?
}