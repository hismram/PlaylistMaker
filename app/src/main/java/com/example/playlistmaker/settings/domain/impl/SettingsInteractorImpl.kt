package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val application: App,
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {
    override fun setDarkMode(value: Boolean) {
        settingsRepository.setDarkMode(value)
        application.switchTheme(value)
    }

    override fun getDarkMode(): Boolean? {
        return settingsRepository.getDarkMode()
    }
}
