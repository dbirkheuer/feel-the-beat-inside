package br.com.feelthebeatinside.manager

import android.os.Parcelable
import br.com.feelthebeatinside.model.Music

class PlaybackManager {


    private var music: Music? = null
    private var state: Parcelable? = null
    private var SearchResultFragmentAdded = false

    companion object {
        private var manager: PlaybackManager? = null

        val instance: PlaybackManager get() {
            if (manager == null) {
                manager = PlaybackManager()
            }
            return manager as PlaybackManager
        }
    }

    fun getMusic(): Music? {
        return music
    }

    fun setMusic(music: Music) {
        this.music = music
    }

    fun getState(): Parcelable? {
        return state
    }

    fun setState(state: Parcelable) {
        this.state = state
    }

    fun isSearchResultFragmentAdded(): Boolean {
        return SearchResultFragmentAdded
    }

    fun setSearchResultFragmentAdded(searchResultFragmentAdded: Boolean) {
        SearchResultFragmentAdded = searchResultFragmentAdded
    }
}