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
        novelDao.insertNovel(novel)
    }

    override suspend fun updateNovel(novel: Novel) {
        novelDao.updateNovel(novel)
    }

    override suspend fun deleteNovel(novel: Novel) {
        novelDao.deleteNovel(novel)
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
        folderDao.insertFolder(folder)
    }

    override suspend fun updateFolder(folder: Folder) {
        folderDao.updateFolder(folder)
    }

    override suspend fun deleteFolder(folder: Folder) {
        folderDao.deleteFolder(folder)
    }

    override fun getFolderWithNovels(folderId: Int): LiveData<FolderWithNovel> {
        return folderDao.getFolderWithNovels(folderId)
    }

    override suspend fun insertOrUpdateFolder(folder: Folder) {
        folderDao.insertOrUpdateFolder(folder)
    }
}