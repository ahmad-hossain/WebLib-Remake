package com.github.godspeed010.weblib.feature_library.domain.use_case.folder

import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository

class AddFolder(
    private val repository: LibraryRepository
) {

    suspend operator fun invoke(folder: Folder) {
        repository.insertFolder(folder)
    }
}