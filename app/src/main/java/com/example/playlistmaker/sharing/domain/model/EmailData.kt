package com.example.playlistmaker.sharing.domain.model

class EmailData(
    val to: Array<String>,
    val subject: String,
    val content: String,
)