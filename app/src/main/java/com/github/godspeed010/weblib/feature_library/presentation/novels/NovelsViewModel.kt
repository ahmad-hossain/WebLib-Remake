package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.use_case.novel.NovelUseCases
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NovelsViewModel @Inject constructor(
    private val novelUseCases: NovelUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    @OptIn(ExperimentalComposeUiApi::class)
    val folder: Folder = savedStateHandle.navArgs<NovelsScreenNavArgs>().folder

    var state by mutableStateOf(NovelsState())
        private set

    fun onEvent(event: NovelsEvent) {
        when (event) {
            is NovelsEvent.AddOrUpdateNovel -> {
                Timber.d("Add Novel")

                //Add Novel
                viewModelScope.launch(Dispatchers.IO) {
                    novelUseCases.addOrUpdateNovel(
                        Novel(
                            folderId = folder.id,
                            id = state.dialogNovelId,
                            title = state.dialogNovelTitle,
                            url = state.dialogNovelUrl
                        )
                    )

                    //Clear TextField states
                    state = state.copy(
                        dialogTitle = "",
                        dialogNovelTitle = "",
                        dialogNovelUrl = "",
                        dialogNovelId = 0
                    )
                }
                //Make AddEditNovelDialog Gone
                state = state.copy(
                    isAddEditNovelDialogVisible = false
                )
            }
            is NovelsEvent.FabClicked -> {
                Timber.d("AddNovelClicked")

                //make AddEditNovelDialog visible
                state = state.copy(
                    dialogTitle = "Add Novel",
                    isAddEditNovelDialogVisible = true
                )
            }
            is NovelsEvent.CancelNovelDialog -> {
                //Make Dialog Gone
                state = state.copy(
                    isAddEditNovelDialogVisible = false,
                    dialogNovelTitle = "",
                    dialogNovelUrl = ""
                )
            }
            is NovelsEvent.DeleteNovel -> {
                Timber.d("DeleteNovel")

                viewModelScope.launch(Dispatchers.IO) {
                    novelUseCases.deleteNovel(event.novel)
                }
            }
            is NovelsEvent.EditNovelClicked -> {
                //Set TextField state & close Dropdown
                state = state.copy(
                    dialogTitle = "Edit Novel",
                    expandedDropdownNovelId = null,
                    dialogNovelId = event.novel.id,
                    dialogNovelTitle = event.novel.title,
                    dialogNovelUrl = event.novel.url,
                    isAddEditNovelDialogVisible = true
                )
            }
            is NovelsEvent.NovelClicked -> TODO()
            is NovelsEvent.RestoreNovel -> TODO()
            is NovelsEvent.EnteredNovelTitle -> {
                Timber.d("EnteredNovelTitle")

                //update the novelName State
                state = state.copy(
                    dialogNovelTitle = event.novelTitle
                )
            }
            is NovelsEvent.EnteredNovelUrl -> {
                Timber.d("EnteredNovelUrl")

                //update the novelName State
                state = state.copy(
                    dialogNovelUrl = event.novelUrl
                )
            }
            is NovelsEvent.MoreOptionsClicked -> {
                Timber.d("More options clicked for Folder ${event.novelId}")

                //Expand Dropdown
                state = state.copy(
                    expandedDropdownNovelId = event.novelId
                )
            }
            is NovelsEvent.MoreOptionsDismissed -> {
                Timber.d("More options dismissed for Novel ${state.expandedDropdownNovelId}")

                //Collapse Dropdown
                state = state.copy(
                    expandedDropdownNovelId = null
                )
            }
            is NovelsEvent.MoveNovel -> TODO()
        }
    }

    //todo should instead be getting novels using a Folder
    init {
        viewModelScope.launch {
            Timber.d("Opened Folder: ${folder.title}")

            state = state.copy(
                folderWithNovels = novelUseCases.getFolderWithNovels(folder.id)
            )
        }
    }
}