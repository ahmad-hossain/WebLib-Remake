package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: T): Long

    @Update
    suspend fun update(entity: T)

    @Delete
    suspend fun delete(entity: T)

    @Transaction
    suspend fun insertOrUpdate(entity: T) {
        val id = insert(entity)
        if (id == -1L) update(entity)
    }

    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}