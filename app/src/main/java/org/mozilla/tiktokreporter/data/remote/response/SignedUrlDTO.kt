package org.mozilla.tiktokreporter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignedUrlDTO(
    @Json(name = "url") val url: String
)