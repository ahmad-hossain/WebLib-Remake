package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.room.*
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao : BaseDao<Folder> {
    @Query("SELECT * FROM Folder")
    fun getFolders(): Flow<List<Folder>>

    @Query("SELECT title FROM Folder")
    suspend fun getFolderNames(): List<String>

    @Transaction
    @Query("SELECT * FROM folder WHERE id = :folderId")
    fun getFolderWithNovels(folderId: Int): Flow<FolderWithNovel>
}