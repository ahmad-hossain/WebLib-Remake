package com.github.godspeed010.weblib.feature_webview.util

import android.util.Patterns
import timber.log.Timber

fun String.isUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

fun String.makeHttpsIfNeeded(): String {
    var result = this

    val urlStartsWithHttp = result.startsWith("http:")
    val urlStartsWithHttps = result.startsWith("https:")

    if (urlStartsWithHttp) {
        Timber.d("Replacing 'http' with 'https'")
        result = result.replaceFirst(oldValue = "http", newValue = "https", ignoreCase = true)
    }
    else if (!urlStartsWithHttp and !urlStartsWithHttps) {
        Timber.d("Prepending 'https://' to res")
        result = "https://$result"
    }

    return result
}

fun String.asGoogleSearch() = "https://www.google.com/search?q=$this"