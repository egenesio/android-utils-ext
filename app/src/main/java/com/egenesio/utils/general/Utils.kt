package com.egenesio.utils.general

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
/**
 * Created by egenesio on 12/04/2018.
 */
class Utils private constructor() {

    companion object {
        val instance: Utils by lazy { Utils() }

        fun init(context: Context, buildType: String): Utils {
            instance.context = context.applicationContext
            instance.buildType = buildType

            return instance
        }
    }

    lateinit var context: Context
        private set

    var buildType: String? = null
        private set

    var errorTitle: String = ""
    var errorMessage: String = ""
    var errorOkButton: String = ""
    var errorRetryButton: String = ""

    var errorMustLogoutBlock: ( (activity: AppCompatActivity) -> Unit ) = {}

    var strTimeZoneData: String = "UTC"
    var strDateFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    var strDateFormattedShort: String = "dd/MM/yyyy"
    var strTimeFormatted: String = "HH:mm"

    init {

    }

    private val timeZoneData: TimeZone by lazy { TimeZone.getTimeZone(strTimeZoneData) }
    val dateFormat: SimpleDateFormat by lazy { SimpleDateFormat(strDateFormat).apply { timeZone = timeZoneData } }
}