package br.com.feelthebeatinside.fragments

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.manager.ExceptionManager


class MainFragment : Fragment() {

    private var manager: FragmentManager? = null
    private var homeText: TextView? = null
    private var browseText: TextView? = null
    private var searchText: TextView? = null
    private var radioText: TextView? = null
    private var libraryText: TextView? = null
    private var prev_view: View? = null
    private var home: Drawable? = null
    private var browse: Drawable? = null
    private var search: Drawable? = null
    private var radio: Drawable? = null
    private var library: Drawable? = null
    private var focusMode: Int = 0
    private var defocusMode: Int = 0
    private var prev_clicked_id = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = fragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        mapFields(view)
        return view
    }

    private fun mapFields(view: View) {
        val homeLayout = view.findViewById<Button>(R.id.nav_home)
        val browseLayout = view.findViewById<Button>(R.id.nav_browse)
        val searchLayout = view.findViewById<Button>(R.id.nav_search)
        val radioLayout = view.findViewById<Button>(R.id.nav_radio)
        val libraryLayout = view.findViewById<Button>(R.id.nav_library)
        homeLayout.setOnClickListener(mListener)
        browseLayout.setOnClickListener(mListener)
        searchLayout.setOnClickListener(mListener)
        radioLayout.setOnClickListener(mListener)
        libraryLayout.setOnClickListener(mListener)

        homeText = view.findViewById(R.id.nav_home_text)
        browseText = view.findViewById(R.id.nav_browse_text)
        searchText = view.findViewById(R.id.nav_search_text)
        radioText = view.findViewById(R.id.nav_radio_text)
        libraryText = view.findViewById(R.id.nav_library_text)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        home = resources.getDrawable(R.drawable.ic_home_black_24dp, null)
        browse = resources.getDrawable(R.drawable.ic_open_in_browser_black_24dp, null)
        search = resources.getDrawable(R.drawable.ic_search_black_24dp, null)
        radio = resources.getDrawable(R.drawable.ic_radio_black_24dp, null)
        library = resources.getDrawable(R.drawable.ic_library_music_black_24dp, null)
        focusMode = resources.getColor(R.color.colorAccentPressed)
        defocusMode = resources.getColor(R.color.colorAccent)
    }

    private var mListener: View.OnClickListener = View.OnClickListener { view ->
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.nav_click))

        when (view.id) {
            R.id.nav_home -> {
                if (!view.isActivated) {
                    manager!!.beginTransaction().replace(R.id.fragment, HomeFragment()).commit()
                    setFocusAndDeFocus(view, R.id.nav_home)
                    prev_clicked_id = R.id.nav_home
                    prev_view = view
                }
            }
            R.id.nav_browse -> {
                if (!view.isActivated) {
                    manager!!.beginTransaction().replace(R.id.fragment, BrowseFragment()).commit()

                    setFocusAndDeFocus(view, R.id.nav_browse)

                    prev_clicked_id = R.id.nav_browse
                    prev_view = view
                }
            }
            R.id.nav_search -> {
                if (!view.isActivated) {
                    manager!!.beginTransaction().replace(R.id.fragment, SearchFragment()).commit()

                    setFocusAndDeFocus(view, R.id.nav_search)

                    prev_clicked_id = R.id.nav_search
                    prev_view = view
                }
            }
            R.id.nav_radio -> {
                if (!view.isActivated) {
                    manager!!.beginTransaction().replace(R.id.fragment, RadioFragment()).commit()

                    setFocusAndDeFocus(view, R.id.nav_radio)

                    prev_clicked_id = R.id.nav_radio
                    prev_view = view
                }
            }
            R.id.nav_library -> {
                if (!view.isActivated) {
                    manager!!.beginTransaction().replace(R.id.fragment, LibraryFragment()).commit()
                    setFocusAndDeFocus(view, R.id.nav_library)
                    prev_clicked_id = R.id.nav_library
                    prev_view = view
                }
            }
        }
    }

    private fun setFocusAndDeFocus(view: View, id: Int) {
        setFocus(id, view)
        if (prev_view != null)
            setDeFocus(prev_clicked_id, prev_view!!)
    }

    private fun setFocus(res_id: Int, view: View) {
        when (res_id) {
            R.id.nav_home -> {
                home!!.setTint(focusMode)
                view.background = home
                homeText!!.setTextColor(focusMode)
                homeText!!.typeface = Typeface.DEFAULT_BOLD
                view.isActivated = true
            }
            R.id.nav_browse -> {
                browse!!.setTint(focusMode)
                view.background = browse
                browseText!!.setTextColor(focusMode)
                browseText!!.typeface = Typeface.DEFAULT_BOLD
                view.isActivated = true
            }
            R.id.nav_search -> {
                search!!.setTint(focusMode)
                view.background = search
                searchText!!.setTextColor(focusMode)
                searchText!!.typeface = Typeface.DEFAULT_BOLD
                view.isActivated = true
            }
            R.id.nav_radio -> {
                radio!!.setTint(focusMode)
                view.background = radio
                radioText!!.setTextColor(focusMode)
                radioText!!.typeface = Typeface.DEFAULT_BOLD
                view.isActivated = true
            }
            R.id.nav_library -> {
                library!!.setTint(focusMode)
                view.background = library
                libraryText!!.setTextColor(focusMode)
                libraryText!!.typeface = Typeface.DEFAULT_BOLD
                view.isActivated = true
            }
        }
    }

    private fun setDeFocus(res_id: Int, view: View) {
        when (res_id) {
            R.id.nav_home -> {
                home!!.setTint(defocusMode)
                view.background = home
                homeText!!.setTextColor(defocusMode)
                homeText!!.typeface = Typeface.DEFAULT
                view.isActivated = false
            }
            R.id.nav_browse -> {
                browse!!.setTint(defocusMode)
                view.background = browse
                browseText!!.setTextColor(defocusMode)
                browseText!!.typeface = Typeface.DEFAULT
                view.isActivated = false
            }
            R.id.nav_search -> {
                search!!.setTint(defocusMode)
                view.background = search
                searchText!!.setTextColor(defocusMode)
                searchText!!.typeface = Typeface.DEFAULT
                view.isActivated = false
            }
            R.id.nav_radio -> {
                radio!!.setTint(defocusMode)
                view.background = radio
                radioText!!.setTextColor(defocusMode)
                radioText!!.typeface = Typeface.DEFAULT
                view.isActivated = false
            }
            R.id.nav_library -> {
                library!!.setTint(defocusMode)
                view.background = library
                libraryText!!.setTextColor(defocusMode)
                libraryText!!.typeface = Typeface.DEFAULT
                view.isActivated = false
            }
            -1 -> {
            }
            else -> {
            }
        }
    }
}
