package io.upify.utils.ui.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import io.upify.utils.domain.NetworkErrorBase
import io.upify.utils.extensions.completeUrl
import io.upify.utils.general.Utils
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity

/**
 * Created by egenesio on 11/04/2018.
 */
/**
 * AppCompatActivity
 */

fun AppCompatActivity.openUrl(url: String?) {
    //url?.let{ browse(it)}
    url?.let {startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.completeUrl)))}
}

fun Context.openUrl(url: String?) {
    //url?.let{ browse(it)}
    url?.let {startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.completeUrl)))}
}

fun AppCompatActivity.stringOrNull(res: Int?): String? {
    res?.let { return getString(it) }
    return null
}

fun AppCompatActivity.alertIfError(error: NetworkErrorBase?, retryBlock: (()-> Unit)? = null, okBlock: (()-> Unit)? = null) {
    error ?: return

    val block: (() -> Unit)? = if (error.mustLogout) {
        {
            Utils.instance.errorMustLogoutBlock.invoke(this)
        }
    } else {
        okBlock
    }

    alertError(messageString = error.message, retryBlock = retryBlock, okBlock = block)
}

fun AppCompatActivity.alertError(messageRes: Int?= null, messageString: String? = null, errorTitle: String? = null, retryBlock: (()-> Unit)? = null, okBlock: (()-> Unit)? = null ) {
    alert {
        title = errorTitle ?: Utils.instance.errorTitle
        message = messageString ?: stringOrNull(messageRes) ?: Utils.instance.errorMessage
        positiveButton(Utils.instance.errorOkButton) {
            okBlock?.invoke()
        }
        retryBlock?.let { block ->
            neutralPressed(Utils.instance.errorRetryButton) {
                it.dismiss()
                block.invoke()
            }
        }

    }.show()
}