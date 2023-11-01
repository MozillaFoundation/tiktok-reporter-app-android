package org.mozilla.tiktokreporter.data.remote

import org.mozilla.tiktokreporter.data.remote.response.PolicyDTO
import org.mozilla.tiktokreporter.data.remote.response.StudyDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface TikTokReporterService {

    @GET("policies/app")
    suspend fun getAppTermsAndConditions(): List<PolicyDTO>

    @GET("studies/by-country-code")
    suspend fun getStudies(): List<StudyDTO>

    @GET("studies/{id}")
    suspend fun getStudyById(@Path("id") studyId: String): StudyDTO
}