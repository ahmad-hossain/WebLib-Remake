package com.github.godspeed010.weblib.feature_webview.di

import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_webview.domain.use_case.UpdateNovel
import com.github.godspeed010.weblib.feature_webview.domain.use_case.WebViewUseCases
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object WebViewModule {

    @Provides
    @Reusable
    fun provideWebViewUseCases(repository: LibraryRepository): WebViewUseCases =
        WebViewUseCases(
            updateNovel = UpdateNovel(repository),
        )
}