package com.github.godspeed010.weblib.test_util.element

import androidx.compose.ui.test.junit4.ComposeTestRule

class Elements(composeTestRule: ComposeTestRule) {
    val foldersScreen = FoldersScreenElements(composeTestRule)
    val novelsScreen = NovelsScreenElements(composeTestRule)
    val addEditFolderDialog = AddEditFolderDialogElements(composeTestRule)
    val addEditNovelDialog = AddEditNovelDialogElements(composeTestRule)
}