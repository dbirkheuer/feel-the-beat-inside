package br.com.feelthebeatinside.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.VideoView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.services.SpotifyService

class LoginActivity : Activity() {

    lateinit var textLoginWithSpotfy: TextView
    lateinit var textRecoveredHere: TextView
    lateinit var textCreateHere: TextView
    lateinit var editEmail: EditText
    lateinit var editPassword: EditText
    lateinit var buttonSignIn: Button
    lateinit var videoBackgroung: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mapFields()
    }

    override fun onResume() {
        super.onResume()
        playBackgroundVideo()
    }

    private fun mapFields() {
        videoBackgroung = findViewById(R.id.videoBackgroung)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)

        textCreateHere = findViewById(R.id.textCreateHere)
        textCreateHere.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        textRecoveredHere = findViewById(R.id.textRecoveredHere)
        textRecoveredHere.setOnClickListener {
            val intent = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(intent)
        }

        buttonSignIn = findViewById(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        textLoginWithSpotfy = findViewById(R.id.textLoginWithSpotfy)
        textLoginWithSpotfy.setOnClickListener {
            SpotifyService.connect(this) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playBackgroundVideo() {
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.live_music)
        videoBackgroung.setVideoURI(uri)
        videoBackgroung.start()
    }
}
