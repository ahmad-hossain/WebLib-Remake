package com.github.godspeed010.weblib.feature_library.domain.use_case.folder

data class FolderUseCases(
    val addOrUpdateFolder: AddOrUpdateFolder,
    val getFolders: GetFolders,
    val deleteFolder: DeleteFolder
)