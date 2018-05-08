package io.upify.utils.ui.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

/**
 * Created by egenesio on 11/04/2018.
 */

/**
 * IMAGE VIEW
 */

enum class ImageScaleType {
    FIT_CENTER, CENTER_CROP
}

enum class ImageType(val id: Int) {
    REMOTE(1), LOCAL(2)
}

val Pair<String?, ImageType>.localFile: File? get() {
    first?.let {
        return if (second == ImageType.LOCAL) File(it) else null
    } ?: kotlin.run {
        return null
    }
}

val Pair<String?, ImageType>.hasLocalFile: Boolean get() {
    first?.let {
        return second == ImageType.LOCAL
    } ?: kotlin.run {
        return false
    }
}

fun ImageView.set(url: String?, scaleType: ImageScaleType, type: ImageType = ImageType.REMOTE, errorRes: Int? = null) {

    val remote = when(type) {
        ImageType.REMOTE -> Glide.with(this.context).load(url)
        ImageType.LOCAL -> if (url != null) Glide.with(this.context).load(File(url)) else Glide.with(this.context).load(url as? String?)
    }

    errorRes?.let { remote.error(it) }

    when(scaleType) {
        ImageScaleType.FIT_CENTER -> remote.fitCenter()
        ImageScaleType.CENTER_CROP -> remote.centerCrop()
    }

    remote.into(this)
}

var ImageView.tintColor: Int

    set(value) {
        setColorFilter(resources.getColor(value), android.graphics.PorterDuff.Mode.MULTIPLY )
    }

    get() = 0