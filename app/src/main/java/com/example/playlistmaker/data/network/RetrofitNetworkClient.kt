package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.ResponseCodes
import com.example.playlistmaker.search.data.dto.SearchRequest

class RetrofitNetworkClient(val iTunesService: ITunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is SearchRequest) {
            val response = iTunesService.search(dto.expression)
            response.apply { resultCode = ResponseCodes.OK }
        } else {
            Response().apply { resultCode = ResponseCodes.BAD_REQUEST }
        }
    }
}