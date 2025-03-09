package com.example.playlismaker.data

import com.example.playlismaker.data.dto.SettingsGetIntValueRequest
import com.example.playlismaker.data.dto.SettingsGetIntValueResponse
import com.example.playlismaker.data.dto.SettingsSetIntValueRequest
import com.example.playlismaker.data.local.SharedPreferencesClient
import com.example.playlismaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val preferencesClient: SharedPreferencesClient
): SettingsRepository {
    override fun setDarkMode(mode: Int) {
        preferencesClient.doRequest(SettingsSetIntValueRequest(DARK_MODE_ID, mode))
    }

    override fun getDarkMode(): Int? {
        val response = preferencesClient.doRequest(SettingsGetIntValueRequest(DARK_MODE_ID))

        return if (response is SettingsGetIntValueResponse) {
            response.results
        } else {
            null
        }
    }

    companion object {
        const val DARK_MODE_ID = "dark_theme"
    }
}