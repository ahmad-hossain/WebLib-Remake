package com.github.godspeed010.weblib.feature_settings.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val novelsUseWebsiteTitle: Boolean = false,
    val isWebViewAdblockEnabled: Boolean = false,
)