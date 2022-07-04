package com.fyam.flowTest

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.fyam.flowTest.components.rememberLoggerState

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application(exitProcessOnExit = true) {
    val windowState = WindowState(
        width = 800.dp,
        height = 1200.dp
    )
    val logger = rememberLoggerState()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ToyBox",
        state = windowState,
        icon = BitmapPainter(useResource("ic/ic_launcher.png", ::loadImageBitmap)),
        onKeyEvent = { event ->
            when {
                event.isMetaPressed && event.key == Key.R -> {
                    logger.clean()
                    true
                }
                event.isMetaPressed && event.key == Key.W -> {
                    exitApplication()
                    true
                }
                else -> false
            }
        },
        content = {
            Root(logger = logger)
        }
    )
}
