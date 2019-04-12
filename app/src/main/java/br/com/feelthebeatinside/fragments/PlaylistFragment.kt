package br.com.feelthebeatinside.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.activities.TrackDetailActivity
import br.com.feelthebeatinside.manager.ExceptionManager
import br.com.feelthebeatinside.manager.ListManager
import br.com.feelthebeatinside.manager.PlaybackManager
import br.com.feelthebeatinside.manager.SearchPager
import br.com.feelthebeatinside.model.Music
import br.com.feelthebeatinside.services.SpotifyWebApiAndroidService
import br.com.feelthebeatinside.util.Consts
import br.com.feelthebeatinside.util.TimeUtil
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerEvent
import kaaes.spotify.webapi.android.models.Track


class PlaylistFragment : Fragment(), Player.NotificationCallback {
    override fun onPlaybackError(p0: Error?) {

    }

    override fun onPlaybackEvent(p0: PlayerEvent?) {}

    private var toolbar: Toolbar? = null
    private var query: String? = null
    private var background_album: ImageView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: TrackListAdapter? = null
    private var mSearchPager: SearchPager? = null
    private var mSearchListener: SearchPager.onCompleteTopTrackListener? = null
    private var listManager: ListManager? = null
    private var playbackManager: PlaybackManager? = null
    private var layoutManager: LinearLayoutManager? = null
    private var state: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSearchPager = SearchPager.instance()
        retainInstance = true
        listManager = ListManager.instance
        playbackManager = PlaybackManager.instance

        SpotifyWebApiAndroidService.mPlayer!!.addNotificationCallback(this@PlaylistFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_radio, container, false)
        try {

            mapFields(view)

            showMyTopTrack()
        } catch (e: Exception) {
            ExceptionManager().showSimpleAtention(context!!, e.message.toString())
        }
        return view
    }

    private fun mapFields(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        query = SearchFragment.query

        playbackManager = PlaybackManager.instance
        state = playbackManager!!.getState()

        layoutManager = LinearLayoutManager(activity)

        if (state != null)
            layoutManager!!.onRestoreInstanceState(state)

        mRecyclerView = view.findViewById(R.id.top_artist_RecyclerView)
        mRecyclerView!!.layoutManager = layoutManager
        background_album = view.findViewById(R.id.background_album_field)
    }

    private fun showMyTopTrack() {
        mSearchListener = object : SearchPager.onCompleteTopTrackListener {
            override fun onComplete(items: List<Track>) {
                listManager!!.clearList()

                for (track in items) {
                    val music = Music(
                        track.id,
                        track.uri,
                        track.name,
                        track.album.name,
                        track.album.images[0].url,
                        track.duration_ms,
                        track.artists[0].name,
                        track.artists[0].id
                    )
                    listManager!!.addTrack(music)
                }
                updateView()
            }

            override fun onError(error: Throwable) {}

        }
        mSearchPager!!.getMyTopTracks(mSearchListener as SearchPager.onCompleteTopTrackListener)

    }

    private fun updateView() {
        val mList = listManager!!.trackLists

        if (mList.size == 0) return

        if (mAdapter == null)
            mAdapter = TrackListAdapter(mList)

        mRecyclerView!!.adapter = mAdapter

        toolbar!!.setTitle(getString(R.string.my_playlist))

    }

    private inner class TrackListAdapter(private val musicList: List<Music>) :
        RecyclerView.Adapter<TrackListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListHolder {
            val layoutInflater = LayoutInflater.from(activity)
            val view = layoutInflater.inflate(R.layout.row_music, parent, false)
            return TrackListHolder(view)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBindViewHolder(holder: TrackListHolder, position: Int) {
            val music = musicList[position]
            holder.bindMusic(music)
        }

        override fun getItemCount(): Int {
            return musicList.size
        }
    }

    private inner class TrackListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var music: Music? = null
        private val text_title: TextView = itemView.findViewById(R.id.text_title)
        private val text_artist: TextView = itemView.findViewById(R.id.text_artist)
        private val text_album: TextView = itemView.findViewById(R.id.text_album)
        private val image_more_details: ImageButton = itemView.findViewById(R.id.image_more_details)

        init {

            val mClickListener = View.OnClickListener {
                when (it.id) {
                    R.id.image_more_details -> {
                        val intent = Intent(context, TrackDetailActivity::class.java)
                        val args = Bundle()
                        args.putParcelable(Consts.NAV_PARAM_DETAIL_MUSIC, music)

                        intent.putExtras(args)
                        startActivity(intent)
                    }
                    R.id.text_title -> {
                        if (SpotifyWebApiAndroidService.mPlayer!!.playbackState.isPlaying) {
                            val album = SpotifyWebApiAndroidService.mPlayer!!.metadata.currentTrack.albumName
                            val title = SpotifyWebApiAndroidService.mPlayer!!.metadata.currentTrack.name
                            val prevMusic = ListManager.instance.findCurrentMusic(title, album)

                            if (prevMusic != null) {
                                prevMusic.setPlaying(false)
                            }
                        }

                        SpotifyWebApiAndroidService.mPlayer!!.playUri(null, music!!.getUri(), 0, 0)
                        music!!.setPlaying(true)
                        mAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            image_more_details.setOnClickListener(mClickListener)
            text_title.setOnClickListener(mClickListener)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindMusic(m: Music) {
            music = m

            var title = music!!.getTitle()
            var album = music!!.getAlbum()

            if (title!!.length > 40) {
                title = title.substring(0, 40)
                title += "..."
            }

            if (album!!.length > 40) {
                album = album.substring(0, 40)
                album += "..."
            }

            text_title.text = title
            text_artist.setText(music!!.getArtist())
            text_album.setText(album)

            if (music!!.isPlaying())
                text_title.setTextColor(resources.getColor(R.color.colorAccent, null))
            else
                text_title.setTextColor(resources.getColor(R.color.colorWhite, null))
        }
    }
}