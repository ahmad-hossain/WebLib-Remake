package com.github.godspeed010.weblib.test_util.element

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEditable
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.test_util.getString

abstract class AddEditItemDialogElements(
    private val composeTestRule: ComposeTestRule
) {
    val titleTextField: UiElement
        get() = UiElement(
            composeTestRule,
            matcher = isEditable() and hasText(getString(R.string.hint_title))
        )
    val saveButton: UiElement
        get() = UiElement(
            composeTestRule,
            matcher = hasText(getString(R.string.dialog_save))
        )
}