package com.example.playlismaker.domain.impl

import com.example.playlismaker.domain.api.SettingsInteractor
import com.example.playlismaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun setDarkMode(value: Int) {
        repository.setDarkMode(value)
    }

    override fun getDarkMode(): Int? {
        return repository.getDarkMode()
    }
}