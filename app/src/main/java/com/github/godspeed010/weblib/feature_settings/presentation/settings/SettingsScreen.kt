package com.github.godspeed010.weblib.feature_settings.presentation.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state

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

                SettingsSectionHeadline(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(R.string.authentication)
                )

                if (state.isAuthed) {
                    // TODO Sign OUT
                } else {
                    GoogleSignInButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { viewModel.onEvent(SettingsEvent.SignInClicked) }
                    )
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

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
        }
    )
}