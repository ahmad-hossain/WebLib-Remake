package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

@Dao
interface NovelDao {
    @Query("SELECT * FROM Novel")
    fun getNovels() : LiveData<List<Novel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNovel(novel: Novel)

    @Update
    suspend fun updateNovel(novel: Novel)

    @Delete
    suspend fun deleteNovel(novel: Novel)

    @Query("SELECT * FROM Novel WHERE title LIKE :query OR url LIKE :query")
    suspend fun findNovelsByNameOrUrl(query: String): List<Novel>
}