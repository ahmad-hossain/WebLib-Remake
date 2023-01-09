package com.github.godspeed010.weblib.feature_settings.presentation.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSectionHeadline(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier.padding(horizontal = 16.dp),
        text = text,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        color = MaterialTheme.colorScheme.primary,
    )
}
