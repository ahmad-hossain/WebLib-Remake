package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.common.components.CustomDialog
import kotlinx.coroutines.delay

@ExperimentalComposeUiApi
@Composable
fun AddEditNovelDialog(
    modifier: Modifier = Modifier,
    title: String,
    novelTitle: String,
    novelUrl: String,
    onNovelTitleChanged: (String) -> Unit,
    onNovelUrlChanged: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDialog: () -> Unit
) {
    CustomDialog(
        modifier = modifier,
        onDismissDialog = onDismissDialog,
        title = {
            Text(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp),
                text = title,
                style = MaterialTheme.typography.h6
            )
        },
        content = {
            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                delay(300)
                focusRequester.requestFocus()
            }
            val keyboardController = LocalSoftwareKeyboardController.current
            val localClipboardManager = LocalClipboardManager.current

            // Novel Name TextField
            OutlinedTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp)
                    .width(TextFieldDefaults.MinWidth)
                    .focusRequester(focusRequester),
                value = novelTitle,
                label = { Text(stringResource(id = R.string.hint_title)) },
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

            Spacer(Modifier.height(10.dp))

            // Novel URL TextField
            OutlinedTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp)
                    .width(TextFieldDefaults.MinWidth),
                value = novelUrl,
                label = { Text(stringResource(R.string.hint_url)) },
                onValueChange = {
                    onNovelUrlChanged(it)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        onNovelUrlChanged(localClipboardManager.getText().toString())
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ContentPaste,
                            contentDescription = "Paste"
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
        },
        confirmButton = {
            TextButton(onClick = onDismissDialog) { Text("CANCEL") }
        },
        dismissButton = {
            TextButton(onClick = onConfirmDialog) { Text("OK") }
        }
    )
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun AddEditNovelPreview() {
    AddEditNovelDialog(
        title = "Title",
        novelTitle = "Novel Name Here",
        novelUrl = "URL Here",
        onNovelTitleChanged = {},
        onNovelUrlChanged = {},
        onDismissDialog = {},
        onConfirmDialog = {}
    )
}