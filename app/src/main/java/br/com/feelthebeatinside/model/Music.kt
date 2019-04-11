package br.com.feelthebeatinside.model

import android.os.Parcel
import android.os.Parcelable

class Music() : Parcelable {
    private var id: String? = null
    private var uri: String? = null
    private var title: String? = null
    private var album: String? = null
    private var album_image: String? = null
    private var duration: Long = 0
    private var artist: String? = null
    private var artist_id: String? = null
    private var playing: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        uri = parcel.readString()
        title = parcel.readString()
        album = parcel.readString()
        album_image = parcel.readString()
        duration = parcel.readLong()
        artist = parcel.readString()
        artist_id = parcel.readString()
        playing = parcel.readByte() != 0.toByte()
    }

    constructor(i: String, u: String, t: String, a: String, a_img: String, dura: Long, art: String, art_id: String) : this() {
        id = i
        uri = u
        title = t
        album = a
        album_image = a_img
        duration = dura
        artist = art
        artist_id = art_id
        playing = false
    }


    fun getArtist_id(): String? {
        return artist_id
    }

    fun getId(): String? {
        return id
    }

    fun getUri(): String? {
        return uri
    }

    fun getTitle(): String? {
        return title
    }

    fun getAlbum(): String? {
        return album
    }

    fun getAlbum_image(): String? {
        return album_image
    }

    fun getDuration(): Long {
        return duration
    }

    fun getArtist(): String? {
        return artist
    }

    fun isPlaying(): Boolean {
        return playing
    }

    fun setPlaying(playing: Boolean) {
        this.playing = playing
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(uri)
        parcel.writeString(title)
        parcel.writeString(album)
        parcel.writeString(album_image)
        parcel.writeLong(duration)
        parcel.writeString(artist)
        parcel.writeString(artist_id)
        parcel.writeByte(if (playing) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Music> {
        override fun createFromParcel(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun newArray(size: Int): Array<Music?> {
            return arrayOfNulls(size)
        }
    }
}