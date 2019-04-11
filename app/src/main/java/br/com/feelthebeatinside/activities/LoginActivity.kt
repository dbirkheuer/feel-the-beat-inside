package br.com.feelthebeatinside.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.VideoView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.util.RequestCode
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

class LoginActivity : Activity() {

    companion object {
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val CLIENT_ID = "eb3ba7a349c84064b92f289c453c5e3a"
        const val REDIRECT_URI = "br.com.feelthebeatinside://callback"
    }

    val TAG = "Spotify " + LoginActivity::class.java.getSimpleName()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.REQUEST_AUTHENTICATION_SPOTIFY) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra(AUTH_TOKEN, response.accessToken)
                    startActivity(intent)
                    destroy()
                }
                AuthenticationResponse.Type.ERROR -> Log.e(TAG, "Auth error: " + response.error)
                else -> Log.d(TAG, "Auth result: " + response.error.toString())
            }
        }
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

    internal var mListener: View.OnClickListener = View.OnClickListener { view ->
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
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-private", "streaming", "user-top-read", "user-read-recently-played"))
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, RequestCode.REQUEST_AUTHENTICATION_SPOTIFY, request)
    }

    fun destroy() {
        this@LoginActivity.finish()
    }
}
