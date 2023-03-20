package com.github.godspeed010.weblib.feature_settings.presentation.settings.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingSwitchItem(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onSwitchChecked: (Boolean) -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text) },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onSwitchChecked
            )
        }
    )
}
