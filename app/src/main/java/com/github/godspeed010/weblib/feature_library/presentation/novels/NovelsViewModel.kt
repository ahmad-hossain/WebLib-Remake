package com.github.godspeed010.weblib.feature_library.presentation.novels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.use_case.novel.NovelUseCases
import com.github.godspeed010.weblib.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NovelsViewModel"

@HiltViewModel
class NovelsViewModel @Inject constructor(
    private val novelUseCases: NovelUseCases,
    state: SavedStateHandle
) : ViewModel() {
    @OptIn(ExperimentalComposeUiApi::class)
    val navArgs: NovelsScreenNavArgs = state.navArgs()

    private val _novelsScreenState = mutableStateOf(NovelsState())
    val novelsScreenState: State<NovelsState> = _novelsScreenState

    fun onEvent(event: NovelsEvent) {
        when (event) {
            is NovelsEvent.AddNovel -> {
                Log.i(TAG, "Add Novel")

                //Add Novel
                viewModelScope.launch(Dispatchers.IO) {
                    novelUseCases.addNovel(
                        Novel(
                            folderId = navArgs.folder.id,
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
                Log.i(TAG, "AddNovelClicked")

                //make AddEditNovelDialog visible
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogTitle = "Add Folder", //todo use String Resource
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
                Log.i(TAG, "DeleteNovel")

                viewModelScope.launch(Dispatchers.IO) {
                    novelUseCases.deleteNovel(event.novel)
                }
            }
            is NovelsEvent.EditNovelClicked -> {
                //Set TextField state & close Dropdown
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogTitle = "Edit Folder",
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
                Log.i(TAG, "EnteredNovelTitle")

                //update the novelName State
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogNovelTitle = event.novelTitle
                )
            }
            is NovelsEvent.EnteredNovelUrl -> {
                Log.i(TAG, "EnteredNovelUrl")

                //update the novelName State
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogNovelUrl = event.novelUrl
                )
            }
            is NovelsEvent.MoreOptionsClicked -> {
                Log.d(TAG, "More options clicked for Folder ${event.novelId}")

                //Expand Dropdown
                _novelsScreenState.value = novelsScreenState.value.copy(
                    expandedDropdownNovelId = event.novelId
                )
            }
            is NovelsEvent.MoreOptionsDismissed -> {
                Log.d(TAG,"More options dismissed for Novel ${novelsScreenState.value.expandedDropdownNovelId}")

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
            Log.i(TAG, "Opened Folder: ${navArgs.folder.title}")
            _novelsScreenState.value = novelsScreenState.value.copy(
                folderWithNovels = novelUseCases.getFolderWithNovels(navArgs.folder.id)
            )
        }
    }
}