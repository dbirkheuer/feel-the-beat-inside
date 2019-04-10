package br.com.feelthebeatinside.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.services.SpotifyService


class PlayerFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    lateinit var imageTrack: ImageView
    lateinit var textAlbumName: TextView
    lateinit var textArtistName: TextView
    lateinit var buttonPreviousTrack: ImageButton
    lateinit var buttonPlayPauseTrack: ImageButton
    lateinit var buttonNextTrack: ImageButton
    lateinit var seekBarTimeCount: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        mapFields(view)
        return view
    }

    private fun mapFields(view: View) {
        seekBarTimeCount = view.findViewById(R.id.seekBarTimeCount)
        seekBarTimeCount.setOnSeekBarChangeListener(this)

        imageTrack = view.findViewById(R.id.imageTrack)

        textAlbumName = view.findViewById(R.id.textAlbumName)
        textArtistName = view.findViewById(R.id.textArtistName)

        buttonPreviousTrack = view.findViewById(R.id.buttonPreviousTrack)
        buttonPreviousTrack.setOnClickListener {
            SpotifyService.previousTrack()
        }

        buttonPlayPauseTrack = view.findViewById(R.id.buttonPlayPauseTrack)
        buttonPlayPauseTrack.setOnClickListener {
            handlerPlayPauseButton()
        }

        buttonNextTrack = view.findViewById(R.id.buttonNextTrack)
        buttonNextTrack.setOnClickListener {
            SpotifyService.nextTrack()
        }

        SpotifyService.suscribeToChanges {
            SpotifyService.getCurrentTrack {
                if (it.album.name == null || it.artist.name == null) {
                    textAlbumName.setText("propaganda")
                    textArtistName.setText(it.name)
                } else {
                    val minutes = it.duration / 1000 / 60
                    val seconds = it.duration / 1000 % 60
                    val songName = it.artist.name + " - " + it.name
                    val albumName = it.album.name
                    textAlbumName.setText(albumName)
                    textArtistName.setText(songName)
                }

                SpotifyService.getImage(it.imageUri) {
                    imageTrack.setImageBitmap(it)
                }
            }
        }

        SpotifyService.getSongPosition {
            val pos = it
        }
    }

    fun handlerPlayPauseButton() {
        SpotifyService.playingState {
            when (it) {
                SpotifyService.PlayingState.PLAYING -> showPauseButton()
                SpotifyService.PlayingState.STOPPED -> showPlayButton()
                SpotifyService.PlayingState.PAUSED -> showResumeButton()
            }
        }
    }

    fun showPauseButton() {
        buttonPlayPauseTrack.setImageDrawable(resources.getDrawable(R.drawable.ic_play_track))
        SpotifyService.pause()
    }

    fun showPlayButton() {
        buttonPlayPauseTrack.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_track))
        SpotifyService.play("spotify:album:5L8VJO457GXReKVVfRhzyM")
    }

    fun showResumeButton() {
        buttonPlayPauseTrack.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_track))
        SpotifyService.play("spotify:album:5L8VJO457GXReKVVfRhzyM")
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}
