package com.github.godspeed010.weblib.test_util.element

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule

class NovelElement(
    private val composeTestRule: ComposeTestRule,
    name: String,
    url: String?
) : UiElement(
    composeTestRule,
    matcher = when (url == null) {
        true -> hasText(name)
        false -> hasText(name) and hasText(url)
    }
) {
    val moreButton: UiElement
        get() = UiElement(
            composeTestRule,
            matcher = hasParent(matcher) and hasContentDescription("More")
        )
}