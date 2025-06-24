package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.AddEditNovelDialog
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@OptIn(ExperimentalComposeUiApi::class)
@PreviewTest
@Preview(showBackground = true)
@Composable
private fun AddEditNovelDialog() {
    WebLibTheme {
        AddEditNovelDialog(
            icon = Icons.Outlined.BookmarkAdd,
            title = "Title",
            novelTitle = TextFieldValue("Novel Name Here"),
            novelUrl = TextFieldValue("URL Here"),
            onNovelTitleChanged = {},
            onNovelUrlChanged = {},
            onDismissDialog = {},
            onConfirmDialog = {}
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@PreviewTest
@Preview(showBackground = true, heightDp = 400, widthDp = 400)
@Composable
private fun AddEditNovelDialog_tallAndWideScreen() {
    WebLibTheme {
        var novelTitle by remember { mutableStateOf(TextFieldValue("Some novel title...")) }
        var novelUrl by remember { mutableStateOf(TextFieldValue("Some novel url...")) }

        AddEditNovelDialog(
            icon = Icons.Outlined.BookmarkAdd,
            title = "Title",
            novelTitle = novelTitle,
            novelUrl = novelUrl,
            onNovelTitleChanged = { novelTitle = it },
            onNovelUrlChanged = { novelUrl = it },
            onDismissDialog = {},
            onConfirmDialog = {}
        )
    }
}
