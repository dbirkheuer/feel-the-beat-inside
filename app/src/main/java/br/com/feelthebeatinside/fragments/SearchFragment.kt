package br.com.feelthebeatinside.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.manager.ExceptionManager
import br.com.feelthebeatinside.manager.ListManager
import br.com.feelthebeatinside.manager.PlaybackManager
import br.com.feelthebeatinside.model.ArtistSearch
import iammert.com.view.scalinglib.ScalingLayout
import iammert.com.view.scalinglib.ScalingLayoutListener
import iammert.com.view.scalinglib.State
import kaaes.spotify.webapi.android.models.Artist
import java.lang.Exception


class SearchFragment : Fragment() {
    private val TAG = "Spotify SearchFragment"

    private lateinit var textViewSearch: TextView
    private lateinit var editTextSearch: EditText
    private lateinit var scalingLayout: ScalingLayout
    private lateinit var searchListView: RecyclerView
    private lateinit var searchLayout: RelativeLayout
    private lateinit var rootLayout: LinearLayout
    private lateinit var button_search: ImageButton

    private var mAdapter: ArtistListAdapter? = null
    private var mFragmentManager: FragmentManager? = null

    companion object {
        @SuppressLint("ResourceType")
        fun instance(fm: FragmentManager, tag: String): SearchFragment {
            var fragment: SearchFragment? = fm.findFragmentByTag(tag) as SearchFragment

            if (fragment == null) {
                fragment = SearchFragment()

                val ft = fm.beginTransaction()
                ft.replace(R.id.fragment, fragment, tag).commitAllowingStateLoss()
            }
            return fragment
        }

        var query: String? = null
        var artist: ArtistSearch? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val manager = PlaybackManager.instance

        try {
            if (manager.isSearchResultFragmentAdded()) {
                mFragmentManager = fragmentManager!!
                val ft = mFragmentManager!!.beginTransaction()
                ft.add(R.id.fragment, SearchResultFragment.instance(query!!))
                    .addToBackStack(TAG)
                    .commit()
            }

            mapFields(view)
        } catch (e: Exception) {
            ExceptionManager().showSimpleAtention(context!!, e.message.toString())
        }

        return view
    }

    private fun mapFields(view: View) {
        textViewSearch = view.findViewById(R.id.textViewSearch)
        rootLayout = view.findViewById(R.id.rootLayout)
        rootLayout.setOnClickListener(mClickListener)

        searchLayout = view.findViewById(R.id.searchLayout)

        editTextSearch = view.findViewById(R.id.editTextSearch)

        button_search = view.findViewById(R.id.button_search)
        button_search.setOnClickListener(mClickListener)

        mAdapter = ArtistListAdapter(ListManager.instance.artists)

        searchListView = view.findViewById(R.id.Artist_search_list)
        searchListView.layoutManager = LinearLayoutManager(context)
        searchListView.adapter = mAdapter

        scalingLayout = view.findViewById(R.id.scalingLayout)
        scalingLayout.setOnClickListener(mClickListener)
        scalingLayout.setListener(object : ScalingLayoutListener {
            override fun onCollapsed() {
                ViewCompat.animate(textViewSearch).alpha(1f).setDuration(150).start()
                ViewCompat.animate(searchLayout).alpha(0f).setDuration(150)
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            textViewSearch.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(view: View) {
                            searchLayout.setVisibility(View.INVISIBLE)
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).start()
            }

            override fun onExpanded() {
                ViewCompat.animate(textViewSearch).alpha(0f).setDuration(200).start()
                ViewCompat.animate(searchLayout).alpha(1f).setDuration(200)
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            searchLayout.setVisibility(View.VISIBLE)
                        }

                        override fun onAnimationEnd(view: View) {
                            textViewSearch.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).start()
            }

            override fun onProgress(progress: Float) {}
        })
    }

    private var mClickListener: View.OnClickListener = View.OnClickListener { view ->
        try {
            when (view.id) {
                R.id.button_search -> {
                    query = editTextSearch.text.toString()

                    scalingLayout.collapse()

                    if (query!!.isEmpty()) {
                        textViewSearch.text = getString(R.string.search)
                    } else {

                        mFragmentManager = fragmentManager!!
                        val ft = mFragmentManager!!.beginTransaction()
                        ft.add(R.id.fragment, SearchResultFragment.instance(query!!))
                            .addToBackStack(TAG)
                            .commit()

                        textViewSearch.text = query
                    }
                }
                R.id.rootLayout -> {
                    if (scalingLayout.state == State.EXPANDED) {
                        scalingLayout.collapse()

                        if (editTextSearch.text.toString().isEmpty())
                            textViewSearch.text = getString(R.string.search)
                    }
                }
                R.id.scalingLayout -> {
                    if (scalingLayout.state == State.COLLAPSED) {
                        scalingLayout.expand()
                    }
                }
            }
        } catch (e: Exception) {
            ExceptionManager().showSimpleAtention(context!!, e.message.toString())
        }
    }

    private inner class ArtistListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artistName: TextView = itemView.findViewById(R.id.search_artist_name)
        private val artistImage: ImageView = itemView.findViewById(R.id.search_artist_image_field)
        private var artistSearch: ArtistSearch? = null

        fun bindArtist(search: ArtistSearch) {
            artistSearch = search
            artistName.text = artistSearch!!.name
            artistImage.setImageBitmap(artistSearch!!.image)


        }
    }

    private inner class ArtistListAdapter(private val artistSearchList: List<ArtistSearch>) :
        RecyclerView.Adapter<ArtistListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistListHolder {
            val layoutInflater = LayoutInflater.from(activity)
            val view = layoutInflater.inflate(R.layout.artist_search, parent, false)
            return ArtistListHolder(view)
        }

        override fun onBindViewHolder(holder: ArtistListHolder, position: Int) {
            holder.bindArtist(artistSearchList[position])
        }

        override fun getItemCount(): Int {
            return artistSearchList.size
        }
    }
}