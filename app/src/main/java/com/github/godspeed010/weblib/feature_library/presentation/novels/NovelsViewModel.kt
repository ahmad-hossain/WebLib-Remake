package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    state: SavedStateHandle
) : ViewModel() {
    @OptIn(ExperimentalComposeUiApi::class)
    val folder: Folder = state.navArgs<NovelsScreenNavArgs>().folder

    private val _novelsScreenState = mutableStateOf(NovelsState())
    val novelsScreenState: State<NovelsState> = _novelsScreenState

    fun onEvent(event: NovelsEvent) {
        when (event) {
            is NovelsEvent.AddNovel -> {
                Timber.i("Add Novel")

                //Add Novel
                viewModelScope.launch(Dispatchers.IO) {
                    novelUseCases.addNovel(
                        Novel(
                            folderId = folder.id,
                            id = novelsScreenState.value.dialogNovelId,
                            title = novelsScreenState.value.dialogNovelTitle,
                            url = novelsScreenState.value.dialogNovelUrl
                        )
                    )

                    //Clear TextField states
                    _novelsScreenState.value = novelsScreenState.value.copy(
                        dialogTitle = "",
                        dialogNovelTitle = "",
                        dialogNovelUrl = "",
                        dialogNovelId = 0
                    )
                }
                //Make AddEditNovelDialog Gone
                _novelsScreenState.value = novelsScreenState.value.copy(
                    isAddEditNovelDialogVisible = false
                )
            }
            is NovelsEvent.AddNovelClicked -> {
                Timber.i("AddNovelClicked")

                //make AddEditNovelDialog visible
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogTitle = "Add Novel",
                    isAddEditNovelDialogVisible = true
                )
            }
            is NovelsEvent.CancelNovelDialog -> {
                //Make Dialog Gone
                _novelsScreenState.value = novelsScreenState.value.copy(
                    isAddEditNovelDialogVisible = false,
                    dialogNovelTitle = "",
                    dialogNovelUrl = ""
                )
            }
            is NovelsEvent.DeleteNovel -> {
                Timber.i("DeleteNovel")

                viewModelScope.launch(Dispatchers.IO) {
                    novelUseCases.deleteNovel(event.novel)
                }
            }
            is NovelsEvent.EditNovelClicked -> {
                //Set TextField state & close Dropdown
                _novelsScreenState.value = novelsScreenState.value.copy(
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
            is NovelsEvent.UpdateNovel -> TODO()
            is NovelsEvent.EnteredNovelTitle -> {
                Timber.i("EnteredNovelTitle")

                //update the novelName State
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogNovelTitle = event.novelTitle
                )
            }
            is NovelsEvent.EnteredNovelUrl -> {
                Timber.i("EnteredNovelUrl")

                //update the novelName State
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogNovelUrl = event.novelUrl
                )
            }
            is NovelsEvent.MoreOptionsClicked -> {
                Timber.d("More options clicked for Folder ${event.novelId}")

                //Expand Dropdown
                _novelsScreenState.value = novelsScreenState.value.copy(
                    expandedDropdownNovelId = event.novelId
                )
            }
            is NovelsEvent.MoreOptionsDismissed -> {
                Timber.d("More options dismissed for Novel ${novelsScreenState.value.expandedDropdownNovelId}")

                //Collapse Dropdown
                _novelsScreenState.value = novelsScreenState.value.copy(
                    expandedDropdownNovelId = null
                )
            }
        }
    }

    //todo should instead be getting novels using a Folder
    init {
        viewModelScope.launch {
            Timber.i("Opened Folder: ${folder.title}")
            _novelsScreenState.value = novelsScreenState.value.copy(
                folderWithNovels = novelUseCases.getFolderWithNovels(folder.id)
            )
        }
    }
}