package br.com.feelthebeatinside.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.widget.ImageView
import android.widget.SeekBar
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.services.SpotifyService

class PlayerActivity : AppCompatActivity() {

    lateinit var imageTrack: ImageView
    lateinit var myActionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        mapFields()

        initializeTrack()
    }

    private fun mapFields() {
        myActionBar = this.supportActionBar!!
        myActionBar.setHomeButtonEnabled(true)
        myActionBar.setDisplayHomeAsUpEnabled(true)
        myActionBar.setTitle("Player")

        imageTrack = findViewById(R.id.imageTrack)
    }



    private fun initializeTrack() {
        SpotifyService.play("spotify:album:5L8VJO457GXReKVVfRhzyM")

        SpotifyService.suscribeToChanges {
            SpotifyService.getImage(it.imageUri) {
                imageTrack.setImageBitmap(it)
            }
        }
    }


}
