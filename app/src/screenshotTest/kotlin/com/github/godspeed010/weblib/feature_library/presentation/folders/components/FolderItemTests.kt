package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PreviewFolderItem() {
    WebLibTheme {
        FolderItem(
            folder = Folder(title = "some folder name"),
            isDropdownExpanded = false,
            onFolderClicked = {},
            isTrailingContentVisible = true
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PreviewFolderItem_multilineTitle() {
    WebLibTheme {
        FolderItem(
            folder = Folder(title = "some folder name with very very very very very very long title"),
            isDropdownExpanded = false,
            onFolderClicked = {},
            isTrailingContentVisible = true
        )
    }
}
