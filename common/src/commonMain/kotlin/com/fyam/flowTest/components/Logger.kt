package com.fyam.flowTest.components

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

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

    infix fun log(text: String) {
        logs = "$text\n$logs"
    }

    fun clean() {
        logs = ""
    }
}
