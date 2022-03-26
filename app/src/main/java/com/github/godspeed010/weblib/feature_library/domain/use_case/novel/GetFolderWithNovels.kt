package com.github.godspeed010.weblib.feature_library.domain.use_case.novel

import androidx.lifecycle.LiveData
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository

class GetFolderWithNovels(
    private val repository: LibraryRepository
    ) {

    suspend operator fun invoke(folderId: Int): LiveData<FolderWithNovel> {
        return repository.getFolderWithNovels(folderId)
    }
}