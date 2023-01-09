package com.github.godspeed010.weblib.feature_settings.presentation.settings.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onSwitchChecked: (Boolean) -> Unit
) {
    ListItem(
        headlineText = { Text(text) },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onSwitchChecked
            )
        }
    )
}
