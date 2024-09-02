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
            Novel.createWithDefaults(id = 1, title = "name", url = "url", folderId = 1),
            Novel.createWithDefaults(id = 2, title = "title",url =  "url", folderId = 1),
            Novel.createWithDefaults(id = 3, title = "name", url = "url_Here", folderId = 1),
        )
        val remainingNovel = Novel.createWithDefaults(id = 5, title = "name", url = "url_Here", folderId = 2)
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

        val novel = Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 1)
        novelDao.insert(novel)

        val allNovels = novelDao.getNovels().getOrAwaitValue()
        assertThat(allNovels).containsExactly(novel)
    }

    @Test
    fun deleteNovel() = runBlockingTest {
        val parentFolder = Folder(1, "title")
        folderDao.insert(parentFolder)

        val novel = Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 1)
        val remainingNovel = Novel.createWithDefaults(id = 2, title = "something", url = "urlabc", folderId = 1)
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

        val novel = Novel.createWithDefaults(id = 3, title = "title", url = "url", folderId = 1)
        val updatedNovel = Novel.createWithDefaults(id = 3, title = "new", url = "newurl", folderId = 1)
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

        val novel1 = Novel.createWithDefaults(id = 1, title = "hello", url = "url", folderId = 9)
        val novel2 = Novel.createWithDefaults(id = 2, title = "hello world", url = "url", folderId = 8)
        val novel3 = Novel.createWithDefaults(id = 3, title = "title", url = "hello", folderId = 7)
        val noMatch = Novel.createWithDefaults(id = 4, title = "title", url = "url", folderId = 6)
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
            Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 2, title = "titleabc", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 3, title = "title", url = "url", folderId = 5),
        )
        novels.forEach { novelDao.insert(it) }

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactlyElementsIn(novels)
    }
}
