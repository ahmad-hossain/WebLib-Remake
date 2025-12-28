package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.common.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.common.model.Screen
import com.github.godspeed010.weblib.destinations.NovelsScreenDestination
import com.github.godspeed010.weblib.feature_library.common.Constants
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.AddEditFolderDialog
import com.github.godspeed010.weblib.feature_library.presentation.folders.components.FolderItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Destination
@RootNavGraph(start = true)
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun FoldersScreen(
    navigator: DestinationsNavigator,
    viewModel: FoldersViewModel = hiltViewModel()
) {
    val state = viewModel.state

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
        snackbarHost = { SnackbarHost(hostState = state.snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(FoldersEvent.FabClicked) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    stringResource(R.string.cd_add_folder),
                )
            }
        }
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + Constants.ListItemHeight
        )

        if (state.isAddEditFolderDialogVisible) {
            AddEditFolderDialog(
                icon = state.dialogIcon,
                title = stringResource(id = state.dialogTitleRes),
                folderName = state.dialogFolderTitle,
                onTextChange = { folderName ->
                    viewModel.onEvent(FoldersEvent.EnteredFolderName(folderName))
                },
                onDismissDialog = {
                    viewModel.onEvent(FoldersEvent.CancelFolderDialog)
                },
                onConfirmDialog = {
                    viewModel.onEvent(FoldersEvent.FolderDialogConfirmed)
                }
            )
        }
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(contentPadding),
            contentPadding = contentPadding
        ) {
            itemsIndexed(state.folders) { index, folder ->
                if (folder.id != state.hiddenFolderId) {
                    FolderItem(
                        modifier = Modifier.animateItem(),
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
}