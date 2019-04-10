package br.com.feelthebeatinside.activities

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import br.com.feelthebeatinside.R
import br.com.feelthebeatinside.fragments.HomeFragment
import br.com.feelthebeatinside.fragments.PlayerFragment
import br.com.feelthebeatinside.fragments.PlaylistFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var myActionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFields()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun mapFields() {
        myActionBar = supportActionBar!!
        myActionBar.setHomeButtonEnabled(true)
        myActionBar.setDisplayHomeAsUpEnabled(true)

        replaceToHomeFragment()
    }

    private fun replaceToHomeFragment() {
        replaceFragmenty(
            fragment = HomeFragment(),
            allowStateLoss = true,
            containerViewId = R.id.frameContainer
        )
        myActionBar.setTitle("Home")
    }

    private fun replaceToPlaylistFragment() {
        replaceFragmenty(
            fragment = PlaylistFragment(),
            allowStateLoss = true,
            containerViewId = R.id.frameContainer
        )
        myActionBar.setTitle("Minha Playlist")
    }

    private fun replaceToPlayFragment() {
        replaceFragmenty(
            fragment = PlayerFragment(),
            allowStateLoss = true,
            containerViewId = R.id.frameContainer
        )
        myActionBar.setTitle("Tocando")
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceToHomeFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_playlist -> {
                replaceToPlaylistFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_track -> {
                replaceToPlayFragment()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun replaceFragmenty(
        fragment: Fragment,
        allowStateLoss: Boolean = false,
        @IdRes containerViewId: Int
    ) {
        val ft = supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment)
        if (!supportFragmentManager.isStateSaved) {
            ft.commit()
        } else if (allowStateLoss) {
            ft.commitAllowingStateLoss()
        }
    }


}
