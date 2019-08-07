package com.egenesio.utils.ui.extensions

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.egenesio.utils.domain.NetworkErrorBase
import com.egenesio.utils.extensions.completeUrl

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

fun Fragment.hideKeyboard() {
    val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Fragment.runOnUiThread(action: () -> Unit) {
    if (activity == null) return
    if (activity?.isActivityDestroyed() == true) return

    activity?.runOnUiThread(action)
}

fun Fragment.findLocation(timeout: Long? = null, timeInterval: Long = 0, listener: (location: Location?) -> Unit) {
    (activity as? AppCompatActivity)?.findLocation(timeout, timeInterval, listener)
}

fun Fragment.hasAccessTo(permission: String) = (activity as? AppCompatActivity)?.hasAccessTo(permission) ?: false //ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED