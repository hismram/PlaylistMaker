package com.example.playlismaker.settings.domain.api

interface SettingsRepository {
    fun setDarkMode(mode: Boolean)
    fun getDarkMode(): Boolean?
}