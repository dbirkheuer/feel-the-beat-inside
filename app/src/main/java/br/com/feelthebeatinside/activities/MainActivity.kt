package br.com.feelthebeatinside.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.fragments.MainFragment
import br.com.feelthebeatinside.manager.ExceptionManager
import br.com.feelthebeatinside.manager.PlaybackManager
import br.com.feelthebeatinside.manager.SearchPager
import br.com.feelthebeatinside.services.SpotifyWebApiAndroidService
import br.com.feelthebeatinside.util.Consts
import com.spotify.sdk.android.player.ConnectionStateCallback
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Spotify

class MainActivity : AppCompatActivity(), ConnectionStateCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            val manager = supportFragmentManager
            manager.beginTransaction().replace(R.id.fragment_container, MainFragment()).commit()

            SpotifyWebApiAndroidService.acessToken = intent.getStringExtra(Consts.NAV_PARAM_AUTH_TOKEN)

            SpotifyWebApiAndroidService().onAuthenticationComplete(this, this)

        } catch (e: Throwable) {
            ExceptionManager().showSimpleAtention(this@MainActivity, e.message.toString())
        }
    }

    override fun onLoggedIn() {
        SearchPager.instance(this).getNewRelease(null)
        SearchPager.instance(this).getMyTopTracks(null)
        SearchPager.instance(this).getFeatured()
    }

    override fun onLoggedOut() {}

    override fun onLoginFailed(error: Error) {}

    override fun onTemporaryError() {}

    override fun onConnectionMessage(message: String) {}

    override fun onPause() {
        super.onPause()

        if (SpotifyWebApiAndroidService.mPlayer != null)
            SpotifyWebApiAndroidService.mPlayer!!.removeConnectionStateCallback(this@MainActivity)
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
