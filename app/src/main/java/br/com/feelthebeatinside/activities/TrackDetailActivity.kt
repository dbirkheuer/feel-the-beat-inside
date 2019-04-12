package br.com.feelthebeatinside.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.manager.ExceptionManager
import br.com.feelthebeatinside.manager.ListManager
import br.com.feelthebeatinside.model.Music
import br.com.feelthebeatinside.services.SpotifyWebApiAndroidService
import br.com.feelthebeatinside.util.Consts
import br.com.feelthebeatinside.util.TimeUtil
import br.com.feelthebeatinside.util.ToastUtil
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class TrackDetailActivity : AppCompatActivity() {

    private var detailMusic: Music? = null

    private lateinit var detail_play_layout: LinearLayout
    private lateinit var detail_add_playlist_layout: LinearLayout
    private lateinit var detail_share_layout: LinearLayout
    private lateinit var detail_radio_layout: LinearLayout
    private lateinit var detail_album_view_layout: LinearLayout
    private lateinit var image_album_image: ImageView
    private lateinit var text_detail_track_title: TextView
    private lateinit var text_detail_artist: TextView
    private lateinit var text_detail_album: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_track_detail)
            mapFields()
        } catch (e: Exception) {
            ExceptionManager().showSimpleAtention(this, e.message.toString())
        }
    }

    private fun mapFields() {
        detailMusic = intent.getParcelableExtra(Consts.NAV_PARAM_DETAIL_MUSIC)
        detail_play_layout = findViewById(R.id.detail_play_layout)
        detail_play_layout.setOnClickListener(mClickListenet)
        detail_add_playlist_layout = findViewById(R.id.detail_add_playlist_layout)
        detail_add_playlist_layout.setOnClickListener(mClickListenet)
        detail_share_layout = findViewById(R.id.detail_share_layout)
        detail_share_layout.setOnClickListener(mClickListenet)
        detail_radio_layout = findViewById(R.id.detail_radio_layout)
        detail_radio_layout.setOnClickListener(mClickListenet)
        detail_album_view_layout = findViewById(R.id.detail_album_view_layout)
        detail_album_view_layout.setOnClickListener(mClickListenet)

        image_album_image = findViewById(R.id.image_album_image)
        text_detail_track_title = findViewById(R.id.text_detail_track_title)
        text_detail_artist = findViewById(R.id.text_detail_artist)
        text_detail_album = findViewById(R.id.text_detail_album)

        Picasso.with(this)
            .load(detailMusic!!.getAlbum_image())
            .transform(object : Transformation {
                override fun transform(source: Bitmap): Bitmap {
                    val copy = source.copy(source.config, true)
                    source.recycle()
                    return copy
                }

                override fun key(): String {
                    return "darken"
                }
            })
            .into(image_album_image)

        val title = replaceMusicDetails(detailMusic!!.getTitle()!!)
        val album = replaceMusicDetails(detailMusic!!.getAlbum()!!)
        val artist = replaceMusicDetails(detailMusic!!.getArtist()!!)
        val duration = TimeUtil.getStringTimeByMilis(detailMusic!!.getDuration())

        val titleDesc = title + "  (" + duration + ")"
        text_detail_track_title.setText(titleDesc)
        text_detail_artist.setText(artist)
        text_detail_album.setText(album)
    }

    private fun replaceMusicDetails(detais: String): String {
        var detaisReturn = detais

        if (detaisReturn.length > 40) {
            detaisReturn = detais.substring(0, 40)
            detaisReturn += "..."
        }

        return detaisReturn
    }

    private val mClickListenet = View.OnClickListener {
        try {
            when (it.id) {
                R.id.detail_play_layout -> {
                    if (SpotifyWebApiAndroidService.mPlayer!!.playbackState.isPlaying) {
                        val album = SpotifyWebApiAndroidService.mPlayer!!.metadata.currentTrack.albumName
                        val title = SpotifyWebApiAndroidService.mPlayer!!.metadata.currentTrack.name
                        val prevMusic = ListManager.instance.findCurrentMusic(title, album)

                        if (prevMusic != null) {
                            prevMusic.setPlaying(false)
                        }
                    }
                    SpotifyWebApiAndroidService.mPlayer!!.playUri(null, detailMusic!!.getUri(), 0, 0)
                }
                R.id.detail_add_playlist_layout -> {
                    ToastUtil.createShortToast(this, getString(R.string.not_prepared))
                }
                R.id.detail_share_layout -> {
                    ToastUtil.createShortToast(this, getString(R.string.not_prepared))
                }
                R.id.detail_radio_layout -> {
                    ToastUtil.createShortToast(this, getString(R.string.not_prepared))
                }
                R.id.detail_album_view_layout -> {
                    ToastUtil.createShortToast(this, getString(R.string.not_prepared))
                }

            }
        } catch (e: Exception) {
            ExceptionManager().showSimpleAtention(this, e.message.toString())
        }

    }
}
