package io.upify.utils.general

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
/**
 * Created by egenesio on 12/04/2018.
 */
class Utils private constructor() {

    companion object {
        val instance: Utils by lazy { Utils() }

        fun init(context: Context): Utils {
            instance.context = context.applicationContext
            return instance
        }
    }

    lateinit var context: Context
        private set

    var errorTitle: String = ""
    var errorMessage: String = ""
    var errorOkButton: String = ""
    var errorRetryButton: String = ""

    var errorMustLogoutBlock: ( (activity: AppCompatActivity) -> Unit ) = {}

    val strTimeZoneData: String = "UTC"
    val strDateFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    init {

    }

    private val timeZoneData: TimeZone by lazy { TimeZone.getTimeZone(strTimeZoneData) }
    val dateFormat: SimpleDateFormat by lazy { SimpleDateFormat(strDateFormat).apply { timeZone = timeZoneData } }
}