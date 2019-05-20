package io.upify.utils.ui.extensions

import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Fade
import androidx.transition.Slide
import io.upify.utils.R
import io.upify.utils.ui.utils.FragmentNavigationGroup

/**
 * Created by egenesio on 11/04/2018.
 */

enum class FragmentAnim {
    TYPE_NO_ANIMATION,
    TYPE_MODAL,
    TYPE_NAVIGATION;

    val inAnim: Int get() = when(this){
        TYPE_MODAL -> R.anim.anim_inner_frag_in
        TYPE_NAVIGATION -> R.anim.anim_inner_frag_in_side
        TYPE_NO_ANIMATION -> 0
    }

    val outAnim: Int get() = when(this){
        TYPE_MODAL -> R.anim.anim_inner_frag_out
        TYPE_NAVIGATION -> R.anim.anim_inner_frag_out_side
        TYPE_NO_ANIMATION -> 0
    }
}

val FragmentManager.lastFragment get() = this.fragments.lastOrNull()

fun FragmentManager.replaceFragment(
    fragment: Fragment,
    fragmentNavigationGroup: FragmentNavigationGroup){

    //println("replaceFragment")
    //println(fragment)
    //println(fragment.isAdded)

    if (fragment.isAdded) return

    fragment.enterTransition = fragmentNavigationGroup.enterTransition
    lastFragment?.exitTransition = fragmentNavigationGroup.exitTransition

    //fragment.allowEnterTransitionOverlap

    val transaction = beginTransaction()
        .replace(fragmentNavigationGroup.parent, fragment)

    if (fragmentNavigationGroup.addToBackStack) transaction.addToBackStack(null)

    transaction.commit()
}

inline fun FragmentManager.replaceFragment(
    fragment: Fragment,
    parent: Int,
    type: FragmentAnim = FragmentAnim.TYPE_NO_ANIMATION,
    tag: String? = null){

    fragment.enterTransition = when(type) {
        FragmentAnim.TYPE_NO_ANIMATION -> Fade()
        FragmentAnim.TYPE_MODAL -> Slide()
        FragmentAnim.TYPE_NAVIGATION -> Slide(Gravity.END)
    }

    this.fragments.lastOrNull()?.exitTransition = when(type) {
        FragmentAnim.TYPE_NO_ANIMATION -> Fade()
        FragmentAnim.TYPE_MODAL -> Slide()
        FragmentAnim.TYPE_NAVIGATION -> Slide(Gravity.START)
    }

    //fragment.allowEnterTransitionOverlap

    val transaction = beginTransaction()
        //.setCustomAnimations(type.inAnim, 0, 0, type.outAnim)
        .replace(parent, fragment)
        .addToBackStack(null)

    //tag?.let { transaction.addToBackStack(null) }

    transaction.commit()
}

inline fun FragmentManager.showAndRemoveFragment(fragmentToRemove: Fragment, fragmentToAdd: Fragment, parent: Int, type: FragmentAnim = FragmentAnim.TYPE_NO_ANIMATION, tag: String? = null){
    val transaction = beginTransaction()
        //.setCustomAnimations(0, 0, 0, 0)
        .add(parent, fragmentToAdd, tag)
        .remove(fragmentToRemove)
        .addToBackStack(null)

    //tag?.let { transaction.addToBackStack(null) }

    transaction.commit()
}

inline fun FragmentManager.showFragment(fragment: Fragment, parent: Int, type: FragmentAnim = FragmentAnim.TYPE_NO_ANIMATION, tag: String? = null){
    //if (fragment.isAdded) remove(fragment, tag != null)

    if (fragment.isAdded) return

    val transaction = beginTransaction()
        //.setCustomAnimations(type.inAnim, 0, 0, type.outAnim)
        .add(parent, fragment, tag)
    tag?.let { transaction.addToBackStack(tag) }

    transaction.commit()
}

inline fun FragmentManager.remove(fragment: Fragment, fromBackStack: Boolean = false){
    if (fromBackStack){
        popBackStackImmediate()
    } else {
        beginTransaction().setCustomAnimations(0, 0, 0, 0).remove(fragment).commit()
    }
}