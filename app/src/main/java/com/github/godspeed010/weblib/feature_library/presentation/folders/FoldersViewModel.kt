package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.domain.use_case.DeleteFolderUseCase
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val libraryRepo: LibraryRepository,
    private val deleteFolderUseCase: DeleteFolderUseCase,
) : ViewModel() {
    var state by mutableStateOf(FoldersState())
        private set
    private var dialogFolder: Folder = Folder.createWithDefaults()

    fun onEvent(event: FoldersEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when (event) {
            is FoldersEvent.FolderDialogConfirmed -> {
                viewModelScope.launch(Dispatchers.IO) {
                    //Add Folder. If id is 0, will generate new id. Else, overrides Folder
                    libraryRepo.insertOrUpdateFolder(
                        dialogFolder.copy(
                            title = state.dialogFolderTitle.text,
                            lastModified = TimeUtil.currentTimeSeconds()
                        )
                    )
                    //Clear TextField state & reset dialogFolderId
                    dialogFolder = Folder.createWithDefaults()
                    state = state.copy(dialogFolderTitle = TextFieldValue())
                }
                //Make AlertDialog for adding a Folder invisible
                state = state.copy(
                    isAddEditFolderDialogVisible = false
                )
            }
            is FoldersEvent.DeleteFolder -> {
                deleteFolderUseCase(
                    scope = viewModelScope,
                    folder = event.folder,
                    getState = { state },
                    updateState = { state = it }
                )
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
                    dialogFolderTitle = TextFieldValue(
                        event.folder.title,
                        selection = TextRange(event.folder.title.length)
                    ),
                    isAddEditFolderDialogVisible = true,
                    dialogTitleRes = R.string.dialog_edit_folder,
                    dialogIcon = Icons.Outlined.DriveFileRenameOutline,
                )
                dialogFolder = event.folder.copy()
            }
            is FoldersEvent.CancelFolderDialog -> {
                //Make AlertDialog for adding a Folder disappear & clear TextField State
                state = state.copy(
                    isAddEditFolderDialogVisible = false,
                    dialogFolderTitle = TextFieldValue(),
                )
                dialogFolder = Folder.createWithDefaults()
            }
            is FoldersEvent.EnteredFolderName -> {
                state = state.copy(dialogFolderTitle = event.folderName)
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
        libraryRepo.getFolders().onEach {
            state = state.copy(folders = it)
        }.launchIn(viewModelScope)
    }
}