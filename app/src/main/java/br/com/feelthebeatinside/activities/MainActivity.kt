package br.com.feelthebeatinside.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.fragments.MainFragment
import br.com.feelthebeatinside.manager.PlaybackManager
import br.com.feelthebeatinside.manager.SearchPager
import com.spotify.sdk.android.player.*
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService
import java.lang.Exception

class MainActivity : AppCompatActivity(), ConnectionStateCallback {

    var authToken: String? = null

    companion object {
        var spotifyService: SpotifyService? = null
        var mPlayer: SpotifyPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            val manager = supportFragmentManager
            manager.beginTransaction().replace(R.id.fragment_container, MainFragment()).commit()

            authToken = intent.getStringExtra(LoginActivity.AUTH_TOKEN)

            onAuthenticationComplete(authToken!!)
        } catch (e: Exception) {
            val error = e.message
        }

    }

    private fun onAuthenticationComplete(auth_token: String) {
        if (mPlayer == null) {
            val playerConfig = Config(this, auth_token, LoginActivity.CLIENT_ID)

            Spotify.getPlayer(playerConfig, this, object : SpotifyPlayer.InitializationObserver {
                override fun onInitialized(spotifyPlayer: SpotifyPlayer) {
                    mPlayer = spotifyPlayer
                    mPlayer!!.addConnectionStateCallback(this@MainActivity)
                    setServiceAPI()
                }

                override fun onError(throwable: Throwable) {
                }
            })
        } else {
            mPlayer!!.login(auth_token)
        }

    }

    private fun setServiceAPI() {
        val api = SpotifyApi()
        api.setAccessToken(authToken)

        spotifyService = api.service
    }

    private fun isLoggedIn(): Boolean {
        return mPlayer != null && mPlayer!!.isLoggedIn()
    }


    override fun onLoggedIn() {
        SearchPager.instance(this).getNewRelease(null)
        SearchPager.instance(this).getMyTopTracks(null)
        SearchPager.instance(this).getFeatured()
    }

    override fun onLoggedOut() {

    }

    override fun onLoginFailed(error: Error) {

    }

    override fun onTemporaryError() {

    }

    override fun onConnectionMessage(message: String) {

    }

    override fun onPause() {
        super.onPause()
        if (mPlayer != null) {
            mPlayer!!.removeConnectionStateCallback(this@MainActivity)
        }
    }

    override fun onDestroy() {
        Spotify.destroyPlayer(this)
        super.onDestroy()
    }


    override fun onBackPressed() {
        super.onBackPressed()

        val playbackManager = PlaybackManager.instance
        playbackManager.setSearchResultFragmentAdded(false)
    }


}
