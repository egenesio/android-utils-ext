package io.upify.utils.ui.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.upify.utils.R

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

inline fun FragmentManager.showFragment(fragment: Fragment, parent: Int, type: FragmentAnim = FragmentAnim.TYPE_NO_ANIMATION, tag: String? = null){
    //if (fragment.isAdded) remove(fragment, tag != null)

    val transaction = beginTransaction().setCustomAnimations(type.inAnim, 0, 0, type.outAnim).add(parent, fragment, tag)
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