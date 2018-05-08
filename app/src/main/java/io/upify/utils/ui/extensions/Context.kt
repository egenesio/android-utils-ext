package io.upify.utils.ui.extensions

import android.content.Context

/**
 * Created by egenesio on 11/04/2018.
 */

/**
 * Context
 */

fun Context.stringFrom(string: String): String? {
    return resIdFrom(string)?.let {
        resources.getString(it)
    } ?: run {
        null
    }
}

fun Context.resIdFrom(string: String): Int? {
    val resId = resources.getIdentifier(string, "string", "") //TODO get package
    return if (resId != 0) resId else null
}