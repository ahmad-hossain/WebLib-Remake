package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.presentation.folders.FoldersEvent
import com.github.godspeed010.weblib.ui.theme.Shapes

@Composable
fun AddEditFolderDialog(
    modifier: Modifier = Modifier,
    title: String,
    folderName: String,
    onTextChange: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDialog: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        onDismissRequest = onDismissDialog,
        title = {
            Text(title)
        },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = {
                    onTextChange(it)
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmDialog) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissDialog) {
                Text("CANCEL")
            }
        }
    )
}