package com.example.playlismaker.sharing.domain.api

import com.example.playlismaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openEmail(data: EmailData)
    fun openLink(link: String)
}