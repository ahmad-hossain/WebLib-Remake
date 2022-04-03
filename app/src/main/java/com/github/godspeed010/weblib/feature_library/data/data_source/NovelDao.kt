package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

@Dao
interface NovelDao : BaseDao<Novel> {
    @Query("SELECT * FROM Novel")
    fun getNovels(): LiveData<List<Novel>>

    @Query("SELECT * FROM Novel WHERE title LIKE :query OR url LIKE :query")
    suspend fun findNovelsByNameOrUrl(query: String): List<Novel>
}