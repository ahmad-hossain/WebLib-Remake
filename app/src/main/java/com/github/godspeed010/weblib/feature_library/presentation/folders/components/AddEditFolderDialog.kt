package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.common.components.FixedWidthOutlinedTextField
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@ExperimentalComposeUiApi
@Composable
fun AddEditFolderDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    folderName: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDialog: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
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

            FixedWidthOutlinedTextField(
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

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
private fun PreviewAddEditFolderDialog() {
    WebLibTheme {
        AddEditFolderDialog(
            title = "",
            icon = Icons.Default.CreateNewFolder,
            folderName = TextFieldValue("some folder name"),
            onTextChange = {},
            onDismissDialog = {},
            onConfirmDialog = {}
        )
    }
}