package com.github.godspeed010.weblib.feature_webview.presentation.webview.components

import android.webkit.WebHistoryItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R

val SmallTopAppBarHeight = 64.dp
private val OutlinedTextFieldHeight = 20.dp
private val AddressBarHorizPadding = 12.dp

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WebViewTopAppBar(
    modifier: Modifier = Modifier,
    url: TextFieldValue,
    onUrlEntered: (TextFieldValue) -> Unit,
    onUrlSubmitted: () -> Unit,
    onBackButtonClicked: () -> Unit,
    onBackButtonLongClicked: () -> Unit,
    isWebViewLoading: Boolean,
    onRefreshClicked: () -> Unit,
    onStopLoadingClicked: () -> Unit,
    onMoreOptionsClicked: () -> Unit,
    isMoreOptionsDropdownEnabled: Boolean,
    onMoreOptionsDropdownDismissRequest: () -> Unit,
    onDarkModeOptionClicked: () -> Unit,
    isDarkModeEnabled: Boolean,
    onUrlFocused: () -> Unit,
    historyItems: List<WebHistoryItem>,
    isHistoryDropdownExpanded: Boolean,
    onHistoryDropdownDismissRequest: () -> Unit,
    onClickHistoryItem: (Int) -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            Box {
                CustomIconButton(
                    onLongClick = onBackButtonLongClicked,
                    onClick = onBackButtonClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.cd_go_back)
                    )
                }
                DropdownMenu(
                    expanded = isHistoryDropdownExpanded,
                    onDismissRequest = onHistoryDropdownDismissRequest,
                ) {
                    historyItems.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = s.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            onClick = { onClickHistoryItem(index) }
                        )
                    }
                }
            }
        },
        title = {
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current

            Box(
                modifier = Modifier
                    .height(SmallTopAppBarHeight)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.CenterStart
            ) {
                var isAddressBarFocused by rememberSaveable { mutableStateOf(false) }
                val shouldShowAddressBarHint by remember(isAddressBarFocused) {
                    derivedStateOf { url.text.isEmpty() && !isAddressBarFocused }
                }
                if (shouldShowAddressBarHint) {
                    AddressBarHint()
                }
                AddressBarTextField(
                    isAddressBarFocused = isAddressBarFocused,
                    onFocusChanged = { focused ->
                        isAddressBarFocused = focused
                        if (focused) onUrlFocused()
                    },
                    url = url,
                    onUrlEntered = onUrlEntered,
                    keyboardController = keyboardController,
                    focusManager = focusManager,
                    onUrlSubmitted = onUrlSubmitted
                )
            }
        },
        actions = {
            val iconButtonProperties = if (isWebViewLoading)
                Triple(onStopLoadingClicked, Icons.Default.Close, R.string.cd_stop_loading_page) else
                Triple(onRefreshClicked, Icons.Default.Refresh, R.string.cd_refresh_page)

            IconButton(onClick = iconButtonProperties.first) {
                Icon(imageVector = iconButtonProperties.second, contentDescription = stringResource(id = iconButtonProperties.third))
            }
            Box {
                IconButton(onClick = onMoreOptionsClicked) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.cd_more_options))
                }
                DropdownMenu(
                    expanded = isMoreOptionsDropdownEnabled,
                    onDismissRequest = onMoreOptionsDropdownDismissRequest) {
                    val uiModeDropdownItemData = if (isDarkModeEnabled)
                        Pair(Icons.Filled.LightMode, R.string.light_mode) else
                        Pair(Icons.Filled.DarkMode, R.string.dark_mode)

                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = uiModeDropdownItemData.first,
                                contentDescription = null
                            )
                        },
                        text = { Text(stringResource(uiModeDropdownItemData.second)) },
                        onClick = onDarkModeOptionClicked
                    )
                }
            }
        }
    )
}

@Composable
fun AddressBarHint() {
    Text(
        modifier = Modifier.padding(start = AddressBarHorizPadding),
        text = stringResource(R.string.hint_address_bar),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BoxScope.AddressBarTextField(
    isAddressBarFocused: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    url: TextFieldValue,
    onUrlEntered: (TextFieldValue) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onUrlSubmitted: () -> Unit
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AddressBarHorizPadding)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = TextStyle.Default.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
        ),
        value = url,
        onValueChange = onUrlEntered,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onUrlSubmitted()
            }
        ),
    )

    when (isAddressBarFocused) {
        true -> AddressBarEndPadding()
        false -> AddressBarEndGradient()
    }
}

@Composable
private fun BoxScope.AddressBarEndPadding() {
    Spacer(
        Modifier.Companion
            .align(Alignment.CenterEnd)
            .height(OutlinedTextFieldHeight)
            .width(AddressBarHorizPadding)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
private fun BoxScope.AddressBarEndGradient() {
    val (startX, endX) = with(LocalDensity.current) {
        Pair(0.dp.toPx(), 8.dp.toPx())
    }
    Spacer(
        Modifier.Companion
            .align(Alignment.CenterEnd)
            .height(OutlinedTextFieldHeight)
            .width(AddressBarHorizPadding + 8.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent, MaterialTheme.colorScheme.surfaceVariant
                    ), startX = startX, endX = endX
                )
            )
    )
}

@Preview
@Composable
fun PreviewWebViewTopAppBar() {
    WebViewTopAppBar(
        Modifier,
        url = TextFieldValue(text = "Hello there"),
        onUrlEntered = {},
        onUrlSubmitted = {},
        onBackButtonClicked = {},
        onBackButtonLongClicked = {},
        isWebViewLoading = true,
        onRefreshClicked = {},
        onStopLoadingClicked = {},
        onMoreOptionsClicked = {},
        isMoreOptionsDropdownEnabled = true,
        onMoreOptionsDropdownDismissRequest = {},
        onDarkModeOptionClicked = {},
        isDarkModeEnabled = true,
        onUrlFocused = {},
        historyItems = listOf(),
        isHistoryDropdownExpanded = false,
        onHistoryDropdownDismissRequest = {},
        onClickHistoryItem = {},
    )
}