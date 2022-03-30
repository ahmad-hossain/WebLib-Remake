package com.github.godspeed010.weblib.feature_library.domain.model.relations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.godspeed010.weblib.feature_library.data.data_source.FolderDao
import com.github.godspeed010.weblib.feature_library.data.data_source.LibraryDatabase
import com.github.godspeed010.weblib.feature_library.data.data_source.NovelDao
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FolderWithNovelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun getNovelsFromFolder() = runBlockingTest {
        val folder = Folder(1, "testFolder")
        folderDao.insertFolder(folder)

        val folderNovels = listOf(
            Novel(id = 1, title = "one", "url", 1),
            Novel(id = 2, title = "two", "url2", 1),
            Novel(id = 3, title = "three", "url3", 1),
            Novel(id = 4, title = "four", "url4", 1),
            Novel(id = 5, title = "five", "url5", 1),
        )
        folderNovels.forEach { novelDao.insertNovel(it) }

        val novelsFromFolder = folder.id.let { folderDao.getFolderWithNovels(it) }.getOrAwaitValue().novels

        assertThat(novelsFromFolder).containsExactlyElementsIn(folderNovels)
    }

}