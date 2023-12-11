package org.mozilla.tiktokreporter.data.remote

import okhttp3.MultipartBody
import org.mozilla.tiktokreporter.data.remote.response.PolicyDTO
import org.mozilla.tiktokreporter.data.remote.response.StudyDTO
import org.mozilla.tiktokreporter.data.remote.response.UploadedRecordingDTO
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TikTokReporterService {

    @GET("policies/app")
    suspend fun getAppTermsAndConditions(): List<PolicyDTO>

    @GET("studies/by-country-code")
    suspend fun getStudies(): List<StudyDTO>

    @GET("studies/{id}")
    suspend fun getStudyById(@Path("id") studyId: String): StudyDTO

    @POST("storage")
    @Multipart
    suspend fun uploadRecording(
        @Header("X-API-KEY") token: String = "f0bfa33e-333b-4704-b57e-bbe6e766ba65",
        @Part file: MultipartBody.Part
    ): UploadedRecordingDTO
}