package io.upify.utils.ui.extensions

import android.widget.TextView

/**
 * Created by egenesio on 11/04/2018.
 */

/**
 * TEXT VIEW
 */

var TextView.stringResource: String
    set(value) {
        context.resIdFrom(value)?.let {
            setText(it)
        } ?: run {
            text = value
        }
    }

    get() = text.toString() //TODO fix


fun TextView.setTextOrGone(string: String?) {
    if (string.isNullOrBlank()) {
        state = Visibility.GONE
    } else {
        text = string
        state = Visibility.VISIBLE
    }

}