package com.example.playlismaker.sharing.data

import android.content.Intent
import android.net.Uri
import com.example.playlismaker.App
import com.example.playlismaker.sharing.domain.api.ExternalNavigator
import androidx.core.net.toUri
import com.example.playlismaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(val application: App): ExternalNavigator {
    override fun shareLink(uri: String) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            uri
        )
        intent.setType("text/plain")
        application.startActivity(intent)
    }

    override fun openEmail(data: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO)

        intent.setType("text/plain")
        intent.data = Uri.parse("mailto:")
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(Intent.EXTRA_EMAIL, data.to)
        intent.putExtra(Intent.EXTRA_SUBJECT, data.subject)
        intent.putExtra(Intent.EXTRA_TEXT, data.content)

        application.startActivity(intent)
    }

    override fun openLink(uri: String) {
        val uri = uri.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

}