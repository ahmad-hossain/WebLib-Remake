package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

@Database(
    entities = [Folder::class, Novel::class],
    version = 1,
    exportSchema = false
)
abstract class LibraryDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun novelDao(): NovelDao

    companion object {
        const val DATABASE_NAME = "library_db"
    }
}