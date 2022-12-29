package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.core.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.core.model.Screen
import com.github.godspeed010.weblib.destinations.WebViewScreenDestination
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.AddEditNovelDialog
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.NovelItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

data class NovelsScreenNavArgs(
    val folder: Folder
)

@ExperimentalComposeUiApi
@Destination(navArgsDelegate = NovelsScreenNavArgs::class)
@Composable
fun NovelsScreen(
    navigator: DestinationsNavigator,
    viewModel: NovelsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val folderWithNovels = state.folderWithNovels.observeAsState(
        FolderWithNovel(
            Folder(title = ""),
            novels = emptyList()
        )
    )
    val novels = folderWithNovels.value.novels

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar({ Text(stringResource(R.string.novels)) })
        },
        bottomBar = {
            WebLibBottomAppBar(
                currentScreen = Screen.Home,
                onClickHome = { navigator.popBackStack() },
                onClickSearch = { }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(NovelsEvent.FabClicked)
                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    "Add Novel"
                )
            }
        }
    ) { innerPadding ->
        if (state.isAddEditNovelDialogVisible) {
            AddEditNovelDialog(
                title = state.dialogTitle,
                novelTitle = state.dialogNovel.title,
                novelUrl = state.dialogNovel.url,
                onNovelTitleChanged = { viewModel.onEvent(NovelsEvent.EnteredNovelTitle(it)) },
                onNovelUrlChanged = { viewModel.onEvent(NovelsEvent.EnteredNovelUrl(it)) },
                onDismissDialog = { viewModel.onEvent(NovelsEvent.CancelNovelDialog) },
                onConfirmDialog = { viewModel.onEvent(NovelsEvent.AddOrUpdateNovel) }
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = innerPadding.calculateBottomPadding())
        ) {
            itemsIndexed(novels) { index, novel ->
                NovelItem(
                    novel = novel,
                    isDropdownExpanded =  (index == state.expandedDropdownNovelListIndex),
                    onNovelClicked = { navigator.navigate(WebViewScreenDestination(novel)) },
                    onMoreClicked = { viewModel.onEvent(NovelsEvent.MoreOptionsClicked(index)) },
                    onDismissDropdown = { viewModel.onEvent(NovelsEvent.MoreOptionsDismissed) },
                    onEditClicked = { viewModel.onEvent(NovelsEvent.EditNovelClicked(novel)) },
                    onDeleteClicked = { viewModel.onEvent(NovelsEvent.DeleteNovel(novel)) },
                    onMoveClicked = { viewModel.onEvent(NovelsEvent.MoveNovel(novel)) }
                )
            }
        }
    }
}