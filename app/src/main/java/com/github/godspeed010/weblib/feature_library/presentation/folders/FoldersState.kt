package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Item

data class FoldersState(
    val folders: LiveData<List<Folder>> = MutableLiveData(),
    val filteredItems: List<Item> = emptyList(),
    val searchQuery: String = "",
    val isSearchSectionVisible: Boolean = false,
    val isAddEditFolderDialogVisible : Boolean = false,
    val dialogTitle: String = "",
    val dialogTextFieldText: String = "",
    val dialogFolderId: Int = 0,
    val expandedDropdownItemListIndex: Int? = null,
    val modalDrawerState: DrawerState = DrawerState(DrawerValue.Closed)
)