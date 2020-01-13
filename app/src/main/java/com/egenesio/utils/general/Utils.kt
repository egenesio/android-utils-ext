package com.egenesio.utils.general

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
/**
 * Created by egenesio on 12/04/2018.
 */
object Utils {

    //val instance: Utils by lazy { Utils() }

    fun init(context: Context, buildType: String, applicationId: String): Utils {
        this.context = context.applicationContext
        this.buildType = buildType
        this.applicationId = applicationId

        return this
    }

    lateinit var context: Context //TODO remove
        private set

    var buildType: String? = null
        private set

    var applicationId: String? = null
        private set

    var errorTitle: String = ""
    var errorMessage: String = ""
    var errorOkButton: String = ""
    var errorRetryButton: String = ""

    var errorMustLogoutBlock: ( (activity: AppCompatActivity) -> Unit ) = {}

    var strDateFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    var strDateTimeZone: String = "UTC"

    var strTimeZoneData: String = "UTC"
    var strDateFormattedShort: String = "dd/MM/yyyy"
    var strTimeFormatted: String = "HH:mm"

    private val timeZoneData: TimeZone by lazy { TimeZone.getTimeZone(strTimeZoneData) }
    val dateFormat: SimpleDateFormat by lazy { SimpleDateFormat(strDateFormat).apply { timeZone = timeZoneData } }


    val gson: Gson by lazy { JsonUtils.buildGson() }
    val jsonParser: JsonParser by lazy { JsonUtils.buildJsonParser() }

}