package com.example.playlismaker.settings.domain.api

interface SettingsInteractor {
    fun setDarkMode(value: Boolean)
    fun getDarkMode(): Boolean?
}