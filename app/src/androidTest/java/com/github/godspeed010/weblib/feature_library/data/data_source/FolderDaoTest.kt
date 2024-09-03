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
class FolderDaoTest {

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
    fun insertFolder() = runTest {
        val folder = Folder(1, title = "title")

        folderDao.insert(folder)

        assertThat(getAllFolders()).contains(folder)
    }

    @Test
    fun deleteFolder() = runTest {
        val folder = Folder(1, "title")
        val remainingFolder = Folder(2, "a_title")

        folderDao.insert(folder)
        folderDao.insert(remainingFolder)
        folderDao.delete(folder)

        assertThat(getAllFolders()).containsExactly(remainingFolder)
    }

    @Test
    fun updateFolder() = runTest {
        val folder = Folder(id = 5, "name")
        val updatedFolder = Folder(5, "updated")

        folderDao.insert(folder)
        folderDao.update(updatedFolder)

        assertThat(getAllFolders()).containsExactly(updatedFolder)
    }

    @Test
    fun folderUpdateRetainsNovels() = runTest {
        val folder = Folder(5, "name")
        val updatedFolder = folder.copy(title = "updated")
        val novels = listOf(
            Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 2, title = "titleabc", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 3, title = "title", url = "url", folderId = 5),
        )

        folderDao.insert(folder)
        novels.forEach { novelDao.insert(it) }
        folderDao.update(updatedFolder)

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    @Test
    fun getFolderNames() = runTest {
        val folders = listOf(
            Folder(1, "one"),
            Folder(2, "two"),
            Folder(3, "three")
        )

        folders.forEach { folderDao.insert(it) }

        val folderNames = folders.map { it.title }
        val dbFolderNames = folderDao.getFolderNames()
        assertThat(dbFolderNames).isEqualTo(folderNames)
    }

    @Test
    fun insertOrUpdateAddsFolder() = runTest {
        val folder = Folder(id = 1, title = "abc")

        folderDao.insertOrUpdate(folder)

        assertThat(getAllFolders()).containsExactly(folder)
    }

    @Test
    fun insertOrUpdateUpdatesFolder() = runTest {
        val folder = Folder(id = 1, title = "abc")
        val updatedFolder = folder.copy(title = "testing123")

        folderDao.insertOrUpdate(folder)
        folderDao.insertOrUpdate(updatedFolder)

        assertThat(getAllFolders()).containsExactly(updatedFolder)
    }

    @Test
    fun insertOrUpdateRetainsNovels() = runTest {
        val folder = Folder(5, "name")
        val updatedFolder = folder.copy(title = "updated")
        val novels = listOf(
            Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 2, title = "titleabc", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 3, title = "title", url = "url", folderId = 5),
        )

        folderDao.insertOrUpdate(folder)
        novels.forEach { novelDao.insert(it) }
        folderDao.insertOrUpdate(updatedFolder)

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    private suspend fun getAllFolders(): List<Folder> = folderDao.getFolders().first()
    private suspend fun getAllNovels(): List<Novel> = novelDao.getNovels().first()
}
