package com.example.translatorapp.util

import androidx.fragment.app.Fragment
import com.example.translatorapp.R

fun Fragment.addFragmentToParent(
    fragment: Fragment,
    addToBackStack: Boolean,
    container: Int?,
    addToActivity: Boolean = false,
    tag: String? = null
) {
    var manager = parentFragmentManager
    if (addToActivity) {
        parentFragment?.let {
            manager = it.parentFragmentManager
        }
    }
    manager.beginTransaction().apply {
        container?.let {
            add(container, fragment, tag)
            if (addToBackStack) {
                addToBackStack(null)
            }
            commit()
        }
    }
}

fun Fragment.addFragmentToChild(
    fragment: Fragment,
    addToBackStack: Boolean,
    container: Int,
    customAnimation: Boolean
) {
    childFragmentManager.beginTransaction().apply {
        if (customAnimation) {
            setCustomAnimations(
                R.anim.right_to_left,
                R.anim.right_to_left,
                R.anim.out_left,
                R.anim.out_left
            )
        }
        add(container, fragment)
        if (addToBackStack) {
            addToBackStack(null)
        }
        commit()
    }
}
