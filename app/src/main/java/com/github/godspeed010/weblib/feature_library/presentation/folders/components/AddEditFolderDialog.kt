package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.common.components.CustomDialog
import kotlinx.coroutines.delay

@ExperimentalComposeUiApi
@Composable
fun AddEditFolderDialog(
    modifier: Modifier = Modifier,
    title: String,
    folderName: String,
    onTextChange: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDialog: () -> Unit
) {
    CustomDialog(
        modifier = modifier,
        onDismissDialog = onDismissDialog,
        title = {
            Text(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp),
                text = title,
                style = MaterialTheme.typography.h6
            )
        },
        content = {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(true) {
                delay(300)
                focusRequester.requestFocus()
            }
            val keyboardController = LocalSoftwareKeyboardController.current

            OutlinedTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp)
                    .width(TextFieldDefaults.MinWidth)
                    .focusRequester(focusRequester),
                value = folderName,
                label = { Text(stringResource(R.string.hint_title)) },
                onValueChange = {
                    onTextChange(it)
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