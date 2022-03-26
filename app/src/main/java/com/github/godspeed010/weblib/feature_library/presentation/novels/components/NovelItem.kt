package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

@Composable
fun NovelItem(
    novel: Novel,
    modifier: Modifier = Modifier,
    onNovelClicked: () -> Unit,
    onMoreClicked: () -> Unit
) {
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
fun NovelItemPreview() {
    NovelItem(
        Novel(title = "Testing", url = "", folderId = 0),
        onNovelClicked = {},
        onMoreClicked = {}
    )
}