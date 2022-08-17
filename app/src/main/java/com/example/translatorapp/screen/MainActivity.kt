package com.example.translatorapp.screen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.translatorapp.R
import com.example.translatorapp.databinding.ActivityMainBinding
import com.example.translatorapp.screen.translate.TranslateFragment
import com.example.translatorapp.util.addFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding
    private val toggle by lazy {
        ActionBarDrawerToggle(
            this, bind.drawerLayout,
            bind.layoutMain.toolbar, R.string.open, R.string.close
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.layoutMain.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayUseLogoEnabled(false)
        }
        bind.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        bind.layoutMain.toolbar.setNavigationIcon(R.drawable.ic_menu)
        addListener()
        addFragment(
            fragment = TranslateFragment.newInstance(),
            addToBackStack = false,
            container = findLayoutContainer(),
            manager = supportFragmentManager
        )
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.fragments
        val length = fragments.size - 1
        var backFlag = true
        var popFlag = true
        for (index in length downTo 0) {
            val childFragManager = fragments[index].childFragmentManager
            if (childFragManager.backStackEntryCount > 0) {
                if (fragments[index].isVisible && popFlag) {
                    childFragManager.popBackStack()
                    return
                }
                backFlag = false
            }
            popFlag = false
        }
        super.onBackPressed()
        if (backFlag) {
            enableView(false)
            changeToolbar(getString(R.string.title_app), R.drawable.ic_menu)
        } else {
            changeToolbar(getString(R.string.title_app), R.drawable.ic_back)
        }
    }

    private fun addListener() {
        bind.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_setting -> Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show()
                R.id.menu_test -> Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
                R.id.menu_history -> Toast.makeText(this, "history", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    fun changeToolbar(title: String, img: Int) {
        bind.layoutMain.textTitle.text = title
        bind.layoutMain.toolbar.setNavigationIcon(img)
    }

    fun enableView(enable: Boolean) {
        if (enable) {
            bind.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
            toggle.setToolbarNavigationClickListener { onBackPressed() }
        } else {
            bind.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            toggle.isDrawerIndicatorEnabled = true
            toggle.toolbarNavigationClickListener = null
        }
    }

    fun findLayoutContainer() = bind.layoutMain.container.frameLayoutContainer.id
}
