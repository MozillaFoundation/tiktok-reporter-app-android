package org.mozilla.tiktokreporter.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

val emptyCallback: () -> Unit = { }

object Common {
    const val PREFERENCES_TERMS_ACCEPTED_KEY = "terms_accepted"
    const val PREFERENCES_ONBOARDING_COMPLETED_KEY = "onboarding_completed"
    const val PREFERENCES_SELECTED_STUDY_KEY = "selected_study"
    const val PREFERENCES_USER_EMAIL_KEY = "user_email"
}

inline fun <T> onSdkVersionAndUp(version: Int, block: () -> T) : T? {
    return if (Build.VERSION.SDK_INT >= version) {
        block()
    } else null
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}