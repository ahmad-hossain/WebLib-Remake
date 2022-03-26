package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

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
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val localClipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismissDialog,
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 24.dp
        ) {
            Column {
                // Title
                Text(
                    modifier = Modifier
                        .firstBaselineToTop(40.dp)
                        .padding(horizontal = 24.dp),
                    text = title,
                    style = MaterialTheme.typography.h6
                )

                Spacer(Modifier.height(16.dp))

                // Novel Name TextField
                OutlinedTextField(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp)
                        .focusRequester(focusRequester),
                    value = novelTitle,
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
                        .padding(horizontal = 24.dp),
                    value = novelUrl,
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

fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = layout { measurable, constraints ->
    // Measure the composable
    val placeable = measurable.measure(constraints)

    // Check the composable has a first baseline
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]

    // Height of the composable with padding - first baseline
    val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
    val height = placeable.height + placeableY
    layout(placeable.width, height) {
        // Where the composable gets placed
        placeable.placeRelative(0, placeableY)
    }
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