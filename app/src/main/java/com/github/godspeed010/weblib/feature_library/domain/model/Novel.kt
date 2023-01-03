package com.github.godspeed010.weblib.feature_library.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.godspeed010.weblib.feature_library.domain.util.TimeUtil
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("folderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )],
    indices = [
        Index(value = arrayOf("folderId")),
        Index(value = arrayOf("id"), unique = true)
    ]
)
data class Novel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val url: String,
    val scrollProgression: Float,
    val folderId: Int,
    override val createdAt: Long = TimeUtil.currentTimeSeconds(),
    override val lastModified: Long = TimeUtil.currentTimeSeconds(),
) : Item(createdAt, lastModified) {

    companion object {
        fun createWithDefaults(
            title: String = "",
            url: String = "",
            scrollProgression: Float = 0f,
            folderId: Int = 0,
        ) = Novel(title = title, url = url, scrollProgression = scrollProgression, folderId = folderId)
    }
}