package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
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
class NovelDaoTest {

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
    fun folderDeletionDeletesChildNovels() = runBlockingTest {
        val parentFolder = Folder(0, "title")
        val remainingFolder = Folder(1, "Title")
        folderDao.insertFolder(parentFolder)
        folderDao.insertFolder(remainingFolder)

        val novels = listOf(
            Novel(0, "name", "url", 0),
            Novel(1, "title", "url", 0),
            Novel(2, "name", "url_Here", 0),
        )
        val remainingNovel = Novel(5, "name", "url_Here", 1)
        novels.forEach { novelDao.insertNovel(it) }
        novelDao.insertNovel(remainingNovel)

        folderDao.deleteFolder(parentFolder)

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactly(remainingNovel)
    }

    @Test
    fun addNovel() = runBlockingTest {
        //folder with same folderId must be created first due to parent-child relationship
        val parentFolder = Folder(0, "title")
        folderDao.insertFolder(parentFolder)

        val novel = Novel(0, "title", "url", 0)
        novelDao.insertNovel(novel)

        val allNovels = novelDao.getNovels().getOrAwaitValue()
        assertThat(allNovels).containsExactly(novel)
    }

    @Test
    fun deleteNovel() = runBlockingTest {
        val parentFolder = Folder(0, "title")
        folderDao.insertFolder(parentFolder)

        val novel = Novel(1, "title", "url", 0)
        val remainingNovel = Novel(2, "something", "urlabc", 0)
        novelDao.insertNovel(novel)
        novelDao.insertNovel(remainingNovel)
        novelDao.deleteNovel(novel)

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactly(remainingNovel)
    }

    @Test
    fun updateNovel() = runBlockingTest {
        val parentFolder = Folder(0, "title")
        folderDao.insertFolder(parentFolder)

        val novel = Novel(0, "title", "url", 0)
        val updatedNovel = Novel(0, "new", "newurl", 0)
        novelDao.insertNovel(novel)
        novelDao.updateNovel(updatedNovel)

        val allNovels = novelDao.getNovels().getOrAwaitValue()
        assertThat(allNovels).containsExactly(updatedNovel)
    }

    @Test
    fun findNovelByNameOrUrl() = runBlockingTest {
        val folders = listOf(
            Folder(9, "title"),
            Folder(7, "folder_title"),
            Folder(8, "abcTitle"),
            Folder(6, "titleHere"),
        )
        folders.forEach { folderDao.insertFolder(it) }

        val novel1 = Novel(0, "hello", "url", 9)
        val novel2 = Novel(1, "hello world", "url", 8)
        val novel3 = Novel(2, "title", "hello", 7)
        val noMatch = Novel(3, "title", "url", 6)
        novelDao.insertNovel(novel1)
        novelDao.insertNovel(novel2)
        novelDao.insertNovel(novel3)
        novelDao.insertNovel(noMatch)

        val queriedNovels = novelDao.findNovelsByNameOrUrl("%hell%")

        assertThat(queriedNovels).containsExactly(novel1, novel2, novel3)
    }

}