package org.mozilla.tiktokreporter

import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toTikTokReporterError(): TikTokReporterError {

    return when (this) {
        is HttpException -> TikTokReporterError.ServerError(this.message())

        is SocketTimeoutException, is UnknownHostException -> TikTokReporterError.NetworkError(this.message.orEmpty())

        // unknown error
        else -> TikTokReporterError.UnknownError(this.message.orEmpty())
    }
}

sealed class TikTokReporterError {
    data class ServerError(
        val message: String
    ): TikTokReporterError()
    data class NetworkError(
        val message: String
    ): TikTokReporterError()
    data class UnknownError(
        val message: String
    ): TikTokReporterError()
}