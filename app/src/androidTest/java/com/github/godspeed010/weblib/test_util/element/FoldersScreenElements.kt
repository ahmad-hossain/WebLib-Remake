package com.github.godspeed010.weblib.test_util.element

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.test_util.getString

class FoldersScreenElements(
    private val composeTestRule: ComposeTestRule,
) {
    val addFolderFab: UiElement
        get() = UiElement(
            composeTestRule,
            matcher = hasContentDescription(getString(R.string.cd_add_folder))
        )

    fun folder(folder: Folder) = FolderElement(composeTestRule, folder)
}