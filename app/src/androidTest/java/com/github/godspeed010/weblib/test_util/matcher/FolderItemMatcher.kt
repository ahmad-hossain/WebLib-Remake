package com.github.godspeed010.weblib.test_util.matcher

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText

class FolderItemMatcher(
    private val folderName: String
): BaseMatcher() {
    override val matcher: SemanticsMatcher
        get() = hasText(folderName)

    val moreButton: SemanticsMatcher
        get() = hasParent(matcher) and hasContentDescription("More")
}