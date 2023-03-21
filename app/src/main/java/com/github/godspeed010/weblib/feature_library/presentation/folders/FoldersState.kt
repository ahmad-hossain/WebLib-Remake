package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder

data class FoldersState(
    val folders: List<Folder> = emptyList(),
    val isAddEditFolderDialogVisible : Boolean = false,
    val dialogTitleRes: Int = R.string.dialog_add_folder,
    val dialogIcon: ImageVector = Icons.Outlined.CreateNewFolder,
    val dialogFolderTitle: TextFieldValue = TextFieldValue(),
    val expandedDropdownItemListIndex: Int? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val hiddenFolderId: Int? = null,
)