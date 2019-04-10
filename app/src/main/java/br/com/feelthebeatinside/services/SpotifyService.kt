package br.com.feelthebeatinside.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.internal.SpotifyAppRemoteIsConnectedRule
import com.spotify.android.appremote.internal.SpotifyLocator
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.Track
import kaaes.spotify.webapi.android.SpotifyApi




object SpotifyService {

    enum class PlayingState {
        PAUSED, PLAYING, STOPPED
    }

    private val CLIENT_ID = "eb3ba7a349c84064b92f289c453c5e3a"
    private val REDIRECT_URI = "br.com.feelthebeatinside://callback"

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var connectionParams: ConnectionParams = ConnectionParams.Builder(CLIENT_ID)
        .setRedirectUri(REDIRECT_URI)
        .showAuthView(true)
        .build()

    lateinit var spotify: SpotifyApi
    var spotifyService = spotify.getService()

    fun getToListByArtists(){
        spotifyService.get
    }

    fun connect(context: Context, handler: (connected: Boolean) -> Unit) {
        if (spotifyAppRemote?.isConnected == true) {
            handler(true)
            return
        }
        val connectionListener = object : Connector.ConnectionListener {
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                this@SpotifyService.spotifyAppRemote = spotifyAppRemote
                handler(true)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyService", throwable.message, throwable)
                handler(false)
            }
        }
        SpotifyAppRemote.connect(context, connectionParams, connectionListener)
    }

    fun play(uri: String) {
        spotifyAppRemote?.playerApi?.play(uri)
    }

    fun previousTrack() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    fun nextTrack() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun resume() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun playingState(handler: (PlayingState) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            if (result.track.uri == null) {
                handler(PlayingState.STOPPED)
            } else if (result.isPaused) {
                handler(PlayingState.PAUSED)
            } else {
                handler(PlayingState.PLAYING)
            }
        }
    }

    fun getCurrentTrack(handler: (track: Track) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            handler(result.track)
        }
    }

    fun getSongPosition(handler: (pos: Long) -> Unit) {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { result ->
            handler(result.playbackPosition)
        }
    }

    fun getSongPosition2(handler: (pos: String) -> Unit) {

         val spotify: SpotifyAppRemote



        spotifyAppRemote?.()?.setEventCallback { result ->
            handler(result.)
        }
    }

    fun getImage(imageUri: ImageUri, handler: (Bitmap) -> Unit) {
        spotifyAppRemote?.imagesApi?.getImage(imageUri)?.setResultCallback {
            handler(it)
        }
    }

    fun getCurrentTrackImage(handler: (Bitmap) -> Unit) {
        getCurrentTrack {
            getImage(it.imageUri) {
                handler(it)
            }
        }
    }

    fun suscribeToChanges(handler: (Track) -> Unit) {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            handler(it.track)
        }
    }

}