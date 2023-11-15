package org.mozilla.tiktokreporter.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val emptyCallback: () -> Unit = { }

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_flags")

object Common {
    const val PREFERENCES_TERMS_ACCEPTED_KEY = "terms_accepted"
    const val PREFERENCES_ONBOARDING_COMPLETED_KEY = "onboarding_completed"
    const val PREFERENCES_SELECTED_STUDY_KEY = "selected_study"
    const val PREFERENCES_USER_EMAIL_KEY = "user_email"
    val IS_RECORDING_PREFERENCE_KEY = booleanPreferencesKey("is_recording")
    val VIDEO_URI_PREFERENCE_KEY = stringPreferencesKey("video_uri")
}


val videosCollection = onSdkVersionAndUp(Build.VERSION_CODES.Q) {
    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
} ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI


inline fun <T> onSdkVersionAndUp(version: Int, block: () -> T) : T? {
    return if (Build.VERSION.SDK_INT >= version) {
        block()
    } else null
}

inline fun <T> onSdkVersionAndDown(version: Int, block: () -> T) : T? {
    return if (Build.VERSION.SDK_INT <= version) {
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

@Composable
fun <T> Flow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    onCollect: (T) -> Unit,
) {
    LaunchedEffect(this, lifecycleOwner.lifecycle, minActiveState, context) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            this@collectWithLifecycle.collect(onCollect)
        }
    }
}