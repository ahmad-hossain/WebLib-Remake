package com.github.godspeed010.weblib.test_util.matcher

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.test_util.getString

object FoldersScreenMatchers {

    val addFolderFab: SemanticsMatcher
        get() = hasContentDescription(getString(R.string.cd_add_folder))

    fun folder(folder: Folder) = FolderItemMatcher(folder)
}