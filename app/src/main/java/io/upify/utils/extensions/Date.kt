package io.upify.utils.extensions

import android.annotation.SuppressLint
import io.upify.utils.general.Utils
import java.lang.ref.WeakReference
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by egenesio on 13/04/2018.
 */

typealias DateString = String

val DateString.timestampMillis: Long? get() {
    return date?.time
}

val DateString.timestamp: Long? get() {
    return date?.let { it.time / 1000 } ?: null
}

val DateString.date: Date? get() = date()

@SuppressLint("SimpleDateFormat")
fun DateString.date(customFormat: String? = Utils.instance.strDateFormat): Date? = try {
    SimpleDateFormat(customFormat).parse(this)
} catch (e: ParseException) {
    e.printStackTrace()
    null
}

@SuppressLint("SimpleDateFormat")
fun Date.formatted(customFormat: String? = null): String {
    val df = SimpleDateFormat(customFormat ?: Utils.instance.strDateFormattedShort)
    df.timeZone = TimeZone.getTimeZone(Utils.instance.strTimeZoneData)
    return df.format(this)
}

val Date.formatted: String get() {
    return this.formatted()
}

val Date.timeFormatted: String get() {
    return this.formatted(Utils.instance.strTimeFormatted)
}

val Calendar.formatted: String get() = time.formatted
fun Calendar.formatted(customFormat: String? = null): String = time.formatted(customFormat)

val Calendar.unixTimestamp: Long get() = time.time / 1000


fun Calendar.isToday(date: Date): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return isToday(calendar)
}

fun Calendar.isToday(calendar: Calendar): Boolean {
    return get(Calendar.DATE) == calendar.get(Calendar.DATE) &&
            get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
            get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
}


fun Long.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this * 1000)
    return calendar
}

/*
public static String toISO8601UTC(Date date) {
  TimeZone tz = TimeZone.getTimeZone("UTC");
  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
  df.setTimeZone(tz);
  return df.format(date);
}

public static Date fromISO8601UTC(String dateStr) {
  TimeZone tz = TimeZone.getTimeZone("UTC");
  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
  df.setTimeZone(tz);

  try {
    return df.parse(dateStr);
  } catch (ParseException e) {
    e.printStackTrace();
  }

  return null;
}
 */

fun foo(body: WeakReference<() -> Unit>){
    // we check here if body  is still alive
}