package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    val state by viewModel.novelsScreenState
    val folderWithNovels = state.folderWithNovels.observeAsState(
        FolderWithNovel(
            Folder(title = ""),
            novels = emptyList()
        )
    )
    val novels = folderWithNovels.value.novels //todo this may not update. Double Check

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(NovelsEvent.AddNovelClicked)
                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    "Add Novel"
                )
            }
        }
    ) {
        //todo AddEditNovelDialog
        if (state.isAddEditNovelDialogVisible) {
            //todo use ENUM class with visibility states for changing title to Add or Edit
            AddEditNovelDialog(
                title = "Add Novel",
                novelTitle = state.dialogNovelTitle,
                novelUrl = state.dialogNovelUrl,
                onNovelTitleChanged = { viewModel.onEvent(NovelsEvent.EnteredNovelTitle(it)) },
                onNovelUrlChanged = { viewModel.onEvent(NovelsEvent.EnteredNovelUrl(it)) },
                onDismissDialog = { viewModel.onEvent(NovelsEvent.CancelNovelDialog) },
                onConfirmDialog = { viewModel.onEvent(NovelsEvent.AddNovel) }
            )
        }
        LazyColumn {
            items(novels) { novel ->
                NovelItem(
                    novel = novel,
                    onNovelClicked = {
                        //todo navigate to WebView
                    },
                    onMoreClicked = {
                        //todo create Dropdown Menu with Move, Edit, Delete options
                    }
                )
            }
        }
    }
}