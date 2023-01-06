package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun AddEditNovelDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    novelTitle: String,
    novelUrl: String,
    onNovelTitleChanged: (String) -> Unit,
    onNovelUrlChanged: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDialog: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissDialog,
        confirmButton = { TextButton(onClick = onConfirmDialog) { Text(stringResource(R.string.dialog_save)) } },
        dismissButton = { TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.dialog_cancel)) } },
        icon = { Icon(icon, contentDescription = null) },
        title = { Text(title) },
        text = {
            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(true) {
                focusRequester.requestFocus()
            }
            val keyboardController = LocalSoftwareKeyboardController.current
            val localClipboardManager = LocalClipboardManager.current

            Column {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = novelTitle,
                    label = { Text(stringResource(R.string.hint_title)) },
                    onValueChange = {
                        onNovelTitleChanged(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = novelUrl,
                    label = { Text(stringResource(R.string.hint_url)) },
                    onValueChange = { onNovelUrlChanged(it) },
                    trailingIcon = {
                        IconButton(onClick = {
                            onNovelUrlChanged(localClipboardManager.getText().toString())
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.ContentPaste,
                                contentDescription = stringResource(R.string.cd_paste)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onConfirmDialog()
                        }
                    )
                )
            }
        },
    )
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun AddEditNovelPreview() {
    AddEditNovelDialog(
        icon = Icons.Outlined.BookmarkAdd,
        title = "Title",
        novelTitle = "Novel Name Here",
        novelUrl = "URL Here",
        onNovelTitleChanged = {},
        onNovelUrlChanged = {},
        onDismissDialog = {},
        onConfirmDialog = {}
    )
}