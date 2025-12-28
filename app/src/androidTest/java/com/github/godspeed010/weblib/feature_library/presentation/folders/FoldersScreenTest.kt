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
import com.github.godspeed010.weblib.test_util.element.FoldersScreenElements
import com.github.godspeed010.weblib.test_util.element.Elements
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
    private lateinit var elements: Elements
    private lateinit var foldersScreen: FoldersScreenElements

    @Before
    fun setUp() {
        hiltTestRule.inject()
        elements = Elements(composeTestRule)
        foldersScreen = elements.foldersScreen

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
        val addFolderDialog = elements.addEditFolderDialog
        foldersScreen.addFolderFab.node.performClick()
        addFolderDialog.titleTextField.node.performTextInput("new_folder_1")
        addFolderDialog.saveButton.node.performClick()
        addFolderDialog.saveButton.waitUntilDoesNotExist()

        assertThat(libraryRepository.getFolderNames()).contains("new_folder_1")
        composeTestRule.onNodeWithText("new_folder_1").assertIsDisplayed()
    }

    @Test
    fun editFolder_folderIsUpdated() = runTest {
        foldersScreen.folder(dataSet.FOLDERS[0]).moreButton.node.performClick()
        composeTestRule.onNode(hasText(getString(R.string.edit))).performClick()

        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("edited_folder")
        elements.addEditFolderDialog.saveButton.node.performClick()
        elements.addEditFolderDialog.saveButton.waitUntilDoesNotExist()

        assertThat(libraryRepository.getFolderNames()).contains("edited_folder")
        composeTestRule.onNodeWithText("edited_folder").assertIsDisplayed()
    }

    @Test
    fun deleteFolder_folderIsDeleted() = runTest {
        val oldFolderNames = libraryRepository.getFolderNames()
        foldersScreen.folder(dataSet.FOLDERS[1]).moreButton.node.performClick()
        composeTestRule.onNode(hasText(getString(R.string.delete))).performClick()

        assertThat(oldFolderNames).contains("folder2")
        composeTestRule.waitUntilDoesNotExist(hasText("folder2"))
        composeTestRule.waitUntilDoesNotExist(hasText("Undo"), timeoutMillis = 5_000L)
        assertThat(libraryRepository.getFolderNames()).doesNotContain("folder2")
    }

    @Test
    fun clickFolder_folderWithNovels_expectedNovelsAreVisibleOnNovelsScreen() = runTest {
        foldersScreen.folder(dataSet.FOLDERS[0]).node.performClick()
        val novelsScreen = elements.novelsScreen

        val folder1Novels = dataSet.NOVELS_FOLDER_1
        novelsScreen.novel(name = folder1Novels[0].title, url = folder1Novels[0].url)
            .waitUntilExactlyOneExists()
        novelsScreen.novel(name = folder1Novels[1].title, url = folder1Novels[1].url)
            .waitUntilExactlyOneExists()
        novelsScreen.novel(dataSet.NOVELS_FOLDER_3[0].title)
            .waitUntilDoesNotExist()
    }
}