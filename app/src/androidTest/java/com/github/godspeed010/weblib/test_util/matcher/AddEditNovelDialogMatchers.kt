package com.github.godspeed010.weblib.test_util.matcher

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEditable
import com.github.godspeed010.weblib.test_util.getString
import com.github.godspeed010.weblib.R

object AddEditNovelDialogMatchers: AddEditItemDialogMatchers() {
    val novelTitleTextField: SemanticsMatcher
        get() = isEditable() and hasText(getString(R.string.hint_title))
    val novelUrlTextField: SemanticsMatcher
        get() = isEditable() and hasText(getString(R.string.hint_url))
}