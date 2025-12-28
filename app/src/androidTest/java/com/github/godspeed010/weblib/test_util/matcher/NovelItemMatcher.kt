package com.github.godspeed010.weblib.test_util.matcher

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText

class NovelItemMatcher(
    private val name: String,
    private val url: String?
): BaseMatcher() {
    override val matcher: SemanticsMatcher
        get() = when (url == null) {
            true -> hasText(name)
            false -> hasText(name) and hasText(url)
        }

    val moreButton: SemanticsMatcher
        get() = hasParent(matcher) and hasContentDescription("More")
}