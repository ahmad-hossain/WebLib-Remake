package com.github.godspeed010.weblib.feature_settings.presentation.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.common.components.WebLibBottomAppBar
import com.github.godspeed010.weblib.common.model.Screen
import com.github.godspeed010.weblib.destinations.FoldersScreenDestination
import com.github.godspeed010.weblib.feature_settings.presentation.settings.components.GoogleSignInButton
import com.github.godspeed010.weblib.feature_settings.presentation.settings.components.SettingSwitchItem
import com.github.godspeed010.weblib.feature_settings.presentation.settings.components.SettingsSectionHeadline
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val oneTapResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            viewModel.onEvent(SettingsEvent.OneTapIntentResult(result))
        }

    val createDocumentActivityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.onEvent(SettingsEvent.OnCreateDocumentActivityResult(it))
        }

    val openDocumentActivityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.onEvent(SettingsEvent.OnOpenDocumentActivityResult(it))
        }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is SettingsUiEvent.Toast -> Toast.makeText(context, it.s, Toast.LENGTH_SHORT).show()
                is SettingsUiEvent.LaunchOneTapIntent -> oneTapResultLauncher.launch(it.intent)
                is SettingsUiEvent.LaunchCreateDocumentIntent -> createDocumentActivityResultLauncher.launch(it.intent)
                is SettingsUiEvent.LaunchOpenDocumentIntent -> openDocumentActivityResultLauncher.launch(it.intent)
            }
        }
    }

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
                LibrarySection()
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                LocalBackupSection()
            }
        }
    )
}

@Composable
fun ColumnScope.AuthSection(viewModel: SettingsViewModel = hiltViewModel()) {
    val state = viewModel.state

    SettingsSectionHeadline(
        modifier = Modifier.padding(bottom = 16.dp),
        text = stringResource(R.string.cloud_backup)
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
fun LibrarySection(viewModel: SettingsViewModel = hiltViewModel()) {
    val state = viewModel.state

    SettingsSectionHeadline(text = stringResource(R.string.library))

    SettingSwitchItem(
        text = stringResource(R.string.novels_use_website_title),
        isChecked = state.settings.novelsUseWebsiteTitle,
        onSwitchChecked = { viewModel.onEvent(SettingsEvent.ToggleNovelsUseWebsiteTitle(it)) }
    )
    SettingSwitchItem(
        text = stringResource(R.string.web_browser_adblock),
        isChecked = state.settings.isWebViewAdblockEnabled,
        onSwitchChecked = { viewModel.onEvent(SettingsEvent.ToggleWebViewAdblock(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalBackupSection(viewModel: SettingsViewModel = hiltViewModel()) {
    SettingsSectionHeadline(text = stringResource(R.string.local_backup))

    ListItem(
        modifier = Modifier.clickable { viewModel.onEvent(SettingsEvent.ExportDataClicked) },
        leadingContent = { Icon(Icons.Default.Upload, contentDescription = null) },
        headlineText = { Text(stringResource(R.string.headline_export_data)) },
        supportingText = { Text(stringResource(R.string.supporting_export_data)) },
    )
    ListItem(
        modifier = Modifier.clickable { viewModel.onEvent(SettingsEvent.ImportDataClicked) },
        leadingContent = { Icon(Icons.Default.Download, contentDescription = null) },
        headlineText = { Text(stringResource(R.string.headline_import_data)) },
        supportingText = { Text(stringResource(R.string.supporting_import_data)) },
    )
}