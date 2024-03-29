package br.com.feelthebeatinside.fragments

import android.content.Intent
import android.graphics.Bitmap
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
import br.com.feelthebeatinside.model.ArtistSearch
import br.com.feelthebeatinside.model.Music
import br.com.feelthebeatinside.services.SpotifyWebApiAndroidService
import br.com.feelthebeatinside.util.Consts
import br.com.feelthebeatinside.util.TimeUtil
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerEvent
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kaaes.spotify.webapi.android.models.Track


class HomeFragment : Fragment(), Player.NotificationCallback {

    private var toolbar: Toolbar? = null
    private var query: String? = null
    private var background_album: ImageView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: TrackListAdapter? = null
    private var mSearchPager: SearchPager? = null
    private var mSearchListener: SearchPager.CompleteListener? = null
    private var mArtistListener: SearchPager.ArtistListener? = null
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

        SpotifyWebApiAndroidService.mPlayer!!.addNotificationCallback(this@HomeFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        try {
            if (SearchResultFragment.last_search_artist_id == null)
                return view

            mapFields(view)

            showTopTen()
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

    private fun showTopTen() {
        mSearchListener = object : SearchPager.CompleteListener {
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
        mSearchPager!!.getTracksFromSearch(
            query!!,
            mSearchListener as SearchPager.CompleteListener
        )

    }

    private fun updateView() {
        val mList = mSearchPager!!.getTracksFromArtistId(
            listManager!!.trackLists,
            SearchResultFragment.last_search_artist_id!!
        )

        if (mList.size == 0) return

        if (mAdapter == null)
            mAdapter = TrackListAdapter(mList)

        mRecyclerView!!.adapter = mAdapter

        val artistName = mList.get(0).getArtist()
        toolbar!!.setTitle(artistName)

        mArtistListener = object : SearchPager.ArtistListener {
            override fun onComplete(url: String) {
                Picasso.with(context)
                    .load(url)
                    .transform(object : Transformation {
                        override fun transform(source: Bitmap): Bitmap {
                            val copy = source.copy(source.config, true)
                            source.recycle()

                            listManager!!.addArtist(ArtistSearch(artistName!!, copy))

                            return copy
                        }

                        override fun key(): String? {
                            return query
                        }
                    }).into(background_album)
            }

            override fun onError(error: Throwable) {}
        }
        mSearchPager!!.getArtist(
            SearchResultFragment.last_search_artist_id!!,
            mArtistListener as SearchPager.ArtistListener
        )
    }

    override fun onPlaybackEvent(playerEvent: PlayerEvent) {
    }

    override fun onPlaybackError(p0: Error?) {
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
