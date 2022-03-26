package com.github.godspeed010.weblib.feature_library.domain.use_case.folder

import androidx.lifecycle.LiveData
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository

class GetFolders(
    private val repository: LibraryRepository
    ) {

    operator fun invoke(): LiveData<List<Folder>> {
        return repository.getFolders()
    }
}