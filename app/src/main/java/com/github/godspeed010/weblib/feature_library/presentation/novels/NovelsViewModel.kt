package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.common.use_case.ValidatedUrl
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.use_case.DeleteNovelUseCase
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NovelsViewModel @Inject constructor(
    private val libraryRepo: LibraryRepository,
    private val validatedUrlUseCase: ValidatedUrl,
    private val deleteNovelUseCase: DeleteNovelUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    @OptIn(ExperimentalComposeUiApi::class)
    val folder: Folder = savedStateHandle.navArgs<NovelsScreenNavArgs>().folder

    var state by mutableStateOf(NovelsState())
        private set
    private var dialogNovel: Novel = Novel.createWithDefaults()

    private var novelToMove: Novel? = null

    private val _toastMessageResId = MutableSharedFlow<Int>()
    val toastMessage = _toastMessageResId.asSharedFlow()

    fun onEvent(event: NovelsEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when (event) {
            is NovelsEvent.NovelDialogConfirmed -> {
                //Add Novel
                viewModelScope.launch(Dispatchers.IO) {
                    libraryRepo.insertOrUpdateNovel(
                        dialogNovel.copy(
                            folderId = folder.id,
                            title = state.dialogNovelTitle.text,
                            url = validatedUrlUseCase(state.dialogNovelUrl.text),
                            lastModified = TimeUtil.currentTimeSeconds()
                        )
                    )

                    //Clear TextField states
                    state = state.copy(
                        dialogNovelTitle = TextFieldValue(),
                        dialogNovelUrl = TextFieldValue(),
                    )
                    dialogNovel = Novel.createWithDefaults()
                }
                //Make AddEditNovelDialog Gone
                state = state.copy(
                    isAddEditNovelDialogVisible = false
                )
            }
            is NovelsEvent.FabClicked -> {
                //make AddEditNovelDialog visible
                state = state.copy(
                    dialogTitleRes = R.string.dialog_add_novel,
                    dialogIcon = Icons.Outlined.BookmarkAdd,
                    isAddEditNovelDialogVisible = true
                )
            }
            is NovelsEvent.CancelNovelDialog -> {
                //Make Dialog Gone
                state = state.copy(
                    isAddEditNovelDialogVisible = false,
                    dialogNovelTitle = TextFieldValue(),
                    dialogNovelUrl = TextFieldValue(),
                )
                dialogNovel = Novel.createWithDefaults()
            }
            is NovelsEvent.DeleteNovel -> {
                deleteNovelUseCase(
                    scope = viewModelScope,
                    novel = event.novel,
                    getState = { state },
                    updateState = { state = it }
                )
            }
            is NovelsEvent.EditNovelClicked -> {
                //Set TextField state & close Dropdown
                state = state.copy(
                    dialogTitleRes = R.string.dialog_edit_novel,
                    dialogIcon = Icons.Outlined.DriveFileRenameOutline,
                    expandedDropdownNovelListIndex = null,
                    dialogNovelTitle = TextFieldValue(
                        text = event.novel.title,
                        selection = TextRange(event.novel.title.length)
                    ),
                    dialogNovelUrl = TextFieldValue(
                        text = event.novel.url,
                        selection = TextRange(event.novel.url.length)
                    ),
                    isAddEditNovelDialogVisible = true
                )
                dialogNovel = event.novel.copy()
            }
            is NovelsEvent.EnteredNovelTitle -> {
                //update the novelName State
                state = state.copy(
                    dialogNovelTitle = event.novelTitle,
                )
            }
            is NovelsEvent.EnteredNovelUrl -> {
                //update the novelName State
                state = state.copy(
                    dialogNovelUrl = event.novelUrl,
                )
            }
            is NovelsEvent.MoreOptionsClicked -> {
                //Expand Dropdown
                state = state.copy(
                    expandedDropdownNovelListIndex = event.index
                )
            }
            is NovelsEvent.MoreOptionsDismissed -> {
                //Collapse Dropdown
                state = state.copy(
                    expandedDropdownNovelListIndex = null
                )
            }
            is NovelsEvent.MoveNovel -> {
                novelToMove = event.novel
                viewModelScope.launch(Dispatchers.IO) {
                    val folders = libraryRepo.getFolders()
                        .first()
                        .filterNot { it.id == folder.id }

                    if (folders.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            _toastMessageResId.emit(R.string.no_folders)
                        }
                        novelToMove = null
                        state = state.copy(expandedDropdownNovelListIndex = null)
                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        state = state.copy(
                            expandedDropdownNovelListIndex = null,
                            isBottomSheetVisible = true,
                            folders = folders
                        )
                    }
                }
            }
            is NovelsEvent.BottomSheetDismissed -> {
                state = state.copy(isBottomSheetVisible = false)
                novelToMove = null
            }
            is NovelsEvent.BottomSheetFolderClicked -> {
                state = state.copy(isBottomSheetVisible = false)
                novelToMove?.let { novel ->
                    viewModelScope.launch(Dispatchers.IO) {
                        libraryRepo.moveNovel(novel, event.folder)
                        novelToMove = null
                    }
                }
            }
        }
    }

    init {
        Timber.d("Opened Folder: ${folder.title}")
        libraryRepo.getFolderWithNovels(folder.id).onEach {
            state = state.copy(novels = it.novels)
        }.launchIn(viewModelScope)
    }
}