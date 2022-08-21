package com.example.translatorapp.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.addFragment(
    fragment: Fragment,
    addToBackStack: Boolean,
    container: Int,
    tag: String? = null
) {
    supportFragmentManager.beginTransaction().apply {
        add(container, fragment, tag)
        if (addToBackStack) {
            addToBackStack(null)
        }
        commit()
    }
}
