package com.github.godspeed010.weblib.feature_library.di

import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.AddOrUpdateFolder
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.DeleteFolder
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.FolderUseCases
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.GetFolders
import com.github.godspeed010.weblib.feature_library.domain.use_case.novel.*
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object LibraryModule {

    @Provides
    @Reusable
    fun provideFolderUseCases(repository: LibraryRepository): FolderUseCases {
        return FolderUseCases(
            AddOrUpdateFolder(repository),
            GetFolders(repository),
            DeleteFolder(repository)
        )
    }

    @Provides
    @Reusable
    fun provideNovelUseCases(repository: LibraryRepository): NovelUseCases {
        return NovelUseCases(
            GetNovels(repository),
            AddOrUpdateNovel(repository),
            GetFolderWithNovels(repository),
            DeleteNovel(repository)
        )
    }
}