package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.FolderItem
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveNovelModalBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
    isVisible: Boolean,
    folders: List<Folder>,
    onBottomSheetDismissed: () -> Unit,
    onFolderClicked: (Folder) -> Unit
) {
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            bottomSheetState.hide()
        }
    }
    if (!isVisible) return

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onBottomSheetDismissed,
        sheetState = bottomSheetState,
        content = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.move_novel),
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                textAlign = TextAlign.Center,
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            LazyColumn {
                items(folders) {
                    FolderItem(
                        containerColor = BottomSheetDefaults.ContainerColor,
                        folder = it,
                        isDropdownExpanded = false,
                        onFolderClicked = { onFolderClicked(it) },
                        isTrailingContentVisible = false
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewMoveNovelModalBottomSheet() {
    val sheetState = rememberModalBottomSheetState()
    runBlocking { sheetState.expand() }
    MoveNovelModalBottomSheet(
        isVisible = true,
        bottomSheetState = sheetState,
        folders = listOf(
            Folder(title = "folder 1"),
            Folder(title = "folder 2"),
            Folder(title = "Very very very very very very very very very very long folder name"),
        ),
        onBottomSheetDismissed = {},
        onFolderClicked = {}
    )
}