package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.core.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.core.model.Screen
import com.github.godspeed010.weblib.destinations.NovelsScreenDestination
import com.github.godspeed010.weblib.destinations.SearchScreenDestination
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.AddEditFolderDialog
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.FolderItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar({ Text(stringResource(R.string.folders)) })
        },
        bottomBar = {
            WebLibBottomAppBar(
                currentScreen = Screen.Home,
                onClickSearch = { navigator.navigate(SearchScreenDestination()) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(FoldersEvent.AddFolderClicked)
                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    "Add Folder"
                )
            }
        }
    ) { innerPadding ->
        if (state.isAddEditFolderDialogVisible) {
            AddEditFolderDialog(
                title = state.dialogTitle,
                folderName = state.dialogTextFieldText,
                onTextChange = { folderName ->
                    viewModel.onEvent(FoldersEvent.EnteredFolderName(folderName))
                },
                onDismissDialog = {
                    viewModel.onEvent(FoldersEvent.CancelFolderDialog)
                },
                onConfirmDialog = {
                    viewModel.onEvent(FoldersEvent.AddFolder)
                }
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = innerPadding.calculateBottomPadding())
        ) {
            items(folders) { folder ->
                FolderItem(
                    folder = folder,
                    expandedDropdownFolderId = state.expandedDropdownFolderId,
                    onFolderClicked = {
                        //Navigate to NovelsScreen & send clicked Folder
                        navigator.navigate(NovelsScreenDestination(folder))
                    },
                    onMoreClicked = {
                        viewModel.onEvent(FoldersEvent.MoreOptionsClicked(folder.id))
                    },
                    onDismissDropdown = {
                        viewModel.onEvent(FoldersEvent.MoreOptionsDismissed)
                    },
                    onEditClicked = {
                        viewModel.onEvent(FoldersEvent.EditFolderClicked(folder))
                    },
                    onDeleteClicked = {
                        viewModel.onEvent(FoldersEvent.DeleteFolder(folder))
                    }
                )
            }
        }
    }
}