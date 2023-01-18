package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.use_case.folder.FolderUseCases
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val folderUseCases: FolderUseCases
) : ViewModel() {
    var state by mutableStateOf(FoldersState())
        private set

    fun onEvent(event: FoldersEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when (event) {
            is FoldersEvent.AddOrUpdateFolder -> {
                viewModelScope.launch(Dispatchers.IO) {
                    //Add Folder. If id is 0, will generate new id. Else, overrides Folder
                    folderUseCases.addOrUpdateFolder(
                        state.dialogFolder.copy(lastModified = TimeUtil.currentTimeSeconds())
                    )
                    //Clear TextField state & reset dialogFolderId
                    state = state.copy(
                        dialogFolder = Folder.createWithDefaults(),
                    )
                }
                //Make AlertDialog for adding a Folder invisible
                state = state.copy(
                    isAddEditFolderDialogVisible = false
                )
            }
            is FoldersEvent.DeleteFolder -> {
                state = state.copy(
                    hiddenFolderId = event.folder.id,
                    expandedDropdownItemListIndex = null
                )

                viewModelScope.launch(Dispatchers.Main) {
                    val snackbarResult = state.snackbarHostState.showSnackbar(
                        message = "Deleted Folder",
                        actionLabel = "Undo"
                    )

                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> {
                            withContext(Dispatchers.IO) {
                                folderUseCases.deleteFolder(event.folder)
                                state = state.copy(hiddenFolderId = null)
                            }
                        }
                        SnackbarResult.ActionPerformed -> state = state.copy(hiddenFolderId = null)
                    }
                }
            }
            is FoldersEvent.FabClicked -> {
                //Make AlertDialog for adding a Folder visible
                state = state.copy(
                    isAddEditFolderDialogVisible = true,
                    dialogTitleRes = R.string.dialog_add_folder,
                    dialogIcon = Icons.Outlined.CreateNewFolder,
                )
            }
            is FoldersEvent.EditFolderClicked -> {
                //Set TextField state & close Dropdown
                state = state.copy(
                    expandedDropdownItemListIndex = null,
                    dialogFolder = Folder(
                        id = event.folder.id,
                        title = event.folder.title,
                        createdAt = event.folder.createdAt,
                    ),
                    isAddEditFolderDialogVisible = true,
                    dialogTitleRes = R.string.dialog_edit_folder,
                    dialogIcon = Icons.Outlined.DriveFileRenameOutline,
                )
            }
            is FoldersEvent.CancelFolderDialog -> {
                //Make AlertDialog for adding a Folder disappear & clear TextField State
                state = state.copy(
                    isAddEditFolderDialogVisible = false,
                    dialogFolder = Folder.createWithDefaults(),
                )
            }
            is FoldersEvent.EnteredFolderName -> {
                //update the folderName State
                state = state.copy(
                    dialogFolder = state.dialogFolder.copy(title = event.folderName)
                )
            }
            is FoldersEvent.MoreOptionsClicked -> {
                //Expand Dropdown
                state = state.copy(
                    expandedDropdownItemListIndex = event.listIndex
                )
            }
            is FoldersEvent.MoreOptionsDismissed -> {
                //Collapse Dropdown
                state = state.copy(
                    expandedDropdownItemListIndex = null
                )
            }
        }
    }

    init {
        state = state.copy(
            folders = folderUseCases.getFolders()
        )
    }
}