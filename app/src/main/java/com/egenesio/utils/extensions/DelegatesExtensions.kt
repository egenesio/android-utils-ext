package com.egenesio.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.egenesio.utils.general.Utils
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability

/**
 * Created by egenesio on 12/04/2018.
 */
object DelegatesExt {
    fun <T> preference(name: String, default: T) = PreferenceProperty(name, default)
}

class PreferenceProperty<T>(private val name: String, private val default: T) {

    companion object {
        val PREFERENCES_NAME = "custom_preferences"
    }

    operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ReadWriteProperty<Any, T> {
        val result: Any = if (property.returnType.isMarkedNullable) NullablePreferenceProperty(name, default) else NotNullPreferenceProperty(name, default)
        @Suppress("UNCHECKED_CAST")
        return result as ReadWriteProperty<Any, T>
    }
}

private class NotNullPreferenceProperty<T>(private val name: String, private val default: T) : ReadWriteProperty<Any, T> {

    private val prefs: SharedPreferences by lazy {
        Utils.context.getSharedPreferences(PreferenceProperty.PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun findPreference(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default) ?: ""
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

        return res as T
    }

    @SuppressLint("CommitPrefEdits")
    private fun putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }
}

private class NullablePreferenceProperty<T>(private val name: String, private val default: T?) : ReadWriteProperty<Any, T?> {

    private val prefs: SharedPreferences by lazy {
        Utils.context.getSharedPreferences(PreferenceProperty.PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return findPreference(name, default, property)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        putPreference(name, value, property)
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun findPreference(name: String, default: T?, property: KProperty<*>): T? = with(prefs) {

        val res = when(property.returnType.withNullability(false)) {
            Long::class.createType() -> {
                val saved = getLong(name, -1000L)
                if (saved == -1000L) default else saved
            }
            String::class.createType() -> {
                val saved = getString(name, "")
                if (saved.isNullOrEmpty()) default else saved

            }
            Int::class.createType() -> {
                val saved = getInt(name, -1000)
                if (saved == -1000) default else saved
            }
        /*Boolean::class.createType() -> {
            println("boolean")

            getBoolean()
        }*/
            Float::class.createType() -> {
                val saved = getFloat(name, -1000f)
                if (saved == -1000f) default else saved
            }
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

        return res as T?
    }

    @SuppressLint("CommitPrefEdits")
    private fun putPreference(name: String, value: T?, property: KProperty<*>) = with(prefs.edit()) {

        when(property.returnType.withNullability(false)) {
            Long::class.createType() -> {
                putLong(name, value as? Long ?: -1000L)
            }
            String::class.createType() -> {
                putString(name, value as? String)
            }
            Int::class.createType() -> {
                putInt(name, value as? Int ?: -1000)
            }
            Float::class.createType() -> {
                putFloat(name, value as? Float ?: -1000f)
            }
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }.apply()
    }
}