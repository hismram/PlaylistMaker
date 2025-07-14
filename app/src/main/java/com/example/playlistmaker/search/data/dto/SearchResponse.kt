package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto

class SearchResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDto>
) : Response()