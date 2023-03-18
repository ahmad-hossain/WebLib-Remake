package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.common.use_case.ValidatedUrl
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NovelsViewModel @Inject constructor(
    private val libraryRepo: LibraryRepository,
    private val validatedUrlUseCase: ValidatedUrl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    @OptIn(ExperimentalComposeUiApi::class)
    val folder: Folder = savedStateHandle.navArgs<NovelsScreenNavArgs>().folder

    var state by mutableStateOf(NovelsState())
        private set
    private var deleteNovelJob: Job? = null

    fun onEvent(event: NovelsEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when (event) {
            is NovelsEvent.AddOrUpdateNovel -> {
                //Add Novel
                viewModelScope.launch(Dispatchers.IO) {
                    libraryRepo.insertOrUpdateNovel(
                        state.dialogNovel.copy(
                            folderId = folder.id,
                            url = validatedUrlUseCase(state.dialogNovel.url),
                            lastModified = TimeUtil.currentTimeSeconds()
                        )
                    )

                    //Clear TextField states
                    state = state.copy(
                        dialogNovel = Novel.createWithDefaults()
                    )
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
                    dialogNovel = Novel.createWithDefaults()
                )
            }
            is NovelsEvent.DeleteNovel -> {
                if (deleteNovelJob?.isActive == true) {
                    Timber.d("DeleteNovel: deleteNovelJob is active; cancelling..")
                    deleteNovelJob?.cancel()
                    val lastDeletedNovelId = state.hiddenNovelId
                    viewModelScope.launch(Dispatchers.IO) {
                        lastDeletedNovelId?.let {
                            libraryRepo.deleteNovel( Novel(id = lastDeletedNovelId, title = "", url = "", scrollProgression = 0f, folderId = 0) )
                            Timber.d("DeleteNovel: Deleted previous Novel with id: $it")
                        }
                    }
                }

                state = state.copy(
                    hiddenNovelId = event.novel.id,
                    expandedDropdownNovelListIndex = null
                )

                deleteNovelJob = viewModelScope.launch(Dispatchers.Main) {
                    val novelToDelete = event.novel
                    val snackbarResult = state.snackbarHostState.showSnackbar(
                        message = "Deleted Novel",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )

                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> {
                            withContext(Dispatchers.IO) {
                                libraryRepo.deleteNovel(novelToDelete)
                                Timber.d("DeleteNovel: Due to no Undo, deleted Novel with id: ${novelToDelete.id}")
                                state = state.copy(hiddenNovelId = null)
                            }
                        }
                        SnackbarResult.ActionPerformed -> state = state.copy(hiddenNovelId = null)
                    }
                }
            }
            is NovelsEvent.EditNovelClicked -> {
                //Set TextField state & close Dropdown
                state = state.copy(
                    dialogTitleRes = R.string.dialog_edit_novel,
                    dialogIcon = Icons.Outlined.DriveFileRenameOutline,
                    expandedDropdownNovelListIndex = null,
                    dialogNovel = Novel(
                        id = event.novel.id,
                        title = event.novel.title,
                        url = event.novel.url,
                        scrollProgression = event.novel.scrollProgression,
                        folderId = folder.id,
                        createdAt = event.novel.createdAt,
                    ),
                    isAddEditNovelDialogVisible = true
                )
            }
            is NovelsEvent.EnteredNovelTitle -> {
                //update the novelName State
                state = state.copy(
                    dialogNovel = state.dialogNovel.copy(title = event.novelTitle)
                )
            }
            is NovelsEvent.EnteredNovelUrl -> {
                //update the novelName State
                state = state.copy(
                    dialogNovel = state.dialogNovel.copy(url = event.novelUrl)
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
            is NovelsEvent.MoveNovel -> TODO()
        }
    }

    init {
        viewModelScope.launch {
            Timber.d("Opened Folder: ${folder.title}")

            state = state.copy(
                folderWithNovels = libraryRepo.getFolderWithNovels(folder.id)
            )
        }
    }
}