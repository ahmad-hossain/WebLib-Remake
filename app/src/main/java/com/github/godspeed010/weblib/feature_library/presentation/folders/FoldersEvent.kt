package com.github.godspeed010.weblib.feature_library.presentation.folders

import com.github.godspeed010.weblib.feature_library.domain.model.Folder

sealed class FoldersEvent {
    object FolderDialogConfirmed : FoldersEvent()
    data class DeleteFolder(val folder: Folder) : FoldersEvent()
    object FabClicked : FoldersEvent()
    data class EditFolderClicked(val folder: Folder) : FoldersEvent()
    object CancelFolderDialog : FoldersEvent()
    data class EnteredFolderName(val folderName: String) : FoldersEvent()
    data class MoreOptionsClicked(val listIndex: Int) : FoldersEvent()
    object MoreOptionsDismissed : FoldersEvent()
}