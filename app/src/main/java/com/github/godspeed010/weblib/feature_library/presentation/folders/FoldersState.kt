package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

data class FoldersState(
    val folders: LiveData<List<Folder>> = MutableLiveData(),
    val isAddEditFolderDialogVisible : Boolean = false,
    val dialogTitle: String = "",
    val dialogFolder: Folder = Folder.createWithDefaults(),
    val expandedDropdownItemListIndex: Int? = null,
)