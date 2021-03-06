package com.fyam.flowTest

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication

fun main() {
    val windowState = WindowState(
        width = 600.dp,
        height = 600.dp
    )
    singleWindowApplication(
        title = "ToyBox",
        state = windowState,
        icon = BitmapPainter(useResource("ic/ic_launcher.png", ::loadImageBitmap)),
    ) {
    }
}
