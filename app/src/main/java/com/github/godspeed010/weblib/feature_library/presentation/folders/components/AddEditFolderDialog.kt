package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.github.godspeed010.weblib.R

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun AddEditFolderDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    folderName: String,
    onTextChange: (String) -> Unit,
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
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(true) {
                focusRequester.requestFocus()
            }
            val keyboardController = LocalSoftwareKeyboardController.current

            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
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
    )
}