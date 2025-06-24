package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.NovelItem
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PreviewNovelItem() {
    WebLibTheme {
        NovelItem(
            novel = Novel(
                title = "some novel name",
                url = "https://google.com/",
                scrollProgression = 0f,
                folderId = 0
            ),
            isDropdownExpanded = false,
            onNovelClicked = {},
            onMoreClicked = {},
            onDismissDropdown = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onMoveClicked = {}
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PreviewNovelItem_multilineTitleAndUrl() {
    WebLibTheme {
        NovelItem(
            novel = Novel(
                title = "some very very very very very very very very very very long novel name",
                url = "this is a very very very very very very long url",
                scrollProgression = 0f,
                folderId = 0
            ),
            isDropdownExpanded = false,
            onNovelClicked = {},
            onMoreClicked = {},
            onDismissDropdown = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onMoveClicked = {}
        )
    }
}
