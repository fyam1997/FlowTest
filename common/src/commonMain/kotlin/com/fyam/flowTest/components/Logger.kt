package com.fyam.flowTest.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock

@Composable
fun LogBoard(
    modifier: Modifier = Modifier,
    state: LoggerState
) = Text(
    text = state.logs,
    modifier = modifier
        .horizontalScroll(rememberScrollState())
        .verticalScroll(rememberScrollState())
)

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
