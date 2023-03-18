package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
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
import com.github.godspeed010.weblib.feature_library.domain.use_case.DeleteNovelUseCase
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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