package com.github.godspeed010.weblib.feature_settings.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val isCloudBackupEnabled: Boolean = false,
    val novelsUseWebsiteTitle: Boolean = false,
)
