package com.github.godspeed010.weblib.feature_webview.util

import android.util.Patterns
import timber.log.Timber

fun String.isUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

fun String.makeHttpsIfNeeded(): String {
    val urlStartsWithHttp = this.startsWith("http:")
    val urlStartsWithHttps = this.startsWith("https:")

    if (urlStartsWithHttps) return this

    val correctedUrl = if (urlStartsWithHttp) {
        Timber.d("Replacing 'http' with 'https'")
        this.replaceFirst(oldValue = "http", newValue = "https", ignoreCase = true)
    }
    else {
        Timber.d("Prepending 'https://' to res")
        "https://$this"
    }

    return correctedUrl
}

fun String.asGoogleSearch() = "https://www.google.com/search?q=$this"