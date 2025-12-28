package com.github.godspeed010.weblib.test_util.element

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

class FolderElement(
    private val composeTestRule: ComposeTestRule,
    folder: Folder
): UiElement(
    composeTestRule,
    matcher = hasText(folder.title)
) {
    val moreButton: UiElement
        get() = UiElement(
            composeTestRule,
            matcher = hasParent(matcher) and hasContentDescription("More")
        )
}