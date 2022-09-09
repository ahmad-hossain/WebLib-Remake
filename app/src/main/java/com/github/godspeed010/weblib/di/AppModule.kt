package com.github.godspeed010.weblib.di

import android.app.Application
import androidx.room.Room
import com.github.godspeed010.weblib.feature_library.data.data_source.LibraryDatabase
import com.github.godspeed010.weblib.feature_library.data.repository.LibraryRepositoryImpl
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.AddOrUpdateFolder
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.DeleteFolder
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.FolderUseCases
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.GetFolders
import com.github.godspeed010.weblib.feature_library.domain.use_case.novel.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLibraryRepository(db: LibraryDatabase): LibraryRepository {
        return LibraryRepositoryImpl(db.folderDao, db.novelDao)
    }

    @Provides
    @Singleton
    fun provideLibraryDatabase(app: Application): LibraryDatabase {
        return Room.databaseBuilder(
            app,
            LibraryDatabase::class.java,
            LibraryDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFolderUseCases(repository: LibraryRepository) : FolderUseCases {
        return FolderUseCases(
            AddOrUpdateFolder(repository),
            GetFolders(repository),
            DeleteFolder(repository)
        )
    }

    @Provides
    @Singleton
    fun provideNovelUseCases(repository: LibraryRepository) : NovelUseCases {
        return NovelUseCases(
            GetNovels(repository),
            AddOrUpdateNovel(repository),
            GetFolderWithNovels(repository),
            DeleteNovel(repository)
        )
    }
}