package com.github.godspeed010.weblib.feature_library.presentation.folders.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

@Composable
fun FolderItem(
    folder: Folder,
    isDropdownExpanded: Boolean,
    modifier: Modifier = Modifier,
    onFolderClicked: () -> Unit,
    onMoreClicked: () -> Unit,
    onDismissDropdown: () -> Unit,
    dropdownOptions: Array<String> = stringArrayResource(id = R.array.folder_dropdown_options),
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val edit = stringResource(id = R.string.edit)
    val delete = stringResource(id = R.string.delete)

    ListItem(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onFolderClicked() }
            .padding(vertical = 8.dp),
        headlineContent = { Text(text = folder.title) },
        leadingContent = { Icon(imageVector = Icons.Outlined.Folder, contentDescription = "Folder") },
        trailingContent = {
            Box {
                IconButton(onClick = onMoreClicked) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, contentDescription = "More"
                    )
                }
                DropdownMenu(
                    expanded = isDropdownExpanded, onDismissRequest = onDismissDropdown
                ) {
                    dropdownOptions.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                when (s) {
                                    edit -> onEditClicked()
                                    delete -> onDeleteClicked()
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun FolderItemPreview() {
    FolderItem(
        folder = Folder(title = "Testing", id = 0),
        isDropdownExpanded = true,
        onFolderClicked = {},
        onMoreClicked = {},
        onDismissDropdown = {},
        onEditClicked = {},
        onDeleteClicked = {}
    )
}