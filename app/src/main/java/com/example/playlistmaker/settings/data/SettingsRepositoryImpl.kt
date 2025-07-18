package com.example.playlistmaker.settings.data

import com.example.playlistmaker.data.PreferencesClient
import com.example.playlistmaker.data.dto.GetBooleanValueRequest
import com.example.playlistmaker.data.dto.GetBooleanValueResponse
import com.example.playlistmaker.data.dto.SetBooleanValueRequest
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val preferencesClient: PreferencesClient
): SettingsRepository {
    override fun setDarkMode(mode: Boolean) {
        preferencesClient.doRequest(SetBooleanValueRequest(DARK_MODE_ID, mode))
    }

    override fun getDarkMode(): Boolean? {
        var mode: Boolean? = null

        try {
            val result = preferencesClient.doRequest(GetBooleanValueRequest(DARK_MODE_ID))

            if (result is GetBooleanValueResponse) {
                mode = result.results
            }
        } catch (_: Exception) {}

        return mode
    }

    companion object {
        private const val DARK_MODE_ID = "dark_theme"
    }
}