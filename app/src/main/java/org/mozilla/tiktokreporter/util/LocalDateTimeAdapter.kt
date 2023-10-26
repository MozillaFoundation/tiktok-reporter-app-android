package org.mozilla.tiktokreporter.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter {
    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"))
    }

    @FromJson
    fun fromJson(value: String): LocalDateTime {
        return LocalDateTime.from(FORMATTER.parse(value))
    }

    @ToJson
    fun toJson(dateTime: LocalDateTime): String {
        return FORMATTER.format(dateTime.atZone(ZoneOffset.UTC))
    }
}