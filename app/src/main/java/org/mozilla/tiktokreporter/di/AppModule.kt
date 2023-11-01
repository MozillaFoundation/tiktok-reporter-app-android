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
import org.mozilla.tiktokreporter.data.remote.TikTokReporterService
import org.mozilla.tiktokreporter.data.remote.response.FormFieldDTO
import org.mozilla.tiktokreporter.data.remote.response.FormFieldTypeDTO
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
    fun provideTikTokReporterService(): TikTokReporterService {

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
                PolymorphicJsonAdapterFactory.of(FormFieldDTO::class.java, "type")
                    .withSubtype(FormFieldDTO.TextField::class.java, FormFieldTypeDTO.TextField.name)
                    .withSubtype(FormFieldDTO.DropDown::class.java, FormFieldTypeDTO.DropDown.name)
                    .withSubtype(FormFieldDTO.Slider::class.java, FormFieldTypeDTO.Slider.name)
                    .withSubtype(FormFieldDTO.CheckboxGroup::class.java, FormFieldTypeDTO.CheckboxGroup.name)
                    .withSubtype(FormFieldDTO.RadioGroup::class.java, FormFieldTypeDTO.RadioGroup.name)
                    .withSubtype(FormFieldDTO.MultiSelect::class.java, FormFieldTypeDTO.MultiSelect.name)
                    .withDefaultValue(FormFieldDTO.Unknown)
            )
            .add(KotlinJsonAdapterFactory())
            .add(LocalDateTimeAdapter())
            .build()

        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.BASE_URL)
            .build()
            .create(TikTokReporterService::class.java)
    }
}