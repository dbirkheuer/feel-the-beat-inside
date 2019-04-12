package br.com.feelthebeatinside.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.VideoView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.manager.ExceptionManager
import br.com.feelthebeatinside.services.SpotifyService
import br.com.feelthebeatinside.services.SpotifyWebApiAndroidService
import br.com.feelthebeatinside.util.Consts
import br.com.feelthebeatinside.util.RequestCode
import com.crashlytics.android.Crashlytics
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import io.fabric.sdk.android.Fabric


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
        Fabric.with(this, Crashlytics())
        mapFields()
    }

    private fun mapFields() {
        videoBackgroung = findViewById(R.id.videoBackgroung)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)

        textCreateHere = findViewById(R.id.textCreateHere)
        textCreateHere.setOnClickListener(mListener)

        textRecoveredHere = findViewById(R.id.textRecoveredHere)
        textRecoveredHere.setOnClickListener(mListener)

        buttonSignIn = findViewById(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener(mListener)

        textLoginWithSpotfy = findViewById(R.id.textLoginWithSpotfy)
        textLoginWithSpotfy.setOnClickListener(mListener)

    }

    override fun onResume() {
        super.onResume()
        playBackgroundVideo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.REQUEST_AUTHENTICATION_SPOTIFY) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra(Consts.NAV_PARAM_AUTH_TOKEN, response.accessToken)
                    startActivity(intent)

                    destroy()
                }
                AuthenticationResponse.Type.ERROR -> {
                    ExceptionManager().showSimpleAtention(this, response.error)
                }

                else -> {
                }
            }
        }
    }

    var mListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.textLoginWithSpotfy -> openLoginWindow()
            R.id.buttonSignIn -> openLoginWindow()
            R.id.textRecoveredHere -> startActivity(Intent(this, RecoverPasswordActivity::class.java))
            R.id.textCreateHere -> startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun playBackgroundVideo() {
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.live_music)
        videoBackgroung.setVideoURI(uri)
        videoBackgroung.start()
    }

    private fun openLoginWindow() {
        SpotifyService.connect(this) {
            val request = SpotifyWebApiAndroidService().getRequest()
            AuthenticationClient.openLoginActivity(this, RequestCode.REQUEST_AUTHENTICATION_SPOTIFY, request)
        }



    }

    fun destroy() {
        this@LoginActivity.finish()
    }
}
