package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.MoveNovelModalBottomSheet
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@PreviewTest
@Preview(showBackground = true)
@Composable
private fun MoveNovelModalBottomSheet() {
    WebLibTheme {
        MoveNovelModalBottomSheet(
            isVisible = true,
            bottomSheetState = SheetState(
                skipPartiallyExpanded = true,
                initialValue = SheetValue.Expanded
            ),
            folders = listOf(
                Folder(title = "folder 1"),
                Folder(title = "folder 2"),
                Folder(title = "Very very very very very very very very very very long folder name"),
                Folder(title = "folder 4"),
            ),
            onBottomSheetDismissed = {},
            onFolderClicked = {}
        )
    }
}
