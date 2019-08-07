package com.egenesio.utils.ui.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.egenesio.utils.domain.NetworkErrorBase
import com.egenesio.utils.extensions.completeUrl
import com.egenesio.utils.general.Utils
import org.jetbrains.anko.alert
import java.util.*

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

fun AppCompatActivity.hideKeyboard() {
    val view = window.decorView
    val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}


fun AppCompatActivity.hasAccessTo(permission: String) = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun FragmentActivity.isActivityDestroyed(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed) {
        return true
    }

    return false
}

val AppCompatActivity.MAX_SCREEN_BRIGHTNESS: Int get() = 255

//Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);  //this will load the automatic mode on
//Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  //this will load the manual mode (load the automatic mode off)

var AppCompatActivity.screenBrightnessMode: Int
    get() = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) return
        if (!listOf(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC).contains(value)) return

        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, value)
    }

var AppCompatActivity.screenBrightness: Int
    get() = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0)
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) return

        if (screenBrightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            screenBrightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        }

        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, value)
    }


//permission management screen
fun AppCompatActivity.showPermissionsScreen(permission: String) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

    val intent = Intent(permission)
    intent.data = Uri.parse("package:$packageName")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

@SuppressLint("MissingPermission")
fun AppCompatActivity.findLocation(timeout: Long? = null, timeInterval: Long = 0, listener: (location: Location?) -> Unit) {

    var listenerCalled = false

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return listener(null)
    }

    val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)

    if (providers.isEmpty()){
        listener(null)
        return
    }

    val locationListeners: MutableList<LocationListener> = mutableListOf()
    var timer: Timer? = null

    timeout?.let {

        timer = Timer()
        timer!!.schedule(object: TimerTask() {

            override fun run() {
                if (!listenerCalled) {
                    listenerCalled = true
                    locationListeners.forEach { locationManager.removeUpdates(it) }
                    runOnUiThread { listener(null) }
                } else return
            }

        }, it * 1000)
    }

    providers.forEach {

        val locationListener = object: LocationListener {

            override fun onLocationChanged(location: Location?) {
                if (listenerCalled) {
                    locationManager.removeUpdates(this)
                    return
                }

                location?.let {
                    listenerCalled = true
                    timer?.cancel()
                    locationManager.removeUpdates(this)
                    listener(location)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) =  if (listenerCalled) locationManager.removeUpdates(this) else Unit

            override fun onProviderEnabled(provider: String?) =  if (listenerCalled) locationManager.removeUpdates(this) else Unit

            override fun onProviderDisabled(provider: String?) = if (listenerCalled) locationManager.removeUpdates(this) else Unit
        }

        locationListeners.add(locationListener)
        locationManager.requestLocationUpdates(it, timeInterval, 0f, locationListener)
    }
}