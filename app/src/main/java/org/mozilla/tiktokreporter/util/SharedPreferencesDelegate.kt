package org.mozilla.tiktokreporter.util

import android.content.Context
import androidx.activity.ComponentActivity
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferencesDelegate<T>(
    private val context: Context,
    private val name: String,
    private val defaultValue: T
): ReadWriteProperty<Any, T> {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(name, ComponentActivity.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when (defaultValue) {
            is String -> {
                val value = sharedPreferences.getString(name, defaultValue) ?: defaultValue
                value as T
            }
            is Boolean -> {
                val value = sharedPreferences.getBoolean(name, defaultValue)
                value as T
            }
            else -> defaultValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        when (value) {
            is String -> {
                sharedPreferences.edit().putString(name, value).apply()
            }
            is Boolean -> {
                sharedPreferences.edit().putBoolean(name, value).apply()
            }
            else -> Unit
        }
    }
}

fun <T> Context.sharedPreferences(name: String, defaultValue: T) = SharedPreferencesDelegate(this, name, defaultValue)