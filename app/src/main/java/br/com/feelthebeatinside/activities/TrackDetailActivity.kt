package br.com.feelthebeatinside.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.fragments.SearchResultFragment
import br.com.feelthebeatinside.model.Music
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class TrackDetailActivity : AppCompatActivity() {

    private var detailMusic: Music? = null
    private var mActionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_detail)

        detailMusic = intent.getParcelableExtra(SearchResultFragment.DETAIL_MUSIC)
        mActionBar = supportActionBar
        mActionBar!!.setHomeButtonEnabled(true)
        mActionBar!!.setDisplayHomeAsUpEnabled(true)

        val albumImage = findViewById<ImageView>(R.id.detail_album_image_field)
        val titleView = findViewById<TextView>(R.id.detail_track_title_field)
        val artistView = findViewById<TextView>(R.id.detail_artist_field)
        val albumView = findViewById<TextView>(R.id.detail_album_field)

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
            .into(albumImage)

        var title = detailMusic!!.getTitle()
        var album = detailMusic!!.getAlbum()
        var artist = detailMusic!!.getArtist()

        if (title != null) {
            if (title.length > 40) {
                title = title.substring(0, 40)
                title += "..."
            }
        }

        if (album != null) {
            if (album.length > 40) {
                album = album.substring(0, 40)
                album += "..."
            }
        }

        if (artist != null) {
            if (artist.length > 40) {
                artist = artist.substring(0, 40)
                artist += "..."
            }
        }

        titleView.setText(title)
        artistView.setText(artist)
        albumView.setText(album)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                this@TrackDetailActivity.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
