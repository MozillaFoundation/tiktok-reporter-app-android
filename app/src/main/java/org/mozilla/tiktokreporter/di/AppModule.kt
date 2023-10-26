package org.mozilla.tiktokreporter.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mozilla.tiktokreporter.BuildConfig
import org.mozilla.tiktokreporter.data.remote.TIkTokReporterService
import org.mozilla.tiktokreporter.data.remote.response.FormField
import org.mozilla.tiktokreporter.data.remote.response.FormFieldType
import org.mozilla.tiktokreporter.util.LocalDateTimeAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideTikTokReporterService(): TIkTokReporterService {

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                    addInterceptor(loggingInterceptor)
                }
            }
            .build()

        val moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(FormField::class.java, "type")
                    .withSubtype(FormField.TextField::class.java, FormFieldType.TextField.name)
                    .withSubtype(FormField.DropDown::class.java, FormFieldType.DropDown.name)
                    .withSubtype(FormField.Slider::class.java, FormFieldType.Slider.name)
                    .withSubtype(FormField.CheckboxGroup::class.java, FormFieldType.CheckboxGroup.name)
                    .withSubtype(FormField.RadioGroup::class.java, FormFieldType.RadioGroup.name)
                    .withSubtype(FormField.MultiSelect::class.java, FormFieldType.MultiSelect.name)
                    .withDefaultValue(FormField.Unknown)
            )
            .add(KotlinJsonAdapterFactory())
            .add(LocalDateTimeAdapter())
            .build()

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()
            .create(TIkTokReporterService::class.java)
    }
}