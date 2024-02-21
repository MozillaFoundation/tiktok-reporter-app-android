package org.mozilla.tiktokreporter.util

import android.text.format.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun Long.millisToMinSecString(): String {
    val seconds = this.milliseconds.inWholeSeconds
    val minutes = seconds.seconds.inWholeMinutes
    val secondsRemaining = if (minutes > 0) { seconds - (minutes*60)} else seconds
    val secondsString = secondsRemaining.let {
        if (it < 10) {
            "0$it"
        } else "$it"
    }
    val minutesString = minutes.let {
        if (it < 10) {
            "0$it"
        } else "$it"
    }

    return "$minutesString:$secondsString"
}

fun LocalDate.asString(): String {
    val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM-dd-yyyy")
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return formatter.format(this)
}
fun LocalTime.asString(): String {
    return "${this.hour}:${this.minute}"
}
fun LocalDateTime.toDateString(): String {
    return this.toLocalDate().asString()
}
fun LocalDateTime.toTimeString(): String {
    return this.toLocalTime().asString()
}