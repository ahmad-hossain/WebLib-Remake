package com.github.godspeed010.weblib.feature_library.presentation.folders

import com.github.godspeed010.weblib.feature_library.domain.model.Folder

sealed class FoldersEvent {
    data class AddFolder(val folder: Folder) : FoldersEvent()
    data class DeleteFolder(val folder: Folder) : FoldersEvent()
    object RestoreFolder : FoldersEvent()
}
