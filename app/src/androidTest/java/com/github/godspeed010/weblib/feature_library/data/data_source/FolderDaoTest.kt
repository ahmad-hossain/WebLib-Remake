package com.github.godspeed010.weblib.feature_library.data.data_source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.godspeed010.weblib.feature_library.common.TestDataUtil
import com.github.godspeed010.weblib.feature_library.common.TestDataUtil.FOLDER_1
import com.github.godspeed010.weblib.feature_library.common.TestDataUtil.FOLDER_2
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
        folderDao.insert(FOLDER_1)

        assertThat(getAllFolders()).contains(FOLDER_1)
    }

    @Test
    fun deleteFolder() = runTest {
        val folder = FOLDER_1
        val remainingFolder = FOLDER_2

        folderDao.insert(folder)
        folderDao.insert(remainingFolder)
        folderDao.delete(folder)

        assertThat(getAllFolders()).containsExactly(remainingFolder)
    }

    @Test
    fun updateFolder() = runTest {
        val folder = FOLDER_1
        val updatedFolder = folder.copy(title = "updated")

        folderDao.insert(folder)
        folderDao.update(updatedFolder)

        assertThat(getAllFolders()).containsExactly(updatedFolder)
    }

    @Test
    fun folderUpdateRetainsNovels() = runTest {
        val folder = FOLDER_1
        val updatedFolder = folder.copy(title = "updated")
        val novels = TestDataUtil.createNovels(count = 3, folderId = folder.id)

        folderDao.insert(folder)
        novels.forEach { novelDao.insert(it) }
        folderDao.update(updatedFolder)

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    @Test
    fun getFolderNames() = runTest {
        val folders = TestDataUtil.createFolders(count = 3)

        folders.forEach { folderDao.insert(it) }

        val folderNames = folders.map { it.title }
        val dbFolderNames = folderDao.getFolderNames()
        assertThat(dbFolderNames).isEqualTo(folderNames)
    }

    @Test
    fun insertOrUpdateAddsFolder() = runTest {
        folderDao.insertOrUpdate(FOLDER_1)

        assertThat(getAllFolders()).containsExactly(FOLDER_1)
    }

    @Test
    fun insertOrUpdateUpdatesFolder() = runTest {
        val folder = FOLDER_1
        val updatedFolder = folder.copy(title = "testing123")

        folderDao.insertOrUpdate(folder)
        folderDao.insertOrUpdate(updatedFolder)

        assertThat(getAllFolders()).containsExactly(updatedFolder)
    }

    @Test
    fun insertOrUpdateRetainsNovels() = runTest {
        val folder = FOLDER_1
        val updatedFolder = folder.copy(title = "updated")
        val novels = TestDataUtil.createNovels(count = 3, folderId = folder.id)

        folderDao.insertOrUpdate(folder)
        novels.forEach { novelDao.insert(it) }
        folderDao.insertOrUpdate(updatedFolder)

        assertThat(getAllNovels()).containsExactlyElementsIn(novels)
    }

    private suspend fun getAllFolders(): List<Folder> = folderDao.getFolders().first()
    private suspend fun getAllNovels(): List<Novel> = novelDao.getNovels().first()
}
