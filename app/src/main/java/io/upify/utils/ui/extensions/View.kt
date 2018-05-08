package io.upify.utils.ui.extensions

import android.view.View

/**
 * Created by egenesio on 11/04/2018.
 */
/**
 * VIEW
 */

enum class Visibility {
    VISIBLE, INVISIBLE, GONE
}

var View.state: Visibility

    set(value) = when(value) {
        Visibility.GONE -> visibility = View.GONE
        Visibility.INVISIBLE -> visibility = View.INVISIBLE
        Visibility.VISIBLE -> visibility = View.VISIBLE
    }

    get() = when(visibility) {
        View.GONE -> Visibility.GONE
        View.INVISIBLE -> Visibility.INVISIBLE
        View.VISIBLE -> Visibility.VISIBLE
        else -> Visibility.VISIBLE
    }