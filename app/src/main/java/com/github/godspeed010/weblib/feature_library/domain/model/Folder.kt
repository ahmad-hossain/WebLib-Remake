package com.github.godspeed010.weblib.feature_library.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    override val createdAt: Long = TimeUtil.currentTimeSeconds(),
    override val lastModified: Long = TimeUtil.currentTimeSeconds(),
) : Item(createdAt, lastModified) {

    companion object {
        fun createWithDefaults(
            title: String = "",
        ) = Folder(title = title)
    }
}
