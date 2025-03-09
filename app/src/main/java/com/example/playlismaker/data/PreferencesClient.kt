package com.example.playlismaker.data

import com.example.playlismaker.data.dto.Response

interface PreferencesClient {
    fun doRequest(dto: Any): Response
}