package org.mozilla.tiktokreporter.data.remote

import org.mozilla.tiktokreporter.data.remote.response.Policy
import org.mozilla.tiktokreporter.data.remote.response.Study
import retrofit2.http.GET
import retrofit2.http.Path

interface TIkTokReporterService {

    @GET("policies/app")
    suspend fun getAppTermsAndConditions(): List<Policy>
    @GET("policies/{id}")
    suspend fun getStudyTermsAndConditions(
        @Path("id") studyId: String
    ): List<Policy>

    @GET("studies/country-codes/{countryCode}")
    suspend fun getStudiesBasedOnCountry(
        @Path("countryCode") countryCode: String
    ): List<Study>
}