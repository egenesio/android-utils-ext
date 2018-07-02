package io.upify.utils.extensions

import android.support.v7.app.AppCompatActivity
import io.upify.utils.domain.APIResult
import io.upify.utils.domain.NetworkErrorBase
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by egenesio on 11/04/2018.
 */
/**
 * General
 */

typealias TimeInterval = Long
val timeNow: Long get() = System.currentTimeMillis() / 1000

/**
 * List
 */
val <T> List<T>.first: T? get() {
    if (isEmpty()) return null
    return this[0]
}

val <T> List<T>.last: T? get() {
    if (isEmpty()) return null
    return this[lastIndex]
}


/**
 * Double
 */
operator fun Double?.compareTo(other: Double): Int {
    this?.let {
        return it.compareTo(other)
    }
    return -10
}

/**
 * Int
 */
operator fun Int?.compareTo(other: Int): Int {
    this?.let {
        return it.compareTo(other)
    }
    return 0
}

/**
 * Long
 */

val Long.stringDateTime: String get() = try {
    val sdf = SimpleDateFormat("dd.MM.yyyy - HH:mm") //TODO change this
    val netDate = Date(this * 1000)
    sdf.format(netDate)
} catch (ex: Exception) {
    ""
}

val Long.stringDate: String get() = try {
    val sdf = SimpleDateFormat("dd.MM.yyyy") //TODO change this
    val netDate = Date(this * 1000)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    sdf.format(netDate)
} catch (ex: Exception) {
    ""
}

val Long.stringTime: String get() = try {
    val sdf = SimpleDateFormat("HH:mm") //TODO change this
    val netDate = Date(this * 1000)
    sdf.format(netDate)
} catch (ex: Exception) {
    ""
}

/**
 * String
 */

val String.completeUrl: String get() {
    if (this.startsWith("http://") || this.startsWith("https://")) return this
    return "http://" + this
}

val String.viewerUrl: String get() = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + this

val String.lastPathComponent: String? get() = this.split("/").last

fun String?.toFile(): File? {
    this?.let {
        FileReader(File(it)).use {  }
        return File(it)
    }
    return null
}

inline fun<S, T, R> Pair<S?, T?>.let(block: (S, T) -> R): R? {
    first?.let { fi ->
        second?.let { se ->
            return block(fi, se)
        }
    }

    return null
}

inline fun<R> test(vararg values: Any?, block: (values: List<Any>) -> R): R? {

    val result: MutableList<Any> = mutableListOf()

    values.forEach {
        if (it == null) return null
        result.add(it)
    }

    return block(result)
}

inline fun <T: APIResult?, E: NetworkErrorBase?> AppCompatActivity.doSomething(noinline block: (T, E) -> Unit): (T, E) -> Unit {
    if (this.title == "A") {
        return block
    }
    return {a,b -> println("else")}
}


/*title = if (title == "B") "B" else "A"

SessionManager.instance.start(txtStudentNumber.inputText, it, doSomething { t, e ->
    println(t)
    println(e)

    title = "B"
})*/