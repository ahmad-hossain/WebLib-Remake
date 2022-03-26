package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        delay(300)
        focusRequester.requestFocus()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissDialog,
    ) {
        Card(
            modifier = modifier
                .width(IntrinsicSize.Min),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 24.dp
        ) {
            Column {
                // Title
                Text(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.h6
                )

                Spacer(Modifier.height(16.dp))

                // TextField
                OutlinedTextField(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp)
                        .width(TextFieldDefaults.MinWidth)
                        .focusRequester(focusRequester),
                    value = folderName,
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

                Spacer(Modifier.height(10.dp))

                //Submit and Cancel Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissDialog) {
                        Text("CANCEL")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onConfirmDialog) {
                        Text("OK")
                    }
                }
            }
        }
    }
}