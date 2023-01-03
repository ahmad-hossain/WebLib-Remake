package com.github.godspeed010.weblib.feature_webview.util

import android.webkit.WebView

val WebView.verticalScrollProgression
    get() = ( ( (scrollY - top).toFloat() / resources.displayMetrics.density) / contentHeight).coerceAtLeast(0f)

fun WebView.calculateScrollYFromProgression(verticalProgression: Float): Int {
    return ((verticalProgression * contentHeight) * resources.displayMetrics.density).toInt() + top
}
