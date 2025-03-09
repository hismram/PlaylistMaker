package com.example.playlismaker.domain.api

interface SettingsInteractor {
    fun setDarkMode(value: Int)
    fun getDarkMode(): Int?
}