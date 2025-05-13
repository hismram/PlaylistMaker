package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    val application: App,
    val externalNavigator: ExternalNavigator
): SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(application.getString(R.string.course_link))
    }

    override fun openTerms() {
        externalNavigator.openLink(application.getString(R.string.terms_of_use_link))
    }

    override fun writeToSupport() {
        externalNavigator.openEmail(EmailData(
            arrayOf(application.getString(R.string.support_mail)),
            application.getString(R.string.support_subject),
            application.getString(R.string.support_message)
        ))
    }

}