package com.github.godspeed010.weblib.feature_library.domain.repository

import androidx.lifecycle.LiveData
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel

interface LibraryRepository {
    fun getNovels() : LiveData<List<Novel>>

    suspend fun insertOrUpdateNovel(novel: Novel)

    suspend fun insertNovel(novel: Novel)

    suspend fun updateNovel(novel: Novel)

    suspend fun deleteNovel(novel: Novel)

    suspend fun findNovelsByNameOrUrl(query: String): List<Novel>

    fun getFolders() : LiveData<List<Folder>>

    suspend fun getFolderNames(): List<String>

    suspend fun insertFolder(folder: Folder)

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteFolder(folder: Folder)

    fun getFolderWithNovels(folderId: Int): LiveData<FolderWithNovel>

    suspend fun insertOrUpdateFolder(folder: Folder)
}