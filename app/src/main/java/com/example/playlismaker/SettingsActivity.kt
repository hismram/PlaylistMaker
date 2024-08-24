package com.example.playlismaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)
        val shareBtn = findViewById<TextView>(R.id.share)
        val writeToSupportBtn = findViewById<TextView>(R.id.write_to_support)
        val termsOfUseBtn = findViewById<TextView>(R.id.terms_of_use)


        themeSwitcher.isChecked = (applicationContext as App).darkTheme
        toolbar.setOnClickListener { finish() }
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            changeTheme(checked)
        }
        shareBtn.setOnClickListener { share() }
        writeToSupportBtn.setOnClickListener { writeToSupport() }
        termsOfUseBtn.setOnClickListener { termsOfUse() }
    }

    private fun share() {
        val intent = Intent(Intent.ACTION_SEND)

        intent.putExtra(
            Intent.EXTRA_TEXT,
            this.getString(R.string.course_link)
        )
        intent.setType("text/plain")


        startActivity(intent)
    }

    private fun changeTheme(checked: Boolean) {
        (applicationContext as App).switchTheme(checked)
    }

    private fun writeToSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)

        intent.setType("text/plain")
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.support_subject))
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(this.getString(R.string.support_mail)))
        intent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.support_message))

        startActivity(intent)
    }

    private fun termsOfUse() {
        val uri = Uri.parse(this.getString(R.string.terms_of_use_link))
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}