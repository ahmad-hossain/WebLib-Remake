package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
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
class NovelDaoTest {

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
    fun folderDeletionDeletesChildNovels() = runTest {
        val parentFolder = Folder(1, "title")
        val remainingFolder = Folder(2, "Title")
        val novels = listOf(
            Novel.createWithDefaults(id = 1, title = "name", url = "url", folderId = 1),
            Novel.createWithDefaults(id = 2, title = "title", url = "url", folderId = 1),
            Novel.createWithDefaults(id = 3, title = "name", url = "url_Here", folderId = 1),
        )
        val remainingNovel =
            Novel.createWithDefaults(id = 5, title = "name", url = "url_Here", folderId = 2)

        folderDao.insert(parentFolder)
        folderDao.insert(remainingFolder)
        (novels + remainingNovel).forEach { novelDao.insert(it) }
        folderDao.delete(parentFolder)

        assertThat(getAllNovels()).containsExactly(remainingNovel)
    }

    @Test
    fun addNovel() = runTest {
        // folder with same folderId must be created first due to parent-child relationship
        val parentFolder = Folder(1, "title")
        val novel = Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 1)

        folderDao.insert(parentFolder)
        novelDao.insert(novel)

        assertThat(getAllNovels()).containsExactly(novel)
    }

    @Test
    fun deleteNovel() = runTest {
        val parentFolder = Folder(1, "title")
        val novel = Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 1)
        val remainingNovel =
            Novel.createWithDefaults(id = 2, title = "something", url = "urlabc", folderId = 1)

        folderDao.insert(parentFolder)
        novelDao.insert(novel)
        novelDao.insert(remainingNovel)
        novelDao.delete(novel)

        assertThat(getAllNovels()).containsExactly(remainingNovel)
    }

    @Test
    fun updateNovel() = runTest {
        val parentFolder = Folder(1, "title")
        val novel = Novel.createWithDefaults(id = 3, title = "title", url = "url", folderId = 1)
        val updatedNovel =
            Novel.createWithDefaults(id = 3, title = "new", url = "newurl", folderId = 1)

        folderDao.insert(parentFolder)
        novelDao.insert(novel)
        novelDao.update(updatedNovel)

        assertThat(getAllNovels()).containsExactly(updatedNovel)
    }

    @Test
    fun findNovelByNameOrUrl() = runTest {
        val folders = listOf(
            Folder(9, "title"),
            Folder(7, "folder_title"),
            Folder(8, "abcTitle"),
            Folder(6, "titleHere"),
        )
        val novels = listOf(
            Novel.createWithDefaults(id = 1, title = "hello", url = "url", folderId = 9),
            Novel.createWithDefaults(id = 2, title = "hello world", url = "url", folderId = 8),
            Novel.createWithDefaults(id = 3, title = "title", url = "hello", folderId = 7)
        )
        val noMatchNovel =
            Novel.createWithDefaults(id = 4, title = "title", url = "url", folderId = 6)

        folders.forEach { folderDao.insert(it) }
        (novels + noMatchNovel).forEach { novelDao.insert(it) }

        val queriedNovels = novelDao.findNovelsByNameOrUrl("%hell%")
        assertThat(queriedNovels).containsExactlyElementsIn(novels)
    }

    @Test
    fun getNovels() = runTest {
        val parentFolder = Folder(id = 5, title = "title here")
        val novels = listOf(
            Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 2, title = "titleabc", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 3, title = "title", url = "url", folderId = 5),
        )

        folderDao.insert(parentFolder)
        novels.forEach { novelDao.insert(it) }

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    private suspend fun getAllNovels(): List<Novel> = novelDao.getNovels().first()
}
