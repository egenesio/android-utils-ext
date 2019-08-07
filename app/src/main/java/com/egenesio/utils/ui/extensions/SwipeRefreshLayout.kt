package com.egenesio.utils.ui.extensions

import com.egenesio.utils.R

/**
 * Created by egenesio on 30/07/2018.
 */
/**
 * SWIPE REFRESH LAYOUT
 */

fun androidx.swiperefreshlayout.widget.SwipeRefreshLayout.init() {
    //setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent)
    setTag(R.id.tag_init, true)
}

fun androidx.swiperefreshlayout.widget.SwipeRefreshLayout.show() {
    if ((getTag(R.id.tag_init) as? Boolean) != true) init()
    if (!isRefreshing) isRefreshing = true
}

fun androidx.swiperefreshlayout.widget.SwipeRefreshLayout.hide() {
    if (isRefreshing) isRefreshing = false
}
