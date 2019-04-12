package br.com.feelthebeatinside.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.manager.ListManager
import br.com.feelthebeatinside.manager.SearchPager
import br.com.feelthebeatinside.model.AlbumNew
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class BrowseFragment : Fragment() {


    private var newAlbumsRecyclerView: RecyclerView? = null
    private var mAdapter: newAlbumAdapter? = null

    private var listManager: ListManager? = null
    private var listener: SearchPager.onCompleteListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listManager = ListManager.instance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_browse, container, false)

        newAlbumsRecyclerView = view.findViewById(R.id.new_albums_RecyclerView)
        newAlbumsRecyclerView!!.layoutManager = GridLayoutManager(context, 2)

        updateView()

        return view
    }

    private fun updateView() {
        val albumNewList = listManager!!.albumNewArrayList

        if (albumNewList.size == 0) {

            listener = object : SearchPager.onCompleteListener {
                override fun onComplete() {
                    mAdapter!!.notifyDataSetChanged()
                    updateView()
                }

                override fun onError(error: Throwable) {

                }
            }
            SearchPager.instance().getNewRelease(listener)
        }

        if (mAdapter == null)
            mAdapter = newAlbumAdapter(albumNewList)

        newAlbumsRecyclerView!!.adapter = mAdapter

    }

    private inner class newAlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val album_image: ImageView = itemView.findViewById(R.id.new_album_image)
        private val album_title: TextView = itemView.findViewById(R.id.new_album_title)
        private val album_artists: TextView = itemView.findViewById(R.id.new_artist_name)

        fun bindAlbum(albumNew: AlbumNew) {
            val title = albumNew.title
            val img_url = albumNew.img_url

            val sb = StringBuilder()

            val artists = albumNew.artists
            for (s in artists) {
                sb.append(s + ", ")
            }

            var artist = sb.toString()

            val index = artist.lastIndexOf(",")
            artist = artist.substring(0, index)

            if (artist.length > 30) {
                artist = artist.substring(0, 30)
                artist += "..."
            }

            album_title.setText(title)
            album_artists.text = artist

            Picasso.with(context)
                .load(img_url)
                .transform(object : Transformation {
                    override fun transform(source: Bitmap): Bitmap {
                        val copy = source.copy(source.config, true)
                        source.recycle()

                        return copy
                    }

                    override fun key(): String {
                        return title
                    }
                })
                .into(album_image)
        }
    }

    private inner class newAlbumAdapter(private val albumNewsList: List<AlbumNew>) :
        RecyclerView.Adapter<newAlbumHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): newAlbumHolder {

            val inflater = LayoutInflater.from(activity)

            val view = inflater.inflate(R.layout.new_album_view, parent, false)

            return newAlbumHolder(view)
        }

        override fun onBindViewHolder(holder: newAlbumHolder, position: Int) {
            holder.bindAlbum(albumNewsList[position])
        }

        override fun getItemCount(): Int {
            return albumNewsList.size
        }
    }
}
