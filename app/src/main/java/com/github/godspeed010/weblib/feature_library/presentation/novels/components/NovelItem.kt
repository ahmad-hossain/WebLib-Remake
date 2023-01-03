package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

@OptIn(ExperimentalMaterialApi::class)
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

    ListItem(
        modifier = modifier
            .clickable { onNovelClicked() }
            .padding(vertical = 8.dp),
        text = { Text(text = novel.title) },
        icon = { Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Novel") },
        trailing = {
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
            }
        })
}

@Preview
@Composable
fun NovelItemPreview() {
    NovelItem(
        novel = Novel(title = "Testing", url = "", scrollProgression = 0f, folderId = 0),
        isDropdownExpanded = true,
        onNovelClicked = {},
        onMoreClicked = {},
        onDismissDropdown = {},
        onEditClicked = {},
        onDeleteClicked = {},
        onMoveClicked = {}
    )
}