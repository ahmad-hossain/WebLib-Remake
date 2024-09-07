package com.github.godspeed010.weblib.feature_library.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.godspeed010.weblib.feature_library.common.TestDataUtil
import com.github.godspeed010.weblib.feature_library.data.data_source.LibraryDatabase
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
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
class LibraryRepositoryImplTest {

    private lateinit var database: LibraryDatabase
    private lateinit var repository: LibraryRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LibraryDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = LibraryRepositoryImpl(database.folderDao, database.novelDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun folderDeletionDeletesChildNovels() = runTest {
        val parentFolder = TestDataUtil.FOLDER_1
        val remainingFolder = TestDataUtil.FOLDER_2
        val novels = TestDataUtil.createNovels(count = 3, folderId = parentFolder.id)
        val remainingNovel = TestDataUtil.createNovelWithId(id = 4, folderId = remainingFolder.id)

        repository.insertFolder(parentFolder)
        repository.insertFolder(remainingFolder)
        (novels + remainingNovel).forEach { repository.insertNovel(it) }
        repository.deleteFolder(parentFolder)

        assertThat(getAllNovels()).containsExactly(remainingNovel)
    }

    @Test
    fun addNovel() = runTest {
        // folder with same folderId must be created first due to parent-child relationship
        val parentFolder = TestDataUtil.FOLDER_1
        val novel = TestDataUtil.createNovelWithId(id = 1, folderId = parentFolder.id)

        repository.insertFolder(parentFolder)
        repository.insertNovel(novel)

        assertThat(getAllNovels()).containsExactly(novel)
    }

    @Test
    fun deleteNovel() = runTest {
        val parentFolder = TestDataUtil.FOLDER_1
        val novel = TestDataUtil.createNovelWithId(id = 1, folderId = parentFolder.id)
        val remainingNovel = TestDataUtil.createNovelWithId(id = 2, folderId = parentFolder.id)

        repository.insertFolder(parentFolder)
        repository.insertNovel(novel)
        repository.insertNovel(remainingNovel)
        repository.deleteNovel(novel)

        assertThat(getAllNovels()).containsExactly(remainingNovel)
    }

    @Test
    fun updateNovel() = runTest {
        val parentFolder = TestDataUtil.FOLDER_1
        val novel = TestDataUtil.createNovelWithId(id = 3, folderId = parentFolder.id)
        val updatedNovel = novel.copy(title = "new", url = "newurl")

        repository.insertFolder(parentFolder)
        repository.insertNovel(novel)
        repository.updateNovel(updatedNovel)

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

        folders.forEach { repository.insertFolder(it) }
        (novels + noMatchNovel).forEach { repository.insertNovel(it) }

        val queriedNovels = repository.findNovelsByNameOrUrl("%hell%")
        assertThat(queriedNovels).containsExactlyElementsIn(novels)
    }

    @Test
    fun getNovels() = runTest {
        val parentFolder = TestDataUtil.FOLDER_1
        val novels = TestDataUtil.createNovels(count = 3, folderId = parentFolder.id)

        repository.insertFolder(parentFolder)
        novels.forEach { repository.insertNovel(it) }

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    @Test
    fun insertFolder() = runTest {
        repository.insertFolder(TestDataUtil.FOLDER_1)

        assertThat(getAllFolders()).contains(TestDataUtil.FOLDER_1)
    }

    @Test
    fun deleteFolder() = runTest {
        val folder = TestDataUtil.FOLDER_1
        val remainingFolder = TestDataUtil.FOLDER_2

        repository.insertFolder(folder)
        repository.insertFolder(remainingFolder)
        repository.deleteFolder(folder)

        assertThat(getAllFolders()).containsExactly(remainingFolder)
    }

    @Test
    fun updateFolder() = runTest {
        val folder = TestDataUtil.FOLDER_1
        val updatedFolder = folder.copy(title = "updated")

        repository.insertFolder(folder)
        repository.updateFolder(updatedFolder)

        assertThat(getAllFolders()).containsExactly(updatedFolder)
    }

    @Test
    fun folderUpdateRetainsNovels() = runTest {
        val folder = TestDataUtil.FOLDER_1
        val updatedFolder = folder.copy(title = "updated")
        val novels = TestDataUtil.createNovels(count = 3, folderId = folder.id)

        repository.insertFolder(folder)
        novels.forEach { repository.insertNovel(it) }
        repository.updateFolder(updatedFolder)

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    @Test
    fun getFolderNames() = runTest {
        val folders = TestDataUtil.createFolders(count = 3)

        folders.forEach { repository.insertFolder(it) }

        val folderNames = folders.map { it.title }
        val dbFolderNames = repository.getFolderNames()
        assertThat(dbFolderNames).isEqualTo(folderNames)
    }

    @Test
    fun insertOrUpdateAddsFolder() = runTest {
        repository.insertOrUpdateFolder(TestDataUtil.FOLDER_1)

        assertThat(getAllFolders()).containsExactly(TestDataUtil.FOLDER_1)
    }

    @Test
    fun insertOrUpdateUpdatesFolder() = runTest {
        val folder = TestDataUtil.FOLDER_1
        val updatedFolder = folder.copy(title = "testing123")

        repository.insertOrUpdateFolder(folder)
        repository.insertOrUpdateFolder(updatedFolder)

        assertThat(getAllFolders()).containsExactly(updatedFolder)
    }

    @Test
    fun insertOrUpdateRetainsNovels() = runTest {
        val folder = TestDataUtil.FOLDER_1
        val updatedFolder = folder.copy(title = "updated")
        val novels = TestDataUtil.createNovels(count = 3, folderId = folder.id)

        repository.insertOrUpdateFolder(folder)
        novels.forEach { repository.insertNovel(it) }
        repository.insertOrUpdateFolder(updatedFolder)

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    @Test
    fun getNovelsFromFolder() = runTest {
        val folder = TestDataUtil.FOLDER_1
        val folderNovels = TestDataUtil.createNovels(count = 5, folderId = folder.id)

        repository.insertFolder(folder)
        folderNovels.forEach { repository.insertNovel(it) }

        val novelsFromFolder = repository.getFolderWithNovels(folder.id).first().novels
        assertThat(novelsFromFolder).containsExactlyElementsIn(folderNovels)
    }

    private suspend fun getAllFolders(): List<Folder> = repository.getFolders().first()
    private suspend fun getAllNovels(): List<Novel> = repository.getNovels().first()
}