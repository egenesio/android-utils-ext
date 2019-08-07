package com.egenesio.utils.ui.extensions

import android.content.Context
import com.egenesio.utils.domain.APIResult
import com.egenesio.utils.networking.APIClientBase
import java.lang.Exception
import java.nio.charset.StandardCharsets

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

fun Context.stringFromRaw(rawId: Int): String? {
    return try {

        val inputStream = resources.openRawResource(rawId)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        String(buffer, StandardCharsets.UTF_8)

    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

inline fun <reified T: APIResult> Context.parseJsonFromRaw(rawId: Int): T? {
    val gson = APIClientBase.gson
    return stringFromRaw(rawId)?.let {
        gson.fromJson<T>(it, T::class.java)
    }
}