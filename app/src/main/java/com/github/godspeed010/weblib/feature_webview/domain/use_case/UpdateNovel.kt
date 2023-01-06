package com.github.godspeed010.weblib.feature_webview.domain.use_case

import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import com.github.godspeed010.weblib.feature_webview.presentation.webview.WebViewScreenState
import com.github.godspeed010.weblib.feature_webview.util.verticalScrollProgression
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateNovel @Inject constructor(
    private val repository: LibraryRepository,
) {

    suspend operator fun invoke(
        state: WebViewScreenState,
        novel: Novel
    ) = withContext(Dispatchers.Main) {
        val currentUrl = state.webViewState.content.getCurrentUrl() ?: return@withContext
        val wvScrollProgression =
            state.webView?.verticalScrollProgression ?: novel.scrollProgression
        val currScrollProgression =
            if (!state.webViewState.currentPageHasError) wvScrollProgression else novel.scrollProgression

        withContext(Dispatchers.IO) {
            repository.updateNovel(
                novel.copy(
                    url = currentUrl,
                    scrollProgression = currScrollProgression,
                    createdAt = novel.createdAt,
                    lastModified = TimeUtil.currentTimeSeconds()
                )
            )
        }
    }
}