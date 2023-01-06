package com.github.godspeed010.weblib.feature_webview.presentation.webview.components

import android.webkit.WebHistoryItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R

@OptIn(ExperimentalComposeUiApi::class)
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
                        DropdownMenuItem(onClick = { onClickHistoryItem(index) }) {
                            Text(
                                text = s.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        },
        title = {
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .onFocusChanged { if (it.isFocused) onUrlFocused() },
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
                    DropdownMenuItem(onClick = onDarkModeOptionClicked) {
                        val itemText: Int
                        val itemIconId: Int
                        if (isDarkModeEnabled) {
                            itemText = R.string.light_mode
                            itemIconId = R.drawable.circle_left_half_full
                        } else {
                            itemText = R.string.dark_mode
                            itemIconId = R.drawable.circle_right_half_full
                        }
                        Icon(painter = painterResource(itemIconId), contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(itemText))
                    }
                }
            }
        }
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