package com.egenesio.utils.extensions

import com.egenesio.utils.general.Utils

val Any?.isNull get() = this == null
val Any?.isNotNull get() = this != null


inline fun onDebug(f: ()-> Unit) {
    val buildType = Utils.instance.buildType ?: return
    if (buildType == "debug") {
        f()
    }
}

inline fun notOnDebug(f: ()-> Unit) {
    val buildType = Utils.instance.buildType ?: return
    if (buildType != "debug") {
        f()
    }
}

inline fun onRelease(f: ()-> Unit) {
    val buildType = Utils.instance.buildType ?: return
    if (buildType == "release") {
        f()
    }
}