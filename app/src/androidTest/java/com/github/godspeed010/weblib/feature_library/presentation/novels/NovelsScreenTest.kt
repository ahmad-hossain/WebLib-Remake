package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import kotlinx.coroutines.flow.first
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
class NovelsScreenTest {
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
    fun addNovel_addsNovelAtEnd() = runTest {
        val folder = dataSet.FOLDERS[0]
        composeTestRule
            .onNode(Matchers.foldersScreen.folder(folder).matcher)
            .performClick()

        composeTestRule
            .onNode(Matchers.novelsScreen.addNovelFab)
            .performClick()
        composeTestRule
            .onNode(Matchers.addEditNovelDialog.novelTitleTextField)
            .performTextInput("new_novel_1")
        composeTestRule
            .onNode(Matchers.addEditNovelDialog.novelUrlTextField)
            .performTextInput("https://example.com/new_novel_1")
        composeTestRule
            .onNode(Matchers.addEditNovelDialog.saveButton)
            .performClick()

        composeTestRule.waitUntilExactlyOneExists(hasText("new_novel_1"))
        composeTestRule.waitUntilExactlyOneExists(hasText("https://example.com/new_novel_1"))
        val newNovel =
            libraryRepository.getNovels().first().firstOrNull { it.title == "new_novel_1" }
        assertThat(newNovel).isNotNull()
        assertThat(newNovel!!.title).isEqualTo("new_novel_1")
        assertThat(newNovel.url).isEqualTo("https://example.com/new_novel_1")
    }

    @Test
    fun editNovel_novelIsUpdated() = runTest {
        val folder = dataSet.FOLDERS[0]
        val novel = dataSet.NOVELS_FOLDER_1[0]
        composeTestRule
            .onNode(Matchers.foldersScreen.folder(folder).matcher)
            .performClick()

        composeTestRule
            .onNode(Matchers.novelsScreen.novel(novel.title).moreButton)
            .performClick()
        composeTestRule.onNode(hasText(getString(R.string.edit))).performClick()
        composeTestRule
            .onNode(Matchers.addEditNovelDialog.novelTitleTextField)
            .performTextReplacement("updated_novel_1")
        composeTestRule
            .onNode(Matchers.addEditNovelDialog.novelUrlTextField)
            .performTextReplacement("https://example.com/updated_novel_1")
        composeTestRule
            .onNode(Matchers.addEditNovelDialog.saveButton)
            .performClick()

        composeTestRule.waitUntilDoesNotExist(hasText(novel.title))
        composeTestRule.waitUntilDoesNotExist(hasText(novel.url))
        composeTestRule.waitUntilExactlyOneExists(hasText("updated_novel_1"))
        composeTestRule.waitUntilExactlyOneExists(hasText("https://example.com/updated_novel_1"))
        val novels = libraryRepository.getNovels().first()
        val oldNovel = novels.firstOrNull { it.title == novel.title }
        val updatedNovel = novels.firstOrNull { it.title == "updated_novel_1" }
        assertThat(oldNovel).isNull()
        assertThat(updatedNovel).isNotNull()
        assertThat(updatedNovel!!.title).isEqualTo("updated_novel_1")
        assertThat(updatedNovel.url).isEqualTo("https://example.com/updated_novel_1")
    }

    @Test
    fun deleteNovel_novelIsDeleted() = runTest {
        val folder = dataSet.FOLDERS[0]
        val novel = dataSet.NOVELS_FOLDER_1[0]
        composeTestRule
            .onNode(Matchers.foldersScreen.folder(folder).matcher)
            .performClick()

        composeTestRule
            .onNode(Matchers.novelsScreen.novel(novel.title).moreButton)
            .performClick()
        composeTestRule
            .onNode(hasText(getString(R.string.delete)))
            .performClick()

        composeTestRule.waitUntilDoesNotExist(hasText("Undo"), timeoutMillis = 5_000L)
        composeTestRule.waitUntilDoesNotExist(hasText(novel.title))
        composeTestRule.waitUntilDoesNotExist(hasText(novel.url))
        val deletedNovel = libraryRepository.getNovels().first().firstOrNull { it.title == novel.title }
        assertThat(deletedNovel).isNull()
    }
}
