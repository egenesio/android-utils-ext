package io.upify.utils.extensions

val Any?.isNull get() = this == null
val Any?.isNotNull get() = this != null