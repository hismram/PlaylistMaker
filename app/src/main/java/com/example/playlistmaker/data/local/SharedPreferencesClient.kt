package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.data.PreferencesClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.ResponseCodes
import com.example.playlistmaker.data.dto.GetIntValueRequest
import com.example.playlistmaker.data.dto.GetIntValueResponse
import com.example.playlistmaker.data.dto.SetIntValueRequest
import androidx.core.content.edit
import com.example.playlistmaker.data.dto.GetBooleanValueRequest
import com.example.playlistmaker.data.dto.GetBooleanValueResponse
import com.example.playlistmaker.data.dto.GetStringValueRequest
import com.example.playlistmaker.data.dto.GetStringValueResponse
import com.example.playlistmaker.data.dto.SetBooleanValueRequest
import com.example.playlistmaker.data.dto.SetStringValueRequest

class SharedPreferencesClient(private val sharedPref: SharedPreferences) : PreferencesClient {
    /**
     * Записывает целочисленное значение в хранилище
     */
    private fun setInt(id: String, value: Int) {
        sharedPref.edit() { putInt(id, value) }
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
     * Записывает логическое значение
     */
    private fun setBoolean(id: String, value: Boolean) {
        sharedPref.edit() { putBoolean(id, value) }
    }

    /**
     * Читает логическое значение
     */
    private fun getBoolean(id: String): Boolean? {
        if (sharedPref.contains(id)) {
            return sharedPref.getBoolean(id, false)
        } else {
            return null
        }
    }

    /**
     * Записывает строковое значение в хранилище
     */
    private fun setString(id: String, value: String) {
        sharedPref.edit() { putString(id, value) }
    }

    /**
     * Обработка запроса
     */
    override fun doRequest(dto: Any): Response {
        return when (dto) {
            is GetStringValueRequest -> GetStringValueResponse(getString(dto.id))
            is SetStringValueRequest -> {
                setString(dto.id, dto.value)
                return Response().apply { resultCode = ResponseCodes.OK }
            }
            is GetIntValueRequest -> GetIntValueResponse(getInt(dto.id))
            is SetIntValueRequest -> {
                setInt(dto.id, dto.value)
                Response().apply { resultCode = ResponseCodes.OK }
            }
            is GetBooleanValueRequest -> GetBooleanValueResponse(getBoolean(dto.id))
            is SetBooleanValueRequest -> {
                setBoolean(dto.id, dto.value)
                Response().apply { resultCode = ResponseCodes.OK }
            }
            else -> Response().apply { resultCode = ResponseCodes.BAD_REQUEST }
        }
    }
}