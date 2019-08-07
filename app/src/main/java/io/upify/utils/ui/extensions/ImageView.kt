package io.upify.utils.ui.extensions

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.lang.Exception

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

fun ImageView.load(url: String?, scaleType: ImageScaleType = ImageScaleType.FIT_CENTER, type: ImageType = ImageType.REMOTE, errorRes: Int? = null) {


    //Glide.with(this.context.applicationContext).load(url).into(this)

    val remote = when(type) {
        ImageType.REMOTE -> Glide.with(this.context.applicationContext).load(url)
        ImageType.LOCAL -> if (url != null) Glide.with(this.context.applicationContext).load(File(url)) else Glide.with(this.context).load(url as? String?)
    }



    /*errorRes?.let { remote.error(it) }
    //TODO: check this
    when(scaleType) {
        ImageScaleType.FIT_CENTER -> remote.fitCenter()
        ImageScaleType.CENTER_CROP -> remote.centerCrop()
    }*/

    remote.into(this)
}

var ImageView.tintColor: Int

    set(value) {
        setColorFilter(resources.getColor(value), android.graphics.PorterDuff.Mode.MULTIPLY )
    }

    get() = 0

fun ImageView.setQRCode(code: String, onCompletion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
    doAsync {
        val writer = QRCodeWriter()
        val codeWidth = (width * 0.5).toInt()
        val codeHeight = (height * 0.5).toInt()

        var bitmap: Bitmap? = null
        var exception: Exception? = null

        try {
            val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, codeWidth, codeHeight)
            val width = bitMatrix.width
            val height = bitMatrix.height
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0..width - 1) {
                for (y in 0..height - 1) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            exception = e
            bitmap = null

        } finally {
            uiThread {
                if (bitmap != null) setImageBitmap(bitmap)
                onCompletion(bitmap, exception)
            }
        }
    }
}