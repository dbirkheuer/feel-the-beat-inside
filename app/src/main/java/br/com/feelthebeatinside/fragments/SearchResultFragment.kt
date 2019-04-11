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
import br.com.feelthebeatinside.activities.MainActivity
import br.com.feelthebeatinside.activities.TrackDetailActivity
import br.com.feelthebeatinside.manager.ListManager
import br.com.feelthebeatinside.manager.PlaybackManager
import br.com.feelthebeatinside.manager.SearchPager
import br.com.feelthebeatinside.model.ArtistSearch
import br.com.feelthebeatinside.model.Music
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerEvent
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kaaes.spotify.webapi.android.models.Track


class SearchResultFragment : Fragment(), Player.NotificationCallback {

    companion object {
        const val QUERY = "QUERY"
        const val DETAIL_MUSIC = "Detail Music"

        fun newInstance(query: String): SearchResultFragment {
            val args = Bundle()
            args.putString(QUERY, query)

            val searchResultFragment = SearchResultFragment()
            searchResultFragment.arguments = args

            return searchResultFragment
        }
    }

    private var query: String? = null
    private var toolbar: Toolbar? = null
    private var background_album: ImageView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: TrackListAdapter? = null
    private var mSearchPager: SearchPager? = null
    private var mSearchListener: SearchPager.CompleteListener? = null
    private var mArtistListener: SearchPager.ArtistListener? = null
    private var listManager: ListManager? = null
    private var playbackManager: PlaybackManager? = null
    private var layoutManager: LinearLayoutManager? = null
    private val mPlayer = MainActivity.mPlayer
    private var state: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSearchPager = SearchPager.instance(this.activity!!)
        retainInstance = true
        listManager = ListManager.instance
        playbackManager = PlaybackManager.instance

        playbackManager!!.setSearchResultFragmentAdded(true)

        mPlayer!!.addNotificationCallback(this@SearchResultFragment)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        query = arguments!!.getString(QUERY)

        val view = inflater.inflate(R.layout.fragment_search_result, container, false)

        playbackManager = PlaybackManager.instance
        state = playbackManager!!.getState()

        layoutManager = LinearLayoutManager(activity)

        if (state != null) {
            layoutManager!!.onRestoreInstanceState(state)
        }

        mRecyclerView = view.findViewById(R.id.track_list_recycler_view)
        mRecyclerView!!.layoutManager = layoutManager

        toolbar = view.findViewById(R.id.toolbar)

        toolbar!!.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp, null)
        toolbar!!.setNavigationOnClickListener {
            fragmentManager!!.beginTransaction().detach(this@SearchResultFragment).commit()

            val playbackManager = PlaybackManager.instance
            playbackManager.setSearchResultFragmentAdded(false)
        }

        background_album = view.findViewById(R.id.background_album_field)

        if (query != "empty") {
            queryData()
        } else {
            updateView()
        }

        return view
    }

    private fun queryData() {

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

            override fun onError(error: Throwable) {

            }
        }

        mSearchPager!!.getTracksFromSearch(query!!, mSearchListener as SearchPager.CompleteListener)
    }

    private fun updateView() {

        val mList = listManager!!.trackLists

        if (mList.size == 0) return

        if (mAdapter == null)
            mAdapter = TrackListAdapter(mList)

        mRecyclerView!!.adapter = mAdapter

        val artistName = mList.get(0).getArtist()

        toolbar!!.setTitle(artistName)

        mArtistListener = object : SearchPager.ArtistListener {
            override fun onComplete(img_url: String) {
                Picasso.with(context)
                    .load(img_url)
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
                    })
                    .into(background_album)
            }

            override fun onError(error: Throwable) {

            }
        }
        mSearchPager!!.getArtist(mList.get(0).getArtist_id()!!, mArtistListener as SearchPager.ArtistListener)
    }

    override fun onPlaybackEvent(playerEvent: PlayerEvent) {
        when (playerEvent.name) {
            "kSpPlaybackNotifyPlay" -> {
            }

            "kSpPlaybackNotifyPause" -> {
                val title = mPlayer!!.metadata.currentTrack.name
                val album = mPlayer.metadata.currentTrack.albumName

                val music = ListManager.instance.findCurrentMusic(title, album)

                if (music != null)
                    music.setPlaying(false)
                if (mAdapter != null)
                    mAdapter!!.notifyDataSetChanged()
            }

            "kSpPlaybackNotifyTrackChanged" -> {
            }

            "kSpPlaybackEventAudioFlush" -> {
            }

            else -> {
            }
        }
    }

    override fun onPlaybackError(error: Error) {

    }

    private inner class TrackListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var music: Music? = null
        private val title_text: TextView = itemView.findViewById(R.id.title_field)
        private val artist_text: TextView = itemView.findViewById(R.id.artist_field)
        private val album_text: TextView = itemView.findViewById(R.id.album_field)
        private val more_button: ImageButton = itemView.findViewById(R.id.more_horiz)

        init {
            more_button.setOnClickListener {
                val intent = Intent(context, TrackDetailActivity::class.java)

                val args = Bundle()
                args.putParcelable(DETAIL_MUSIC, music)

                intent.putExtras(args)
                startActivity(intent)
            }

            title_text.setOnClickListener {
                if (mPlayer!!.playbackState.isPlaying) {
                    val album = mPlayer.metadata.currentTrack.albumName
                    val title = mPlayer.metadata.currentTrack.name

                    val prevMusic = ListManager.instance.findCurrentMusic(title, album)

                    if (prevMusic != null) {
                        prevMusic.setPlaying(false)
                    }
                }

                mPlayer.playUri(null, music!!.getUri(), 0, 0)
                music!!.setPlaying(true)
                mAdapter!!.notifyDataSetChanged()
            }
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

            title_text.setText(title)
            artist_text.setText(music!!.getArtist())
            album_text.setText(album)

            if (music!!.isPlaying())
                title_text.setTextColor(resources.getColor(R.color.colorAccent, null))
            else
                title_text.setTextColor(resources.getColor(R.color.colorWhite, null))
        }
    }

    private inner class TrackListAdapter(private val musicList: List<Music>) :
        RecyclerView.Adapter<TrackListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListHolder {

            val layoutInflater = LayoutInflater.from(activity)

            val view = layoutInflater.inflate(R.layout.music, parent, false)

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

    override fun onPause() {
        super.onPause()
        state = layoutManager!!.onSaveInstanceState()!!

        playbackManager = PlaybackManager.instance
        playbackManager!!.setState(state!!)
    }
}
