package com.example.playlismaker.settings.domain.impl

import com.example.playlismaker.App
import com.example.playlismaker.settings.domain.api.SettingsInteractor
import com.example.playlismaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val application: App,
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {
    override fun setDarkMode(value: Boolean) {
        settingsRepository.setDarkMode(value)
        application.switchTheme(value)
    }

    override fun getDarkMode(): Boolean {
        return settingsRepository.getDarkMode()
    }
}