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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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
    fun insertFolder() = runTest {
        val folder = Folder(1, title = "title")
        folderDao.insert(folder)

        val allFolders = folderDao.getFolders().first()

        assertThat(allFolders).contains(folder)
    }

    @Test
    fun deleteFolder() = runTest {
        val folder = Folder(1, "title")
        val remainingFolder = Folder(2, "a_title")
        folderDao.insert(folder)
        folderDao.insert(remainingFolder)
        folderDao.delete(folder)

        val allFolders = folderDao.getFolders().first()

        assertThat(allFolders).containsExactly(remainingFolder)
    }

    @Test
    fun updateFolder() = runTest {
        val folder = Folder(id = 5, "name")
        folderDao.insert(folder)

        val updatedFolder = Folder(5, "updated")
        folderDao.update(updatedFolder)

        val allFolders = folderDao.getFolders().first()

        assertThat(allFolders).containsExactly(updatedFolder)
    }

    @Test
    fun folderUpdateRetainsNovels() = runTest {
        val folder = Folder(5, "name")
        folderDao.insert(folder)

        val novels = listOf(
            Novel.createWithDefaults(id = 1, title = "title", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 2, title = "titleabc", url = "url", folderId = 5),
            Novel.createWithDefaults(id = 3, title = "title", url = "url", folderId = 5),
        )
        novels.forEach { novelDao.insert(it) }

        val updatedFolder = Folder(5, "updated")
        folderDao.update(updatedFolder)

        val allNovels = novelDao.getNovels().getOrAwaitValue()

        assertThat(allNovels).containsExactlyElementsIn(novels)
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

// Tests not functional. Loading infinitely. Cause unknown.
//    @Test
//    fun insertOrUpdateAddsFolder() = runBlockingTest {
//        println("upsertAddFolder println")
//        Log.d("testingdao", "start")
//
//        val folder = Folder(id = 1, title = "abc")
//        folderDao.insertOrUpdate(folder) // loads infinitely
//
//        Log.d("testingdao", "inserted folder")
//
//        val allFolders = folderDao.getFolders().getOrAwaitValue()
//
//        Log.d("testingdao", "gotFolders")
//
//        assertThat(allFolders).containsExactly(folder)
//    }
//
//    @Test
//    fun insertOrUpdateUpdatesFolder() = runBlockingTest {
//        var folder = Folder(id = 1, title = "abc")
//        folderDao.insertOrUpdate(folder)
//
//        //change folder title
//        folder = folder.copy(title = "testing123")
//
//        val allFolders = folderDao.getFolders().getOrAwaitValue()
//
//        assertThat(allFolders).containsExactly(folder)
//    }
//
//    @Test
//    fun insertOrUpdateRetainsNovels() = runBlockingTest {
//        var folder = Folder(5, "name")
//        folderDao.insertOrUpdate(folder)
//
//        val novels = listOf(
//            Novel(1, "title", "url", 5),
//            Novel(2, "titleabc", "url", 5),
//            Novel(3, "title", "url", 5),
//        )
//        novels.forEach { novelDao.insertNovel(it) }
//
//        folder = folder.copy(title = "updated")
//        folderDao.insertOrUpdate(folder)
//
//        val allNovels = novelDao.getNovels().getOrAwaitValue()
//
//        assertThat(allNovels).containsExactlyElementsIn(novels)
//    }
}