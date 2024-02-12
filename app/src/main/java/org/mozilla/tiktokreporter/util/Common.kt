package org.mozilla.tiktokreporter.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.util.lerp
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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_flags")

object Common {
    const val PREFERENCES_TERMS_ACCEPTED_KEY = "terms_accepted"
    const val PREFERENCES_ONBOARDING_COMPLETED_KEY = "onboarding_completed"
    const val PREFERENCES_SELECTED_STUDY_KEY = "selected_study"
    const val PREFERENCES_USER_EMAIL_KEY = "user_email"
    val DATASTORE_KEY_IS_RECORDING = booleanPreferencesKey("is_recording")
    val DATASTORE_KEY_VIDEO_URI = stringPreferencesKey("video_uri")
    val DATASTORE_KEY_RECORDING_UPLOADED = booleanPreferencesKey("recording_uploaded")
    val DATASTORE_KEY_REDIRECT_FIRST_TAB = booleanPreferencesKey("first_tab")
}


val videosCollection: Uri = onSdkVersionAndUp(Build.VERSION_CODES.Q) {
    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
} ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI


inline fun <T> onSdkVersionAndUp(version: Int, block: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= version) {
        block()
    } else null
}

inline fun <T> onSdkVersionAndDown(version: Int, block: () -> T): T? {
    return if (Build.VERSION.SDK_INT <= version) {
        block()
    } else null
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

@Suppress("unused")
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

@Composable
fun <T> CollectWithLifecycle(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    onCollect: (T) -> Unit,
) {
    LaunchedEffect(flow, lifecycleOwner.lifecycle, minActiveState, context) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            flow.collect(onCollect)
        }
    }
}

// Scale num from min..max range to targetMin..targetMax range
fun scale(
    min: Float,
    max: Float,
    num: Float,
    targetMin: Float,
    targetMax: Float
) = lerp(targetMin, targetMax, calcFraction(min, max, num))

// Scale range.start, range.end from min..max range to targetMin..targetMax range
@Suppress("unused")
fun scale(
    min: Float,
    max: Float,
    range: ClosedFloatingPointRange<Float>,
    targetMin: Float,
    targetMax: Float
): ClosedRange<Float> {
    val scaledMin = scale(min, max, range.start, targetMin, targetMax)
    val scaledMax = scale(min, max, range.endInclusive, targetMin, targetMax)

    return scaledMin..scaledMax
}

// Calculate the 0..1 fraction that `pos` value represents between `min` and `max`
fun calcFraction(min: Float, max: Float, pos: Float) =
    (if (max - min == 0f) 0f else (pos - min) / (max - min)).coerceIn(0f, 1f)