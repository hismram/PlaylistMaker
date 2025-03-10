package com.example.playlismaker.data
import com.example.playlismaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}