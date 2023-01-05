package com.github.godspeed010.weblib.feature_webview.domain.use_case

import javax.inject.Inject

data class WebViewUseCases @Inject constructor(
    val updateNovel: UpdateNovel,
)