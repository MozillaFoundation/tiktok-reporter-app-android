package org.mozilla.tiktokreporter.data.remote

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Url

interface GCSService {

    @PUT
    suspend fun uploadRecording(
        @Url url: String, @Header("content-type") contentType: String = "video/mp4", @Body file: RequestBody
    )
}