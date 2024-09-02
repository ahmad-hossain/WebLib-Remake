package com.github.godspeed010.weblib.feature_library.domain.model.relations

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.godspeed010.weblib.feature_library.data.data_source.FolderDao
import com.github.godspeed010.weblib.feature_library.data.data_source.LibraryDatabase
import com.github.godspeed010.weblib.feature_library.data.data_source.NovelDao
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FolderWithNovelTest {

    private lateinit var folderDao: FolderDao
    private lateinit var novelDao: NovelDao
    private lateinit var database: LibraryDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LibraryDatabase::class.java
        ).allowMainThreadQueries().build()

        folderDao = database.folderDao
        novelDao = database.novelDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getNovelsFromFolder() = runTest {
        val folder = Folder(1, "testFolder")
        folderDao.insert(folder)

        val folderNovels = listOf(
            Novel.createWithDefaults(id = 1, title = "one", url = "url", folderId = 1),
            Novel.createWithDefaults(id = 2, title = "two", url = "url2", folderId = 1),
            Novel.createWithDefaults(id = 3, title = "three", url = "url3", folderId = 1),
            Novel.createWithDefaults(id = 4, title = "four", url = "url4", folderId = 1),
            Novel.createWithDefaults(id = 5, title = "five", url = "url5", folderId = 1),
        )
        folderNovels.forEach { novelDao.insert(it) }

        val novelsFromFolder = folderDao.getFolderWithNovels(folder.id).first().novels

        assertThat(novelsFromFolder).containsExactlyElementsIn(folderNovels)
    }

}