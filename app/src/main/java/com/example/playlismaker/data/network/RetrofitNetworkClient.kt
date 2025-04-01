package com.example.playlismaker.data.network

import com.example.playlismaker.data.NetworkClient
import com.example.playlismaker.data.dto.Response
import com.example.playlismaker.data.dto.ResponseCodes
import com.example.playlismaker.search.data.dto.SearchRequest


class RetrofitNetworkClient(val iTunesService: ITunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return if (dto is SearchRequest) {
            val response = iTunesService.search(dto.expression).execute()
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
        } else {
            Response().apply { resultCode = ResponseCodes.BAD_REQUEST }
        }
    }
}