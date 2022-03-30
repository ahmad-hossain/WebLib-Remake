package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.core.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.core.model.Screen
import com.github.godspeed010.weblib.feature_library.presentation.destinations.NovelsScreenDestination
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
    val state by viewModel.foldersScreenState
    val folders by state.folders.observeAsState(emptyList())
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Folders") }
            )
        },
        bottomBar = {
            WebLibBottomAppBar(
                currentScreen = Screen.Home,
                onClickAccount = {
                    //todo
                }
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
    ) {
        if (state.isAddFolderDialogVisible) {
            AddEditFolderDialog(
                title = "Add Folder",
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 56.dp)
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
                    onDismissDropdown = { viewModel.onEvent(FoldersEvent.MoreOptionsDismissed) }
                )
            }
        }
    }
}