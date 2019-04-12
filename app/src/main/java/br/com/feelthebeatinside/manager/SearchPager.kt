package br.com.feelthebeatinside.manager

import android.content.Context
import android.util.Log
import br.com.feelthebeatinside.activities.MainActivity
import br.com.feelthebeatinside.model.AlbumNew
import br.com.feelthebeatinside.model.SimplePlaylist
import br.com.feelthebeatinside.model.TopArtist
import br.com.feelthebeatinside.model.TopTrack
import br.com.feelthebeatinside.services.SpotifyWebApiAndroidService
import kaaes.spotify.webapi.android.SpotifyCallback
import kaaes.spotify.webapi.android.SpotifyError
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.*
import retrofit.client.Response
import java.util.*

class SearchPager {

    interface CompleteListener {
        fun onComplete(items: List<Track>)
        fun onError(error: Throwable)
    }

    interface ArtistListener {
        fun onComplete(url: String)
        fun onError(error: Throwable)
    }

    interface onCompleteListener {
        fun onComplete()
        fun onError(error: Throwable)
    }

    interface onCompleteTopArtistListener {
        fun onComplete()
        fun onError(error: Throwable)
    }

    interface onCompleteTopTrackListener {
        fun onComplete()
        fun onError(error: Throwable)
    }

    fun getTracksFromSearch(query: String, listener: CompleteListener) {
        getData(query, listener)
    }

    private fun getData(query: String, listener: CompleteListener) {

        SpotifyWebApiAndroidService.spotifyService!!.searchTracks(query, object : SpotifyCallback<TracksPager>() {
            override fun failure(spotifyError: SpotifyError) {
                listener.onError(spotifyError)
            }

            override fun success(tracksPager: TracksPager, response: Response) {
                listener.onComplete(tracksPager.tracks.items)
            }
        })
    }

    fun getArtist(id: String, listener: ArtistListener) {

        SpotifyWebApiAndroidService.spotifyService!!.getArtist(id, object : SpotifyCallback<Artist>() {
            override fun failure(spotifyError: SpotifyError) {
                Log.d("SearchPager", spotifyError.toString())
                listener.onError(spotifyError)
            }

            override fun success(artist: Artist, response: Response) {
                listener.onComplete(artist.images[1].url)
            }
        })
    }

    fun getMyTopArtist(listener: onCompleteTopArtistListener?) {

        val options = HashMap<String, Any>()
        options[SpotifyService.LIMIT] = 10

        val listManager = ListManager.instance

        SpotifyWebApiAndroidService.spotifyService!!.getTopArtists(options, object : SpotifyCallback<Pager<Artist>>() {
            override fun failure(spotifyError: SpotifyError) {
                Log.d("SearchPager", spotifyError.toString())

                listener?.onError(spotifyError)
            }

            override fun success(artistPager: Pager<Artist>, response: Response) {
                val mList = artistPager.items

                for (art in mList) {
                    Log.d("SearchPager", art.name)
                    Log.d("SearchPager", art.images[1].url)

                    listManager.addTopArtist(TopArtist(art.name, art.images[1].url))
                }

                listener?.onComplete() ?: Log.d("SearchPager", "What is happening?")
            }
        })
    }

    fun getMyTopTracks(listener: onCompleteTopTrackListener?) {
        val options = HashMap<String, Any>()
        options[SpotifyService.LIMIT] = 10

        val listManager = ListManager.instance

        SpotifyWebApiAndroidService.spotifyService!!.getTopTracks(options, object : SpotifyCallback<Pager<Track>>() {
            override fun failure(spotifyError: SpotifyError) {
                Log.d("SearchPager", spotifyError.toString())

                listener?.onError(spotifyError)
            }

            override fun success(trackPager: Pager<Track>, response: Response) {
                val tracks = trackPager.items

                for (track in tracks) {
                    Log.d("SearchPager", track.album.name)
                    Log.d("SearchPager", track.album.images[1].url)

                    listManager.addTopTrack(TopTrack(track.album.name, track.album.images[1].url))

                }

                listener?.onComplete()
            }
        })
    }

    fun getNewRelease(listener: onCompleteListener?) {
        val options = HashMap<String, Any>()
        options[SpotifyService.OFFSET] = 0  // 0 ~ 5 6 ~ 10
        options[SpotifyService.LIMIT] = 10

        val listManager = ListManager.instance

        SpotifyWebApiAndroidService.spotifyService!!.getNewReleases(options, object : SpotifyCallback<NewReleases>() {
            override fun failure(spotifyError: SpotifyError) {
                Log.d("SearchPager", spotifyError.toString())

                listener?.onError(spotifyError)
            }

            override fun success(newReleases: NewReleases, response: Response) {
                val albums = newReleases.albums.items

                for (albumSimple in albums) {
                    SpotifyWebApiAndroidService.spotifyService!!.getAlbum(
                        albumSimple.id,
                        object : SpotifyCallback<Album>() {
                            override fun failure(spotifyError: SpotifyError) {
                                Log.d("SearchPage Followup", spotifyError.toString())

                                listener?.onError(spotifyError)
                            }

                            override fun success(album: Album, response: Response) {

                                Log.d("SearchPage Followup", albumSimple.name)
                                Log.d("SearchPage Followup", albumSimple.id)
                                Log.d("SearchPage Followup", albumSimple.images[1].url)

                                val list = album.artists
                                val artists = ArrayList<String>()

                                for (simple in list) {
                                    Log.d("SearchPage Followup", simple.name)
                                    artists.add(simple.name)
                                }

                                val albumNew = AlbumNew(albumSimple.name, albumSimple.images[1].url, artists)

                                listManager.addNewAlbum(albumNew)

                            }

                        })
                }

                listener?.onComplete()
            }
        })
    }

    fun getMyPlayList() {
        val options = HashMap<String, Any>()
        options[SpotifyService.LIMIT] = 30

        SpotifyWebApiAndroidService.spotifyService!!.getMyPlaylists(
            options,
            object : SpotifyCallback<Pager<PlaylistSimple>>() {
                override fun failure(spotifyError: SpotifyError) {
                    Log.d("SearchPager", spotifyError.toString())
                }

                override fun success(playlistSimplePager: Pager<PlaylistSimple>, response: Response) {
                    val simples = playlistSimplePager.items

                    for (simple in simples) {
                        Log.d("SearchPager", simple.name)
                        Log.d("SearchPager", simple.images[1].url)
                    }

                }
            })
    }

    fun getFeatured() {

        SpotifyWebApiAndroidService.spotifyService!!.getFeaturedPlaylists(object :
            SpotifyCallback<FeaturedPlaylists>() {
            override fun failure(spotifyError: SpotifyError) {
                Log.d("SearchPager", spotifyError.toString())
            }

            override fun success(featuredPlaylists: FeaturedPlaylists, response: Response) {
                val mlist = featuredPlaylists.playlists.items

                for (simple in mlist) {
                    Log.d("SearchPager Simple", simple.name)
                    Log.d("SearchPager Simple", simple.images[0].url)

                    ListManager.instance.addSimpleList(SimplePlaylist(simple.name, simple.images[0].url))
                }
            }
        })
    }

    companion object {

        private var searchPager: SearchPager? = null

        fun instance(context: Context): SearchPager {
            if (searchPager == null) {
                searchPager = SearchPager()
            }
            return searchPager as SearchPager
        }
    }

}