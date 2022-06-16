package com.fyam.flowTest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun LogBoard(
    modifier: Modifier = Modifier,
    state: LoggerState,
) {
    LazyColumn(modifier = modifier) {
        items(state.logs) { log ->
            LogItem(
                log = log,
                focused = state.focusing == log,
                onClick = {
                    state.focusing = log.takeIf { state.focusing != log }
                }
            )
        }
    }
}

@Composable
private fun LazyItemScope.LogItem(
    modifier: Modifier = Modifier,
    log: Log,
    focused: Boolean,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val isHovered by interaction.collectIsHoveredAsState()
    val highlight = isHovered || focused
    val color = if (highlight) MaterialTheme.colors.onPrimary else Color.Unspecified
    val background = if (highlight) MaterialTheme.colors.primary else Color.Unspecified
    Row(
        modifier = modifier
            .fillParentMaxWidth()
            .hoverable(interaction)
            .clickable(onClick = onClick)
            .clip(MaterialTheme.shapes.medium)
            .background(background)
            .padding(4.dp)
    ) {
        Text(color = color, text = "${log.time}: ")
        Text(color = color, text = log.text)
    }
}

@Composable
fun rememberLoggerState() = remember {
    LoggerState()
}

class LoggerState {
    private val _logs = mutableStateListOf<Log>()
    val logs: List<Log> get() = _logs
    var focusing by mutableStateOf<Log?>(null)

    infix fun log(text: Any?) {
        _logs += Log(Clock.System.now(), text.toString())
    }

    fun newLine() {
        log("")
    }

    fun clean() {
        _logs.clear()
    }
}

data class Log(
    val time: Instant,
    val text: String
)
