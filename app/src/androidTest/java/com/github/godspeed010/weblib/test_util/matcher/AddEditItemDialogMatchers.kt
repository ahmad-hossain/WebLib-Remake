package com.github.godspeed010.weblib.test_util.matcher

import androidx.compose.ui.test.SemanticsMatcher
import com.github.godspeed010.weblib.R
import androidx.compose.ui.test.hasText
import com.github.godspeed010.weblib.test_util.getString

object AddEditItemDialogMatchers {
    val saveButton: SemanticsMatcher
        get() = hasText(getString(R.string.dialog_save))
}