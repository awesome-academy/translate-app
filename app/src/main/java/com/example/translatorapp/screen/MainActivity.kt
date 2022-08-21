package com.example.translatorapp.screen

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.translatorapp.R
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.databinding.ActivityMainBinding
import com.example.translatorapp.screen.history.HistoryFragment
import com.example.translatorapp.screen.setting.SettingFragment
import com.example.translatorapp.screen.test.TestFragment
import com.example.translatorapp.screen.translate.TranslateFragment
import com.example.translatorapp.util.NetworkUtils
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

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_setting -> {
                    binding.drawerLayout.close()
                    val translateFragment =
                        supportFragmentManager.findFragmentByTag(Constant.TAG_TRANSLATE)
                    if (translateFragment is TranslateFragment) {
                        addFragment(
                            fragment = SettingFragment.newInstance(translateFragment.speak),
                            addToBackStack = true,
                            container = findLayoutContainer()
                        )
                    }
                }
                R.id.menu_test -> {
                    addFragment(
                        fragment = TestFragment.newInstance(),
                        addToBackStack = true,
                        container = findLayoutContainer()
                    )
                }
                R.id.menu_history -> {
                    addFragment(
                        fragment = HistoryFragment.newInstance(),
                        addToBackStack = true,
                        container = findLayoutContainer()
                    )
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.fragments
        val length = fragments.size - 1
        var backFlag = true
        var popFlag = true
        for (index in length downTo 0) {
            val childFragManager = fragments[index].childFragmentManager
            if (childFragManager.backStackEntryCount > 0) {
                if (fragments[index].isVisible && popFlag && fragments[index] is TranslateFragment) {
                    childFragManager.popBackStack()
                    return
                }
                backFlag = false
                if (fragments[index].isVisible && fragments[index] is TestFragment) {
                    backFlag = true
                }
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

    private fun restoreState(instanceState: Bundle?) {
        if (instanceState == null) {
            if (NetworkUtils.isNetworkAvailable(applicationContext)) {
                addFragment(
                    fragment = TranslateFragment.newInstance(),
                    addToBackStack = false,
                    container = findLayoutContainer(),
                    tag = Constant.TAG_TRANSLATE
                )
            } else {
                NetworkUtils.setDialogAction(applicationContext) { restoreState(instanceState) }
            }
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

    fun enableItemToolbar(visibility: Boolean) {
        binding.layoutMain.textTitle.isVisible = visibility
        if (visibility) {
            binding.layoutMain.toolbar.setNavigationIcon(R.drawable.ic_back)
        } else {
            binding.layoutMain.toolbar.navigationIcon = null
        }
    }

    fun findLayoutContainer() = binding.layoutMain.container.frameLayoutContainer.id
}
