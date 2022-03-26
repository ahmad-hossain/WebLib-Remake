package com.github.godspeed010.weblib.feature_library.domain.use_case.novel

import androidx.lifecycle.LiveData
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository

class GetNovels(
    private val repository: LibraryRepository
) {

    operator fun invoke(): LiveData<List<Novel>> {
        return repository.getNovels()
    }
}
