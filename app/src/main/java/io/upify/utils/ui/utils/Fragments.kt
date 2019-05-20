package io.upify.utils.ui.utils

import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Slide
import androidx.transition.Transition
import io.upify.utils.ui.extensions.FragmentAnim
import io.upify.utils.ui.extensions.lastFragment
import io.upify.utils.ui.extensions.replaceFragment

data class FragmentNavigationGroup(

    val parent: Int,
    val enterTransition: Transition?,
    val returnTransition: Transition?,
    val exitTransition: Transition,
    val addToBackStack: Boolean,
    var fragmentManager: FragmentManager

    ) {

    fun replaceWith(fragment: Fragment) {
        fragmentManager.replaceFragment(fragment, this)
    }

}

data class ModalNavigationGroup(
    val fragmentManager: FragmentManager,
    val overLayout: Int,
    val key: String) {

    fun presentRoot(fragment: Fragment) {
        if (fragment.isAdded) return

        fragment.enterTransition = Slide(Gravity.BOTTOM)

        fragmentManager.beginTransaction()
            .add(overLayout, fragment, key)
            .addToBackStack(key)
            .commit()
    }

    fun navigateTo(fragment: Fragment) {
        if (fragment.isAdded) return

        fragment.enterTransition = Slide(Gravity.END)
        fragmentManager.lastFragment?.exitTransition = Slide(Gravity.START)

        fragmentManager.beginTransaction()
            .replace(overLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateBack() {
        fragmentManager.popBackStackImmediate()
    }

    fun dismiss() {
        fragmentManager.lastFragment?.returnTransition = Slide(Gravity.BOTTOM)

        fragmentManager.popBackStackImmediate(key, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}

/*
fun FragmentManager.showFragment(overLayout: Int, fragment: Fragment, tag: String){
    if (fragment.isAdded) return

    val transaction = beginTransaction()
        .add(overLayout, fragment, tag)
        .addToBackStack(tag)
        .commit()
}*/