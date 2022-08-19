package com.example.translatorapp.screen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.translatorapp.R
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.databinding.ActivityMainBinding
import com.example.translatorapp.screen.setting.SettingFragment
import com.example.translatorapp.screen.translate.TranslateFragment
import com.example.translatorapp.util.addFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val toggle by lazy {
        ActionBarDrawerToggle(
            this, binding.drawerLayout,
            binding.layoutMain.toolbar, R.string.open, R.string.close
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        restoreState(savedInstanceState)
        setSupportActionBar(binding.layoutMain.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayUseLogoEnabled(false)
        }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.layoutMain.toolbar.setNavigationIcon(R.drawable.ic_menu)
        addListener()
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(Constant.KEY_RECREATE, true)
        super.onSaveInstanceState(outState)
    }

    private fun addListener() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_setting -> {
                    binding.drawerLayout.close()
                    addSettingFragment()
                }
                R.id.menu_test -> Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
                R.id.menu_history -> Toast.makeText(this, "history", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun addSettingFragment() {
        val translateFragment = supportFragmentManager.findFragmentByTag(Constant.TAG_TRANSLATE)
        if (translateFragment is TranslateFragment) {
            addFragment(
                fragment = SettingFragment.newInstance(translateFragment.speak),
                addToBackStack = true,
                container = findLayoutContainer(),
                manager = supportFragmentManager
            )
        }
    }

    private fun restoreState(instanceState: Bundle?) {
        if (instanceState == null) {
            addFragment(
                fragment = TranslateFragment.newInstance(),
                addToBackStack = false,
                container = findLayoutContainer(),
                manager = supportFragmentManager,
                tag = Constant.TAG_TRANSLATE
            )
        }
    }

    fun changeToolbar(title: String, img: Int) {
        binding.layoutMain.textTitle.text = title
        binding.layoutMain.toolbar.setNavigationIcon(img)
    }

    fun enableView(enable: Boolean) {
        if (enable) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
            toggle.setToolbarNavigationClickListener { onBackPressed() }
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            toggle.isDrawerIndicatorEnabled = true
            toggle.toolbarNavigationClickListener = null
        }
    }

    fun unCheckItem() {
        binding.navView.checkedItem?.isChecked = false
    }

    fun findLayoutContainer() = binding.layoutMain.container.frameLayoutContainer.id
}
