package com.akshay.statuscatch.utils

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.akshay.statuscatch.R

fun Activity.replaceFragment(fragment: Fragment, args: Bundle? = null, addToBackStack: Boolean = false, reverseAnimation: Boolean = false) {
    val fragmentActivity = this as FragmentActivity
    fragmentActivity.supportFragmentManager.beginTransaction().apply {
        // Use custom slide animations to avoid blink
        if (reverseAnimation) {
            setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else {
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        args?.let {
            fragment.arguments = it
        }
        replace(R.id.fragment_container, fragment)
        if (addToBackStack) addToBackStack(null)
    }.commit()
}