package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel

@Dao
interface FolderDao {
    @Query("SELECT * FROM Folder")
    fun getFolders() : LiveData<List<Folder>>

    @Query("SELECT title FROM Folder")
    suspend fun getFolderNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(folder: Folder): Long

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Transaction
    @Query("SELECT * FROM folder WHERE id = :folderId")
    fun getFolderWithNovels(folderId: Int): LiveData<FolderWithNovel>

    @Transaction
    suspend fun insertOrUpdateFolder(folder: Folder) {
        val id = insertFolder(folder)
        if (id == -1L) updateFolder(folder)
    }
}