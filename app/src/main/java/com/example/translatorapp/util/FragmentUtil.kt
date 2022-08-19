package com.example.translatorapp.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun addFragment(
    fragment: Fragment,
    addToBackStack: Boolean,
    container: Int,
    manager: FragmentManager,
    tag: String? = null
) {
    manager.beginTransaction().apply {
        add(container, fragment, tag)
        if (addToBackStack) {
            addToBackStack(null)
        }
        commit()
    }
}
