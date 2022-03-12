package com.github.godspeed010.weblib.feature_library.data.repository

import androidx.lifecycle.LiveData
import com.github.godspeed010.weblib.feature_library.data.data_source.FolderDao
import com.github.godspeed010.weblib.feature_library.data.data_source.NovelDao
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository

class LibraryRepositoryImpl(
    private val folderDao: FolderDao,
    private val novelDao: NovelDao
) : LibraryRepository {
    override fun getNovels(): LiveData<List<Novel>> {
        return novelDao.getNovels()
    }

    override suspend fun insertNovel(novel: Novel) {
        return novelDao.insertNovel(novel)
    }

    override suspend fun updateNovel(novel: Novel) {
        return novelDao.updateNovel(novel)
    }

    override suspend fun deleteNovel(novel: Novel) {
        return novelDao.deleteNovel(novel)
    }

    override suspend fun findNovelsByNameOrUrl(query: String): List<Novel> {
        return novelDao.findNovelsByNameOrUrl(query)
    }

    override fun getFolders(): LiveData<List<Folder>> {
        return folderDao.getFolders()
    }

    override suspend fun getFolderNames(): List<String> {
        return folderDao.getFolderNames()
    }

    override suspend fun insertFolder(folder: Folder) {
        return folderDao.insertFolder(folder)
    }

    override suspend fun updateFolder(folder: Folder) {
        return folderDao.updateFolder(folder)
    }

    override suspend fun deleteFolder(folder: Folder) {
        return folderDao.deleteFolder(folder)
    }

    override suspend fun getFolderWithNovels(folderId: Int): FolderWithNovel {
        return folderDao.getFolderWithNovels(folderId)
    }
}