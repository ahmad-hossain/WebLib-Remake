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
class FolderDaoTest {

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
    fun insertFolder() = runBlockingTest {
        val folder = Folder(1, title = "title")
        folderDao.insertFolder(folder)

        val allFolders = folderDao.getFolders().getOrAwaitValue()

        assertThat(allFolders).contains(folder)
    }

    @Test
    fun deleteFolder() = runBlockingTest {
        val folder = Folder(1, "title")
        val remainingFolder = Folder(2, "a_title")
        folderDao.insertFolder(folder)
        folderDao.insertFolder(remainingFolder)
        folderDao.deleteFolder(folder)

        val allFolders = folderDao.getFolders().getOrAwaitValue()

        assertThat(allFolders).containsExactly(remainingFolder)
    }

    @Test
    fun updateFolder() = runBlockingTest {
        val folder = Folder(id = 5, "name")
        folderDao.insertFolder(folder)

        val updatedFolder = Folder(5, "updated")
        folderDao.updateFolder(updatedFolder)

        val allFolders = folderDao.getFolders().getOrAwaitValue()

        assertThat(allFolders).containsExactly(updatedFolder)
    }

    @Test
    fun folderUpdateRetainsNovels() = runBlockingTest {
        val folder = Folder(5, "name")
        folderDao.insertFolder(folder)

        val novels = listOf(
            Novel(1, "title", "url", 5),
            Novel(2, "titleabc", "url", 5),
            Novel(3, "title", "url", 5),
        )
        novels.forEach { novelDao.insertNovel(it) }

        val updatedFolder = Folder(5, "updated")
        folderDao.updateFolder(updatedFolder)

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactlyElementsIn(novels)
    }

    @Test
    fun getFolderNames() = runBlockingTest {
        val folders = listOf(
            Folder(1, "one"),
            Folder(2, "two"),
            Folder(3, "three")
        )

        folders.forEach { folderDao.insertFolder(it) }
        val folderNames = folders.map { it.title }
        val dbFolderNames = folderDao.getFolderNames()

        assertThat(dbFolderNames).isEqualTo(folderNames)
    }
}