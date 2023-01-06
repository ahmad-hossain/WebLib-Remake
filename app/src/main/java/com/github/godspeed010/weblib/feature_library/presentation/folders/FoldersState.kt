package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

data class FoldersState(
    val folders: LiveData<List<Folder>> = MutableLiveData(),
    val isAddEditFolderDialogVisible : Boolean = false,
    val dialogTitleRes: Int = R.string.dialog_add_folder,
    val dialogIcon: ImageVector = Icons.Outlined.CreateNewFolder,
    val dialogFolder: Folder = Folder.createWithDefaults(),
    val expandedDropdownItemListIndex: Int? = null,
)