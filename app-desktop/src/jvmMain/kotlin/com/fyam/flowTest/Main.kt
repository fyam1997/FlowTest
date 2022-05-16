package com.fyam.flowTest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.fyam.flowTest.cases.CallbackFlows
import com.fyam.flowTest.components.rememberLoggerState

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application(exitProcessOnExit = true) {
    LaunchedEffect(0) {
        CallbackFlows.oldAssCall = ::oldAssCall
    }
    val windowState = WindowState(
        width = 800.dp,
        height = 600.dp
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
            Root(modifier = Modifier.fillMaxSize(), logger = logger)
        }
    )
}

fun oldAssCall(onResult: (String) -> Unit) {
    val thread = Thread {
        Thread.sleep(2000)
        onResult("Hello")
    }
    thread.start()
    thread.join()
}
