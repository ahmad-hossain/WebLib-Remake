package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.common.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.common.model.Screen
import com.github.godspeed010.weblib.destinations.NovelsScreenDestination
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.AddEditFolderDialog
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.FolderItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Destination(start = true)
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun FoldersScreen(
    navigator: DestinationsNavigator,
    viewModel: FoldersViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val folders by state.folders.observeAsState(emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.folders)) })
        },
        bottomBar = {
            WebLibBottomAppBar(
                currentScreen = Screen.Home,
                onClickSettings = { }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(FoldersEvent.FabClicked)}) {
                Icon(
                    imageVector = Icons.Default.Add,
                    "Add Folder"
                )
            }
        }
    ) { innerPadding ->
        if (state.isAddEditFolderDialogVisible) {
            AddEditFolderDialog(
                icon = state.dialogIcon,
                title = stringResource(id = state.dialogTitleRes),
                folderName = state.dialogFolder.title,
                onTextChange = { folderName ->
                    viewModel.onEvent(FoldersEvent.EnteredFolderName(folderName))
                },
                onDismissDialog = {
                    viewModel.onEvent(FoldersEvent.CancelFolderDialog)
                },
                onConfirmDialog = {
                    viewModel.onEvent(FoldersEvent.AddOrUpdateFolder)
                }
            )
        }
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(innerPadding),
            contentPadding = innerPadding
        ) {
            itemsIndexed(folders) { index, folder ->
                FolderItem(
                    folder = folder,
                    isDropdownExpanded = (index == state.expandedDropdownItemListIndex),
                    onFolderClicked = { navigator.navigate(NovelsScreenDestination(folder)) },
                    onMoreClicked = { viewModel.onEvent(FoldersEvent.MoreOptionsClicked(index)) },
                    onDismissDropdown = { viewModel.onEvent(FoldersEvent.MoreOptionsDismissed) },
                    onEditClicked = { viewModel.onEvent(FoldersEvent.EditFolderClicked(folder)) },
                    onDeleteClicked = { viewModel.onEvent(FoldersEvent.DeleteFolder(folder)) }
                )
            }
        }
    }
}