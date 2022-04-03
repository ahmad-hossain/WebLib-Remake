package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

data class FoldersState(
    val folders : LiveData<List<Folder>> = MutableLiveData(),
    val isAddFolderDialogVisible : Boolean = false,
    val dialogTextFieldText: String = "",
    val dialogFolderId: Int = 0,
    val expandedDropdownFolderId: Int? = null,
)