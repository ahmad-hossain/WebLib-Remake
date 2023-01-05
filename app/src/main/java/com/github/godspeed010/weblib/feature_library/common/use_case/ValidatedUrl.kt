package com.github.godspeed010.weblib.feature_library.common.use_case

import com.github.godspeed010.weblib.feature_webview.util.asGoogleSearch
import com.github.godspeed010.weblib.feature_webview.util.isUrl
import com.github.godspeed010.weblib.feature_webview.util.makeHttpsIfNeeded
import timber.log.Timber
import javax.inject.Inject

class ValidatedUrl @Inject constructor() {

    operator fun invoke(text: String): String {
        if (!text.isUrl()) {
            Timber.d("Text isn't url. Converting to Google search")
            return text.asGoogleSearch()
        } else {
            return text.makeHttpsIfNeeded()
        }
    }
}