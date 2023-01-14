package com.github.godspeed010.weblib.feature_settings.domain.model

sealed class Response<out T> {
    object NotStarted : Response<Nothing>()
    object Loading : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class Failure(val e: Exception) : Response<Nothing>()
}