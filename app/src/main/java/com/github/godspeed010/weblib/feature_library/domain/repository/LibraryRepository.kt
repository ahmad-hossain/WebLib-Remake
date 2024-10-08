package com.github.godspeed010.weblib.feature_library.domain.repository

import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getNovels() : Flow<List<Novel>>

    suspend fun insertOrUpdateNovel(novel: Novel)

    suspend fun insertNovel(novel: Novel)

    suspend fun updateNovel(novel: Novel)

    suspend fun deleteNovel(novel: Novel)

    suspend fun findNovelsByNameOrUrl(query: String): List<Novel>

    fun getFolders() : Flow<List<Folder>>

    suspend fun getFolderNames(): List<String>

    suspend fun insertFolder(folder: Folder)

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteFolder(folder: Folder)

    fun getFolderWithNovels(folderId: Int): Flow<FolderWithNovel>

    suspend fun insertOrUpdateFolder(folder: Folder)

    suspend fun moveNovel(novel: Novel, to: Folder)
}