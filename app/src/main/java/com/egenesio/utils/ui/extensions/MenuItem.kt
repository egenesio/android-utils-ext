package com.egenesio.utils.ui.extensions

import android.view.MenuItem

/**
 * Created by egenesio on 11/04/2018.
 */

/**
 * MENU ITEM
 */

var MenuItem.enabled: Boolean
    set(value) {
        isEnabled = value
        icon?.alpha = if (value) 255 else 125
    }

    get() = isEnabled
