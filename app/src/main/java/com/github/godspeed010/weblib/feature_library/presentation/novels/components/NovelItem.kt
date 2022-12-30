package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

@Composable
fun NovelItem(
    novel: Novel,
    modifier: Modifier = Modifier,
    isDropdownExpanded: Boolean,
    onNovelClicked: () -> Unit,
    onMoreClicked: () -> Unit,
    onDismissDropdown: () -> Unit,
    dropdownOptions: Array<String> = stringArrayResource(id = R.array.novel_dropdown_options),
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onMoveClicked: () -> Unit,
) {
    val move = stringResource(id = R.string.move)
    val edit = stringResource(id = R.string.edit)
    val delete = stringResource(id = R.string.delete)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onNovelClicked()
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.BookmarkBorder,
            contentDescription = "Novel"
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = novel.title
        )
        Box {
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = onDismissDropdown
            ) {
                dropdownOptions.forEach { s ->
                    DropdownMenuItem(onClick = {
                        when (s) {
                            move -> onMoveClicked()
                            edit -> onEditClicked()
                            delete -> onDeleteClicked()
                        }
                    }) {
                        Text(s)
                    }
                }
            }
            IconButton(onClick = onMoreClicked) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
        }
    }
}

@Preview
@Composable
fun NovelItemPreview() {
    NovelItem(
        Novel(title = "Testing", url = "", folderId = 0),
        isDropdownExpanded = true,
        onNovelClicked = {},
        onMoreClicked = {},
        onDismissDropdown = {},
        onEditClicked = {},
        onDeleteClicked = {},
        onMoveClicked = {}
    )
}