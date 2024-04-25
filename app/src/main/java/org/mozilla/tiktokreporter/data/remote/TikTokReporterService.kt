package org.mozilla.tiktokreporter.data.remote

import org.mozilla.tiktokreporter.BuildConfig
import org.mozilla.tiktokreporter.data.remote.response.PolicyDTO
import org.mozilla.tiktokreporter.data.remote.response.SignedUrlDTO
import org.mozilla.tiktokreporter.data.remote.response.StudyDTO
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TikTokReporterService {

    @GET("policies/app")
    suspend fun getAppTermsAndConditions(): List<PolicyDTO>

    @GET("studies/by-country-code")
    suspend fun getStudies(): List<StudyDTO>

    @GET("studies/{id}")
    suspend fun getStudyById(@Path("id") studyId: String): StudyDTO

    @GET("signedUrl")
    suspend fun getSignedUrl(
        @Header("X-API-KEY") token: String = BuildConfig.UPLOAD_RECORDING_API_KEY,
    ): SignedUrlDTO
}