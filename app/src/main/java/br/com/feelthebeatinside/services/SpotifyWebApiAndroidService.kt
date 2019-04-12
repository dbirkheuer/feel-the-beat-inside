package br.com.feelthebeatinside.services

import android.content.Context
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.player.*
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService

class SpotifyWebApiAndroidService {

    companion object {
        const val CLIENT_ID = "eb3ba7a349c84064b92f289c453c5e3a"
        const val REDIRECT_URI = "br.com.feelthebeatinside://callback"

        var acessToken: String? = null
        var spotifyService: SpotifyService? = null
        var mPlayer: Player? = null
    }


    fun getRequest(): AuthenticationRequest {
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-private", "streaming", "user-top-read", "user-read-recently-played"))
        return builder.build()
    }

    fun onAuthenticationComplete(context: Context, callback: ConnectionStateCallback) {
        if (mPlayer == null) {

            val playerConfig = Config(context, acessToken, CLIENT_ID)

            mPlayer = Spotify.getPlayer(playerConfig, this, object : SpotifyPlayer.InitializationObserver {
                override fun onInitialized(spotifyPlayer: SpotifyPlayer) {
                    mPlayer = spotifyPlayer
                    mPlayer!!.addConnectionStateCallback(callback)
                    setServiceAPI()
                }

                override fun onError(throwable: Throwable) {
                }
            })
        }

        SpotifyWebApiAndroidService.mPlayer!!.login(acessToken)
    }

    fun setServiceAPI() {
        val api = SpotifyApi()
        api.setAccessToken(acessToken)
        spotifyService = api.service
    }
}