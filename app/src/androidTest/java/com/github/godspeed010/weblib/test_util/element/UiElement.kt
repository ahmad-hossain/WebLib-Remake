package com.github.godspeed010.weblib.test_util.element

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule

@OptIn(ExperimentalTestApi::class)
open class UiElement(
    private val composeTestRule: ComposeTestRule,
    val matcher: SemanticsMatcher,
) {
    val node: SemanticsNodeInteraction
        get() = composeTestRule.onNode(matcher)

    fun waitUntilExactlyOneExists() {
        composeTestRule.waitUntilExactlyOneExists(matcher)
    }

    fun waitUntilDoesNotExist() {
        composeTestRule.waitUntilDoesNotExist(matcher)
    }
}