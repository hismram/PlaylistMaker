package com.example.playlismaker.sharing.domain.impl

import com.example.playlismaker.App
import com.example.playlismaker.R
import com.example.playlismaker.sharing.domain.api.ExternalNavigator
import com.example.playlismaker.sharing.domain.api.SharingInteractor
import com.example.playlismaker.sharing.domain.model.EmailData

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