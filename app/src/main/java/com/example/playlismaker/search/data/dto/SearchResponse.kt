package com.example.playlismaker.search.data.dto

import com.example.playlismaker.data.dto.Response

class SearchResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDto>
) : Response()