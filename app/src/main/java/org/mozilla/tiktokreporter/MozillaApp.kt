package org.mozilla.tiktokreporter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val coilImageLoader by lazy {
    OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                addInterceptor(loggingInterceptor)
            }
        }
        .build()
}

@HiltAndroidApp
class MozillaApp: Application()