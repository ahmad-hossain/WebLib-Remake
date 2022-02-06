package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

@Dao
interface FolderDao {
    @Query("SELECT * FROM Folder")
    fun getFolders() : LiveData<List<Folder>>

    @Query("SELECT title FROM Folder")
    suspend fun getFolderNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(folder: Folder)

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)
}