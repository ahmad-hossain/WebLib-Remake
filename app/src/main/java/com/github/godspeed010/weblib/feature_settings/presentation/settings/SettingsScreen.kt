package com.github.godspeed010.weblib.feature_settings.presentation.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.common.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.common.model.Screen
import com.github.godspeed010.weblib.destinations.FoldersScreenDestination
import com.github.godspeed010.weblib.feature_settings.presentation.settings.components.GoogleSignInButton
import com.github.godspeed010.weblib.feature_settings.presentation.settings.components.SettingItem
import com.github.godspeed010.weblib.feature_settings.presentation.settings.components.SettingsSectionHeadline
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings)) })
        },
        bottomBar = {
            WebLibBottomAppBar(
                currentScreen = Screen.Settings,
                onClickHome = { navigator.navigate(FoldersScreenDestination) },
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                AuthSection()
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                MiscSection()
            }
        }
    )
}

@Composable
fun ColumnScope.AuthSection(viewModel: SettingsViewModel = hiltViewModel()) {
    val state = viewModel.state

    SettingsSectionHeadline(
        modifier = Modifier.padding(bottom = 16.dp),
        text = stringResource(R.string.authentication)
    )

    if (state.isAuthed) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = state.authEmail
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = { viewModel.onEvent(SettingsEvent.SignOutClicked) }
        ) {
            Text(stringResource(R.string.sign_out))
        }
    } else {
        GoogleSignInButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = { viewModel.onEvent(SettingsEvent.SignInClicked) }
        )
    }
}

@Composable
fun MiscSection(viewModel: SettingsViewModel = hiltViewModel()) {
    val state = viewModel.state

    SettingsSectionHeadline(text = stringResource(R.string.misc))

    SettingItem(
        text = stringResource(R.string.auto_cloud_backup),
        isChecked = state.settings.isAutoCloudBackupEnabled,
        onSwitchChecked = { viewModel.onEvent(SettingsEvent.ToggleAutoCloudBackup(it)) }
    )
    SettingItem(
        text = stringResource(R.string.novels_use_website_title),
        isChecked = state.settings.novelsUseWebsiteTitle,
        onSwitchChecked = { viewModel.onEvent(SettingsEvent.ToggleNovelsUseWebsiteTitle(it)) }
    )

}