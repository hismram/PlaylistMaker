package com.example.playlismaker.data.local

import android.content.SharedPreferences
import com.example.playlismaker.data.PreferencesClient
import com.example.playlismaker.data.dto.Response
import com.example.playlismaker.data.dto.TracksClearHistoryRequest
import com.example.playlismaker.data.dto.TracksHistoryRequest
import com.example.playlismaker.data.dto.TracksHistoryResponse
import com.example.playlismaker.data.dto.TracksSetHistoryRequest
import com.example.playlismaker.data.dto.ResponseCodes
import com.example.playlismaker.data.dto.SettingsGetIntValueRequest
import com.example.playlismaker.data.dto.SettingsGetIntValueResponse
import com.example.playlismaker.data.dto.SettingsSetIntValueRequest

class SharedPreferencesClient(private val sharedPref: SharedPreferences) : PreferencesClient {
    /**
     * Записывает целочисленное значение в хранилище
     */
    private fun setInt(id: String, value: Int) {
        sharedPref.edit().putInt(id, value).apply()
    }

    /**
     * Читает целочисленное значение из хранилища
     */
    private fun getInt(id: String): Int {
        return sharedPref.getInt(id, 0)
    }

    /**
     * Читает строковое значение из хранилища
     */
    private fun getString(id: String): String? {
        return sharedPref.getString(id, null)
    }

    /**
     * Записывает строковое значение в хранилище
     */
    private fun setString(id: String, value: String) {
        sharedPref.edit().putString(id, value).apply()
    }

    /**
     * Обработка запроса
     */
    override fun doRequest(dto: Any): Response {
        return when (dto) {
            is TracksHistoryRequest -> TracksHistoryResponse(getString(dto .id) ?: EMPTY_LIST)
            is TracksClearHistoryRequest -> {
                setString(dto.id, EMPTY_LIST)
                return Response().apply { resultCode = ResponseCodes.OK }
            }
            is TracksSetHistoryRequest -> {
                setString(dto.id, dto.history)
                return Response().apply { resultCode = ResponseCodes.OK }
            }
            is SettingsGetIntValueRequest -> SettingsGetIntValueResponse(getInt(dto.id))
            is SettingsSetIntValueRequest -> {
                setInt(dto.id, dto.value)
                Response().apply { resultCode = ResponseCodes.OK }
            }
            else -> Response().apply { resultCode = ResponseCodes.BAD_REQUEST }
        }
    }

    companion object {
        const val EMPTY_LIST = "[]"
    }
}