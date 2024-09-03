package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.godspeed010.weblib.feature_library.common.TestDataUtil
import com.github.godspeed010.weblib.feature_library.common.TestDataUtil.FOLDER_1
import com.github.godspeed010.weblib.feature_library.common.TestDataUtil.FOLDER_2
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
        val parentFolder = FOLDER_1
        val remainingFolder = FOLDER_2
        val novels = TestDataUtil.createNovels(count = 3, folderId = parentFolder.id)
        val remainingNovel = TestDataUtil.createNovelWithId(id = 4, folderId = remainingFolder.id)

        folderDao.insert(parentFolder)
        folderDao.insert(remainingFolder)
        (novels + remainingNovel).forEach { novelDao.insert(it) }
        folderDao.delete(parentFolder)

        assertThat(getAllNovels()).containsExactly(remainingNovel)
    }

    @Test
    fun addNovel() = runTest {
        // folder with same folderId must be created first due to parent-child relationship
        val parentFolder = FOLDER_1
        val novel = TestDataUtil.createNovelWithId(id = 1, folderId = parentFolder.id)

        folderDao.insert(parentFolder)
        novelDao.insert(novel)

        assertThat(getAllNovels()).containsExactly(novel)
    }

    @Test
    fun deleteNovel() = runTest {
        val parentFolder = FOLDER_1
        val novel = TestDataUtil.createNovelWithId(id = 1, folderId = parentFolder.id)
        val remainingNovel = TestDataUtil.createNovelWithId(id = 2, folderId = parentFolder.id)

        folderDao.insert(parentFolder)
        novelDao.insert(novel)
        novelDao.insert(remainingNovel)
        novelDao.delete(novel)

        assertThat(getAllNovels()).containsExactly(remainingNovel)
    }

    @Test
    fun updateNovel() = runTest {
        val parentFolder = FOLDER_1
        val novel = TestDataUtil.createNovelWithId(id = 3, folderId = parentFolder.id)
        val updatedNovel = novel.copy(title = "new", url = "newurl")

        folderDao.insert(parentFolder)
        novelDao.insert(novel)
        novelDao.update(updatedNovel)

        assertThat(getAllNovels()).containsExactly(updatedNovel)
    }

    @Test
    fun findNovelByNameOrUrl() = runTest {
        val folders = TestDataUtil.createFolders(count = 4)
        val novels = listOf(
            Novel.createWithDefaults(
                id = 1,
                title = "hello",
                url = "url",
                folderId = folders[0].id
            ),
            Novel.createWithDefaults(
                id = 2,
                title = "hello world",
                url = "url",
                folderId = folders[1].id
            ),
            Novel.createWithDefaults(
                id = 3,
                title = "title",
                url = "hello",
                folderId = folders[2].id
            )
        )
        val noMatchNovel =
            Novel.createWithDefaults(id = 4, title = "title", url = "url", folderId = folders[3].id)

        folders.forEach { folderDao.insert(it) }
        (novels + noMatchNovel).forEach { novelDao.insert(it) }

        val queriedNovels = novelDao.findNovelsByNameOrUrl("%hell%")
        assertThat(queriedNovels).containsExactlyElementsIn(novels)
    }

    @Test
    fun getNovels() = runTest {
        val parentFolder = FOLDER_1
        val novels = TestDataUtil.createNovels(count = 3, folderId = parentFolder.id)

        folderDao.insert(parentFolder)
        novels.forEach { novelDao.insert(it) }

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    private suspend fun getAllNovels(): List<Novel> = novelDao.getNovels().first()
}
