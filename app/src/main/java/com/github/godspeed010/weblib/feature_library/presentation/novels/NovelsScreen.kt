package com.github.godspeed010.weblib.feature_library.presentation.novels

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.common.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.common.model.Screen
import com.github.godspeed010.weblib.destinations.WebViewScreenDestination
import com.github.godspeed010.weblib.feature_library.common.Constants
import com.github.godspeed010.weblib.feature_library.common.use_case.ValidatedUrl
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.use_case.DeleteNovelUseCase
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.AddEditNovelDialog
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.MoveNovelModalBottomSheet
import com.github.godspeed010.weblib.feature_library.presentation.novels.components.NovelItem
import com.github.godspeed010.weblib.navArgs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.flow.emptyFlow
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

data class NovelsScreenNavArgs(
    val folder: Folder
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@ExperimentalComposeUiApi
@Destination(navArgsDelegate = NovelsScreenNavArgs::class)
@Composable
fun NovelsScreen(
    navigator: DestinationsNavigator,
    viewModel: NovelsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect {
            Toast.makeText(context, context.getString(it), Toast.LENGTH_SHORT).show()
        }
    }
    val state = viewModel.state

    MoveNovelModalBottomSheet(
        isVisible = state.isBottomSheetVisible,
        folders = state.folders,
        onBottomSheetDismissed = { viewModel.onEvent(NovelsEvent.BottomSheetDismissed) },
        onFolderClicked = { viewModel.onEvent(NovelsEvent.BottomSheetFolderClicked(it)) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.novels)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_go_back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            WebLibBottomAppBar(
                currentScreen = Screen.Home,
                onClickHome = { navigator.popBackStack() },
                onClickSettings = { }
            )
        }, snackbarHost = { SnackbarHost(hostState = state.snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End,
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
        val contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + Constants.ListItemHeight
        )

        if (state.isAddEditNovelDialogVisible) {
            AddEditNovelDialog(
                icon = state.dialogIcon,
                title = stringResource(id = state.dialogTitleRes),
                novelTitle = state.dialogNovelTitle,
                novelUrl = state.dialogNovelUrl,
                onNovelTitleChanged = { viewModel.onEvent(NovelsEvent.EnteredNovelTitle(it)) },
                onNovelUrlChanged = { viewModel.onEvent(NovelsEvent.EnteredNovelUrl(it)) },
                onDismissDialog = { viewModel.onEvent(NovelsEvent.CancelNovelDialog) },
                onConfirmDialog = { viewModel.onEvent(NovelsEvent.NovelDialogConfirmed) }
            )
        }
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(contentPadding),
            contentPadding = contentPadding
        ) {
            itemsIndexed(state.novels) { index, novel ->
                if (novel.id != state.hiddenNovelId) {
                    NovelItem(
                        novel = novel,
                        isDropdownExpanded = (index == state.expandedDropdownNovelListIndex),
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
}