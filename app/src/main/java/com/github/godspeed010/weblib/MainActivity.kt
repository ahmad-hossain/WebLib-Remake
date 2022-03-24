package com.github.godspeed010.weblib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import com.github.godspeed010.weblib.feature_library.presentation.NavGraphs
import com.github.godspeed010.weblib.ui.theme.WebLibTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebLibTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}