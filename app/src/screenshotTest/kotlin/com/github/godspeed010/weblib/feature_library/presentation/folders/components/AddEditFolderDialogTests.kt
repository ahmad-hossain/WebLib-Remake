package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@OptIn(ExperimentalComposeUiApi::class)
@PreviewTest
@Preview(showBackground = true)
@Composable
private fun AddEditFolderDialog() {
    WebLibTheme {
        AddEditFolderDialog(
            title = "Some Title",
            icon = Icons.Default.CreateNewFolder,
            folderName = TextFieldValue("some folder name"),
            onTextChange = {},
            onDismissDialog = {},
            onConfirmDialog = {}
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@PreviewTest
@Preview(showBackground = true, heightDp = 400, widthDp = 400)
@Composable
private fun AddEditFolderDialog_tallAndWideScreen() {
    WebLibTheme {
        AddEditFolderDialog(
            title = "Some Title",
            icon = Icons.Default.CreateNewFolder,
            folderName = TextFieldValue("some folder name"),
            onTextChange = {},
            onDismissDialog = {},
            onConfirmDialog = {}
        )
    }
}
