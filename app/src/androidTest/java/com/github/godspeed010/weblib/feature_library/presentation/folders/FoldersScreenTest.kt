package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.github.godspeed010.weblib.MainActivity
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.data.data_source.LibraryDatabase
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.test_util.data_set.ManyFoldersAndNovelsDataSet
import com.github.godspeed010.weblib.test_util.getString
import com.github.godspeed010.weblib.test_util.matcher.Matchers
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalTestApi::class
)
@HiltAndroidTest
@RunWith(JUnit4::class)
class FoldersScreenTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var libraryRepository: LibraryRepository
    @Inject
    lateinit var libraryDatabase: LibraryDatabase

    private val dataSet = ManyFoldersAndNovelsDataSet

    @Before
    fun setUp() {
        hiltTestRule.inject()

        runBlocking {
            dataSet.FOLDERS.forEach { libraryRepository.insertFolder(it) }
            dataSet.NOVELS.forEach { libraryRepository.insertNovel(it) }
        }
    }

    @After
    fun tearDown() {
        libraryDatabase.clearAllTables()
        libraryDatabase.close()
    }

    @Test
    fun addFolder_addsFolderAtEnd() = runTest {
        composeTestRule
            .onNode(Matchers.foldersScreen.addFolderFab)
            .performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("new_folder_1")
        composeTestRule
            .onNode(Matchers.addEditFolderDialog.saveButton)
            .performClick()
        composeTestRule.waitUntilDoesNotExist(Matchers.addEditFolderDialog.saveButton)

        assertThat(libraryRepository.getFolderNames()).contains("new_folder_1")
        composeTestRule.onNodeWithText("new_folder_1").assertIsDisplayed()
    }

    @Test
    fun editFolder_folderIsUpdated() = runTest {
        composeTestRule
            .onNode(Matchers.foldersScreen.folder(dataSet.FOLDERS[0]).moreButton)
            .performClick()
        composeTestRule.onNode(hasText(getString(R.string.edit))).performClick()

        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("edited_folder")
        composeTestRule
            .onNode(Matchers.addEditFolderDialog.saveButton)
            .performClick()
        composeTestRule.waitUntilDoesNotExist(Matchers.addEditFolderDialog.saveButton)

        assertThat(libraryRepository.getFolderNames()).contains("edited_folder")
        composeTestRule.onNodeWithText("edited_folder").assertIsDisplayed()
    }

    @Test
    fun deleteFolder_folderIsDeleted() = runTest {
        val oldFolderNames = libraryRepository.getFolderNames()
        composeTestRule
            .onNode(Matchers.foldersScreen.folder(dataSet.FOLDERS[1]).moreButton)
            .performClick()
        composeTestRule.onNode(hasText(getString(R.string.delete))).performClick()

        assertThat(oldFolderNames).contains("folder2")
        composeTestRule.waitUntilDoesNotExist(hasText("folder2"))
        composeTestRule.waitUntilDoesNotExist(hasText("Undo"), timeoutMillis = 5_000L)
        assertThat(libraryRepository.getFolderNames()).doesNotContain("folder2")
    }

    @Test
    fun clickFolder_folderWithNovels_expectedNovelsAreVisibleOnNovelsScreen() = runTest {
        composeTestRule
            .onNode(Matchers.foldersScreen.folder(dataSet.FOLDERS[0]).matcher)
            .performClick()
        val novelsScreen = Matchers.novelsScreen

        val folder1Novels = dataSet.NOVELS_FOLDER_1
        composeTestRule.waitUntilExactlyOneExists(
            novelsScreen.novel(name = folder1Novels[0].title, url = folder1Novels[0].url).matcher
        )
        composeTestRule.waitUntilExactlyOneExists(
            novelsScreen.novel(name = folder1Novels[1].title, url = folder1Novels[1].url).matcher
        )
        composeTestRule.waitUntilDoesNotExist(
            novelsScreen.novel(dataSet.NOVELS_FOLDER_3[0].title).matcher
        )
    }
}