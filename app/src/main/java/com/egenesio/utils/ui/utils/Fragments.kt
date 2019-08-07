package com.egenesio.utils.ui.utils

import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import com.egenesio.utils.ui.extensions.lastFragment
import com.egenesio.utils.ui.extensions.replaceFragment

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

enum class ModalNavigationType {
    SLIDE, FADE;

    val enterTransition get() = when(this) {
        SLIDE -> Slide(Gravity.BOTTOM)
        FADE -> Fade()
    }

    val returnTransition get() = when(this) {
        SLIDE -> Slide(Gravity.BOTTOM)
        FADE -> Fade()
    }
}

data class ModalNavigationGroup(
    val fragmentManager: FragmentManager,
    val overLayout: Int,
    val key: String) {

    companion object {
        private val DEFAULT_TYPE = ModalNavigationType.SLIDE
    }

    fun presentRoot(fragment: Fragment, withType: ModalNavigationType? = null) {
        if (fragment.isAdded) return

        val type = withType ?: DEFAULT_TYPE
        fragment.enterTransition = type.enterTransition

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

    fun dismiss(withType: ModalNavigationType? = null) {
        val type = withType ?: DEFAULT_TYPE
        fragmentManager.lastFragment?.returnTransition = type.returnTransition
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