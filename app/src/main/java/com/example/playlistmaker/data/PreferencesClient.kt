package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.Response

interface PreferencesClient {
    fun doRequest(dto: Any): Response
}