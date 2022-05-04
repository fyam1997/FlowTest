package com.fyam.flowTest.components

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock

@Composable
fun Logger(
    modifier: Modifier = Modifier,
    state: LoggerState
) = Text(text = state.logs, modifier = modifier)

@Composable
fun rememberLoggerState() = remember {
    LoggerState()
}

class LoggerState {
    var logs: String by mutableStateOf("")
        private set

    infix fun log(text: Any?) {
        logs = "$logs\n${Clock.System.now()}: $text"
    }

    fun clean() {
        logs = ""
    }
}
