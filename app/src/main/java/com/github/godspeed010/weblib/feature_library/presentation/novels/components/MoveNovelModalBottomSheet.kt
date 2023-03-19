package com.github.godspeed010.weblib.feature_library.presentation.novels.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.FolderItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveNovelModalBottomSheet(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    folders: List<Folder>,
    onBottomSheetDismissed: () -> Unit,
    onFolderClicked: (Folder) -> Unit
) {
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(isVisible) {
        if (isVisible) {
            showBottomSheet = true
        }
        else {
            scope.launch {
                bottomSheetState.hide()
            }.invokeOnCompletion {
                showBottomSheet = false
            }
        }
    }
    if (!showBottomSheet) return

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
            Divider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            LazyColumn {
                items(folders) {
                    FolderItem(
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