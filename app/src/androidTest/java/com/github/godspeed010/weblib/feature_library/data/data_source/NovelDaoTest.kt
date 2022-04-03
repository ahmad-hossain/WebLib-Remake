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
        val parentFolder = Folder(1, "title")
        val remainingFolder = Folder(2, "Title")
        folderDao.insert(parentFolder)
        folderDao.insert(remainingFolder)

        val novels = listOf(
            Novel(1, "name", "url", 1),
            Novel(2, "title", "url", 1),
            Novel(3, "name", "url_Here", 1),
        )
        val remainingNovel = Novel(5, "name", "url_Here", 2)
        novels.forEach { novelDao.insert(it) }
        novelDao.insert(remainingNovel)

        folderDao.delete(parentFolder)

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactly(remainingNovel)
    }

    @Test
    fun addNovel() = runBlockingTest {
        //folder with same folderId must be created first due to parent-child relationship
        val parentFolder = Folder(1, "title")
        folderDao.insert(parentFolder)

        val novel = Novel(1, "title", "url", 1)
        novelDao.insert(novel)

        val allNovels = novelDao.getNovels().getOrAwaitValue()
        assertThat(allNovels).containsExactly(novel)
    }

    @Test
    fun deleteNovel() = runBlockingTest {
        val parentFolder = Folder(1, "title")
        folderDao.insert(parentFolder)

        val novel = Novel(1, "title", "url", 1)
        val remainingNovel = Novel(2, "something", "urlabc", 1)
        novelDao.insert(novel)
        novelDao.insert(remainingNovel)
        novelDao.delete(novel)

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactly(remainingNovel)
    }

    @Test
    fun updateNovel() = runBlockingTest {
        val parentFolder = Folder(1, "title")
        folderDao.insert(parentFolder)

        val novel = Novel(3, "title", "url", 1)
        val updatedNovel = Novel(3, "new", "newurl", 1)
        novelDao.insert(novel)
        novelDao.update(updatedNovel)

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
        folders.forEach { folderDao.insert(it) }

        val novel1 = Novel(1, "hello", "url", 9)
        val novel2 = Novel(2, "hello world", "url", 8)
        val novel3 = Novel(3, "title", "hello", 7)
        val noMatch = Novel(4, "title", "url", 6)
        novelDao.insert(novel1)
        novelDao.insert(novel2)
        novelDao.insert(novel3)
        novelDao.insert(noMatch)

        val queriedNovels = novelDao.findNovelsByNameOrUrl("%hell%")

        assertThat(queriedNovels).containsExactly(novel1, novel2, novel3)
    }

    @Test
    fun getNovels() = runBlockingTest {
        val parentFolder = Folder(id = 5, title = "title here")
        folderDao.insert(parentFolder)

        val novels = listOf(
            Novel(1, "title", "url", 5),
            Novel(2, "titleabc", "url", 5),
            Novel(3, "title", "url", 5),
        )
        novels.forEach { novelDao.insert(it) }

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactlyElementsIn(novels)
    }
}