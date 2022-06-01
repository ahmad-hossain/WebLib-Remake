package com.github.godspeed010.weblib.feature_library.domain.use_case.novel

import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository

class AddNovel(
    private val repository: LibraryRepository
) {

    suspend operator fun invoke(novel: Novel) {
        repository.insertOrUpdateNovel(novel)
    }
}