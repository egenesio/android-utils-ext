package io.upify.utils.extensions

import io.upify.utils.general.Utils
import java.lang.ref.WeakReference
import java.text.ParseException
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

val DateString.date: Date? get() {
    return try {
        Utils.instance.dateFormat.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
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