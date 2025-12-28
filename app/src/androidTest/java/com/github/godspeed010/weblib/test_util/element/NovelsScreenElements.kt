package com.github.godspeed010.weblib.test_util.element

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.test_util.getString

class NovelsScreenElements(
    private val composeTestRule: ComposeTestRule,
) {
    val addNovelFab: UiElement
        get() = UiElement(
            composeTestRule,
            matcher = hasContentDescription(getString(R.string.cd_add_novel))
        )

    fun novel(name: String, url: String? = null) = NovelElement(composeTestRule, name, url)
}