package com.github.godspeed010.weblib.feature_library.presentation.folders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.printToLog
import androidx.test.platform.app.InstrumentationRegistry
import com.github.godspeed010.weblib.MainActivity
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.google.common.truth.Truth.assertThat
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint
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

    private object Constants {
        val FOLDERS = listOf(
            Folder(id = 1, title = "folder1"),
            Folder(id = 2, title = "folder2"),
            Folder(id = 3, title = "folder3"),
            Folder(id = 4, title = "folder4"),
        )
        val NOVELS = listOf(
            Novel(
                id = 0,
                title = "novel1",
                url = "https://google.com",
                scrollProgression = 0f,
                folderId = 1
            ),
            Novel(
                id = 1,
                title = "novel2",
                url = "https://example.com",
                scrollProgression = 0f,
                folderId = 1
            ),
            Novel(
                id = 2,
                title = "novel3",
                url = "https://bing.com",
                scrollProgression = 0f,
                folderId = 3
            ),
        )
    }

    @Before
    fun setUp() {
        hiltTestRule.inject()

        runBlocking {
            Constants.FOLDERS.forEach { libraryRepository.insertFolder(it) }
            Constants.NOVELS.forEach { libraryRepository.insertNovel(it) }
        }
    }

    @Test
    fun addFolder_addsFolderAtEnd() = runTest {
        composeTestRule
            .onNodeWithContentDescription(getString(R.string.cd_add_folder))
            .performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("new_folder_1")
        composeTestRule
            .onNodeWithText(getString(R.string.dialog_save))
            .performClick()
        composeTestRule.waitUntilDoesNotExist(hasText(getString(R.string.dialog_save)))

        composeTestRule.onRoot().printToLog("MYTAG")
        assertThat(libraryRepository.getFolderNames()).contains("new_folder_1")
        composeTestRule.onNodeWithText("new_folder_1").assertIsDisplayed()
    }

    @Test
    fun editFolder_folderIsUpdated() = runTest {
        composeTestRule
            .onNode(
                hasParent(hasText("folder1"))
                    .and(hasContentDescription("More"))
            )
            .performClick()
        composeTestRule.onNode(hasText(getString(R.string.edit))).performClick()

        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("edited_folder")
        composeTestRule
            .onNodeWithText(getString(R.string.dialog_save))
            .performClick()
        composeTestRule.waitUntilDoesNotExist(hasText(getString(R.string.dialog_save)))

        assertThat(libraryRepository.getFolderNames()).contains("edited_folder")
        composeTestRule.onNodeWithText("edited_folder").assertIsDisplayed()
    }

    @Test
    fun deleteFolder_folderIsDeleted() = runTest {
        val oldFolderNames = libraryRepository.getFolderNames()
        composeTestRule
            .onNode(
                hasParent(hasText("folder2"))
                    .and(hasContentDescription("More"))
            )
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