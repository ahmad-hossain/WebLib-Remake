package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.FolderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val folderUseCases: FolderUseCases
) : ViewModel() {
    var state by mutableStateOf(FoldersState())
        private set

    fun onEvent(event: FoldersEvent) {
        when (event) {
            is FoldersEvent.AddOrUpdateFolder -> {
                Timber.d("AddFolder")

                viewModelScope.launch(Dispatchers.IO) {
                    //Add Folder. If id is 0, will generate new id. Else, overrides Folder
                    folderUseCases.addOrUpdateFolder(
                        Folder(
                            id = state.dialogFolderId,
                            title = state.dialogTextFieldText
                        )
                    )
                    //Clear TextField state & reset dialogFolderId
                    state = state.copy(
                        dialogTitle = "",
                        dialogTextFieldText = "",
                        dialogFolderId = 0
                    )
                }
                //Make AlertDialog for adding a Folder invisible
                state = state.copy(
                    isAddEditFolderDialogVisible = false
                )
            }
            is FoldersEvent.DeleteFolder -> {
                Timber.d("DeleteFolder")

                viewModelScope.launch(Dispatchers.IO) {
                    folderUseCases.deleteFolder(event.folder)
                }
            }
            is FoldersEvent.RestoreFolder -> TODO()
            is FoldersEvent.FabClicked -> {
                Timber.d("AddFolderClicked")

                //Make AlertDialog for adding a Folder visible
                state = state.copy(
                    isAddEditFolderDialogVisible = true,
                    dialogTitle = "Add Folder"
                )
            }
            is FoldersEvent.EditFolderClicked -> {
                //Set TextField state & close Dropdown
                state = state.copy(
                    expandedDropdownItemListIndex = null,
                    dialogFolderId = event.folder.id,
                    dialogTextFieldText = event.folder.title,
                    isAddEditFolderDialogVisible = true,
                    dialogTitle = "Edit Folder"
                )
            }
            is FoldersEvent.CancelFolderDialog -> {
                Timber.d("CancelFolderDialog")

                //Make AlertDialog for adding a Folder disappear & clear TextField State
                state = state.copy(
                    isAddEditFolderDialogVisible = false,
                    dialogTextFieldText = ""
                )
            }
            is FoldersEvent.EnteredFolderName -> {
                Timber.d("EnteredFolderName")

                //update the folderName State
                state = state.copy(
                    dialogTextFieldText = event.folderName
                )
            }
            is FoldersEvent.FolderClicked -> TODO()
            is FoldersEvent.MoreOptionsClicked -> {
                Timber.d("More options clicked for Item index ${event.listIndex}")

                //Expand Dropdown
                state = state.copy(
                    expandedDropdownItemListIndex = event.listIndex
                )
            }
            is FoldersEvent.MoreOptionsDismissed -> {
                Timber.d("More options dismissed for Item index ${state.expandedDropdownItemListIndex}")

                //Collapse Dropdown
                state = state.copy(
                    expandedDropdownItemListIndex = null
                )
            }
            is FoldersEvent.DeleteNovel -> TODO()
            is FoldersEvent.EditNovelClicked -> TODO()
            is FoldersEvent.MoveNovel -> TODO()
        }
    }

    init {
        state = state.copy(
            folders = folderUseCases.getFolders()
        )
    }
}