package io.upify.utils.ui.extensions

import android.content.Intent
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import io.upify.utils.domain.NetworkErrorBase
import io.upify.utils.extensions.completeUrl

/**
 * Created by egenesio on 11/04/2018.
 */

/**
 * Fragment
 */

fun Fragment.openUrl(url: String?) {
    //url?.let{ activity?.browse(it)}
    url?.let {startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.completeUrl)))}
}

fun Fragment.share(url: String) {
    val sendIntent = Intent().also {
        it.action = Intent.ACTION_SEND
        it.type = "text/plain"
        //it.putExtra(Intent.EXTRA_SUBJECT, "Shared")
        it.putExtra(Intent.EXTRA_TEXT, url)
    }

    startActivity(sendIntent)
}

fun Fragment.stringOrNull(res: Int?): String? {
    res?.let { return getString(it) }
    return null
}

fun Fragment.alertIfError(error: NetworkErrorBase?, retryBlock: (()-> Unit)? = null, okBlock: (()-> Unit)? = null) {
    (activity as? AppCompatActivity)?.alertIfError(error, retryBlock, okBlock)
}

fun Fragment.alertError(messageRes: Int?= null, messageString: String? = null, errorTitle: String? = null, retryBlock: (()-> Unit)? = null, okBlock: (()-> Unit)? = null ) {
    (activity as? AppCompatActivity)?.alertError(messageRes, messageString, errorTitle, retryBlock, okBlock)
}