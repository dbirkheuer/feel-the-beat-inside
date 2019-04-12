package br.com.feelthebeatinside.manager

import android.content.Context
import android.util.Log
import br.com.feelthebeatinside.activities.MainActivity
import br.com.feelthebeatinside.model.*
import br.com.feelthebeatinside.services.SpotifyWebApiAndroidService
import kaaes.spotify.webapi.android.SpotifyCallback
import kaaes.spotify.webapi.android.SpotifyError
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.*
import retrofit.client.Response
import java.util.*
import kotlin.collections.ArrayList

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
        fun onComplete(items: List<Track>)
        fun onError(error: Throwable)
    }

    interface onCompleteMyPlaylistListener {
        fun onComplete(it: Playlist)
        fun onError(error: Throwable)
    }

    fun getTracksFromSearch(query: String, listener: CompleteListener) {
        getData(query, listener)
    }

    fun getTracksFromArtistId(music: ArrayList<Music>, idArtist: String): ArrayList<Music> {
        val artistTracks: ArrayList<Music> = ArrayList()

        for (track in music) {
            if (track.getArtist_id().equals(idArtist))
                artistTracks.add(track)
            if (artistTracks.size == 10)
                break
        }

        return artistTracks
    }

    private fun getArtistById(id: String) {
        SpotifyWebApiAndroidService.spotifyService!!.searchArtists(id).artists.items[0]
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
                listener.onError(spotifyError)
            }

            override fun success(artist: Artist, response: Response) {
                listener.onComplete(artist.images[1].url)
            }
        })
    }

    fun getMyTopTracks(listener: onCompleteTopTrackListener?) {
        val options = HashMap<String, Any>()
        options[SpotifyService.LIMIT] = 20

        SpotifyWebApiAndroidService.spotifyService!!.getTopTracks(options, object : SpotifyCallback<Pager<Track>>() {
            override fun failure(spotifyError: SpotifyError) {
                listener?.onError(spotifyError)
            }

            override fun success(trackPager: Pager<Track>, response: Response) {
                val tracks = trackPager.items
                listener?.onComplete(tracks)
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
                listener?.onError(spotifyError)
            }

            override fun success(newReleases: NewReleases, response: Response) {
                val albums = newReleases.albums.items

                for (albumSimple in albums) {
                    SpotifyWebApiAndroidService.spotifyService!!.getAlbum(
                        albumSimple.id,
                        object : SpotifyCallback<Album>() {
                            override fun failure(spotifyError: SpotifyError) {
                                listener?.onError(spotifyError)
                            }

                            override fun success(album: Album, response: Response) {
                                val list = album.artists
                                val artists = ArrayList<String>()

                                for (simple in list) {
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

    fun getFeatured() {

        SpotifyWebApiAndroidService.spotifyService!!.getFeaturedPlaylists(object :
            SpotifyCallback<FeaturedPlaylists>() {
            override fun failure(spotifyError: SpotifyError) {}

            override fun success(featuredPlaylists: FeaturedPlaylists, response: Response) {
                val mlist = featuredPlaylists.playlists.items

                for (simple in mlist) {
                    ListManager.instance.addSimpleList(SimplePlaylist(simple.name, simple.images[0].url))
                }
            }
        })
    }

    companion object {

        private var searchPager: SearchPager? = null

        fun instance(): SearchPager {
            if (searchPager == null) {
                searchPager = SearchPager()
            }
            return searchPager as SearchPager
        }
    }

}