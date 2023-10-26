package org.mozilla.tiktokreporter.data.remote

import org.mozilla.tiktokreporter.data.remote.response.Policy
import retrofit2.http.GET

interface TIkTokReporterService {

    @GET("policies")
    suspend fun getAppTermsAndConditions(): List<Policy>
}