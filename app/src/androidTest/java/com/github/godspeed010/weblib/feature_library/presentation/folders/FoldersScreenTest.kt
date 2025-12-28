package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.annotation.StringRes
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
import androidx.test.platform.app.InstrumentationRegistry
import com.github.godspeed010.weblib.MainActivity
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.test_util.data_set.ManyFoldersAndNovelsDataSet
import com.github.godspeed010.weblib.test_util.matcher.Matchers
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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

    private val dataSet = ManyFoldersAndNovelsDataSet
    private val matchers = Matchers()

    @Before
    fun setUp() {
        hiltTestRule.inject()

        runBlocking {
            dataSet.FOLDERS.forEach { libraryRepository.insertFolder(it) }
            dataSet.NOVELS.forEach { libraryRepository.insertNovel(it) }
        }
    }

    @Test
    fun addFolder_addsFolderAtEnd() = runTest {
        composeTestRule
            .onNode(matchers.foldersScreen.addFolderFab)
            .performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("new_folder_1")
        composeTestRule
            .onNode(matchers.addEditItemDialog.saveButton)
            .performClick()
        composeTestRule.waitUntilDoesNotExist(matchers.addEditItemDialog.saveButton)

        assertThat(libraryRepository.getFolderNames()).contains("new_folder_1")
        composeTestRule.onNodeWithText("new_folder_1").assertIsDisplayed()
    }

    @Test
    fun editFolder_folderIsUpdated() = runTest {
        composeTestRule
            .onNode(matchers.foldersScreen.folder("folder1").moreButton)
            .performClick()
        composeTestRule.onNode(hasText(getString(R.string.edit))).performClick()

        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("edited_folder")
        composeTestRule
            .onNode(matchers.addEditItemDialog.saveButton)
            .performClick()
        composeTestRule.waitUntilDoesNotExist(matchers.addEditItemDialog.saveButton)

        assertThat(libraryRepository.getFolderNames()).contains("edited_folder")
        composeTestRule.onNodeWithText("edited_folder").assertIsDisplayed()
    }

    @Test
    fun deleteFolder_folderIsDeleted() = runTest {
        val oldFolderNames = libraryRepository.getFolderNames()
        composeTestRule
            .onNode(matchers.foldersScreen.folder("folder2").moreButton)
            .performClick()
        composeTestRule.onNode(hasText(getString(R.string.delete))).performClick()

        assertThat(oldFolderNames).contains("folder2")
        composeTestRule.waitUntilDoesNotExist(hasText("folder2"))
        composeTestRule.waitUntilDoesNotExist(hasText("Undo"), timeoutMillis = 5_000L)
        assertThat(libraryRepository.getFolderNames()).doesNotContain("folder2")
    }

    private fun getString(@StringRes id: Int) =
        InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id)
}