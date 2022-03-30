package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

@Composable
fun FolderItem(
    folder: Folder,
    expandedDropdownFolderId: Int?,
    modifier: Modifier = Modifier,
    onFolderClicked: () -> Unit,
    onMoreClicked: () -> Unit,
    onDismissDropdown: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
    ) {
        val dropdownOptions = listOf("Move", "More", "Delete")
        DropdownMenu(
            expanded = expandedDropdownFolderId == folder.id,
            onDismissRequest = onDismissDropdown
        ) {
            dropdownOptions.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = { /*TODO*/ }) {
                    Text(s)
                }
            }
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onFolderClicked()
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = "Folder"
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = folder.title
        )
        IconButton(onClick = onMoreClicked) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }
    }
}

@Preview
@Composable
fun FolderItemPreview() {
    FolderItem(
        folder = Folder(title = "Testing", id = 0),
        expandedDropdownFolderId = 0,
        onFolderClicked = {},
        onMoreClicked = {},
        onDismissDropdown = {}
    )
}