package br.com.feelthebeatinside.manager

import br.com.feelthebeatinside.model.*
import java.util.ArrayList

class ListManager private constructor() {

    val trackLists: ArrayList<Music> = ArrayList()
    val artists: ArrayList<ArtistSearch> = ArrayList()
    val albumNewArrayList: ArrayList<AlbumNew> = ArrayList()
    val topArtists: ArrayList<TopArtist> = ArrayList()
    val topTracks: ArrayList<TopTrack> = ArrayList()
    val simplePlaylists: ArrayList<SimplePlaylist> = ArrayList()

    fun addSimpleList(simple: SimplePlaylist) {
        simplePlaylists.add(simple)
    }

    fun addTopTrack(track: TopTrack) {
        topTracks.add(track)
    }

    fun addTopArtist(artist: TopArtist) {
        topArtists.add(artist)
    }

    fun addNewAlbum(albumNew: AlbumNew) {
        albumNewArrayList.add(albumNew)
    }

    fun addArtist(search: ArtistSearch) {
        var found: ArtistSearch? = null

        for (artistSearch in artists) {
            if (artistSearch.name.equals(search.name))
                found = artistSearch
        }

        if (found != null)
            artists.remove(found)

        artists.add(0, search)
    }

    fun addTrack(music: Music) {
        trackLists.add(music)
    }

    fun clearList() {
        trackLists.clear()
    }

    fun findCurrentMusic(title: String, album: String): Music? {
        for (m in trackLists) {
            if (m.getTitle().equals(title) && m.getAlbum().equals(album)) {
                return m
            }
        }
        return null
    }

    companion object {

        private var listManager: ListManager? = null

        val instance: ListManager
            get() {
                if (listManager == null) {
                    listManager = ListManager()
                }

                return listManager!!
            }
    }
}