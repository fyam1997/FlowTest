package com.fyam.flowTest.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun LogBoard(
    modifier: Modifier = Modifier,
    state: LoggerState,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items(state.logs) { log ->
            LogItem(
                log = log,
                onClick = {
                    state.focus(log)
                },
                onHoverChanged = {
                    state.hover(log)
                },
            )
        }
    }
}

@Composable
private fun LazyItemScope.LogItem(
    modifier: Modifier = Modifier,
    log: Log,
    onClick: () -> Unit,
    onHoverChanged: (hovered: Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    if (hovered != log.hovering) onHoverChanged(hovered)

    val color = when {
        log.focusing || log.hovering -> MaterialTheme.colors.onPrimary
        else -> Color.Unspecified
    }
    val background = when {
        log.hovering -> MaterialTheme.colors.primary
        log.focusing -> MaterialTheme.colors.primary.copy(ContentAlpha.high)
        else -> Color.Unspecified
    }
    Row(
        modifier = modifier
            .fillParentMaxWidth()
            .hoverable(interactionSource)
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
fun TimeLine(
    modifier: Modifier = Modifier,
    logs: List<Log>
) {
    if (logs.isEmpty()) return
    val color = MaterialTheme.colors.primary
    Canvas(
        modifier
            .fillMaxSize()
            .border(ButtonDefaults.outlinedBorder, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .padding(4.dp)
            .clipToBounds()
    ) {
        val start = logs.first().timeMs
        val end = logs.last().timeMs

        val dotRadius = size.height / 4
        val indicatorRadius = dotRadius / 2
        val scale = (size.width - dotRadius * 2) / (end - start)

        val axisDot = size.height / 2
        val axisFocus = size.height / 8
        val axisHover = size.height * 7 / 8

        logs.forEach { log ->
            val x = (log.timeMs - start) * scale + dotRadius
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(x, axisDot)
            )
            if (log.focusing) {
                drawCircle(
                    color = color,
                    radius = indicatorRadius,
                    center = Offset(x, axisFocus)
                )
            }
            if (log.hovering) {
                drawCircle(
                    color = color,
                    radius = indicatorRadius,
                    center = Offset(x, axisHover)
                )
            }
        }
    }
}

@Composable
fun rememberLoggerState() = remember {
    LoggerState()
}

class LoggerState {
    private val _logs = mutableStateListOf<Log>()
    val logs: List<Log> get() = _logs

    fun log(text: Any?, tag: String = "default") {
        _logs += Log(tag, Clock.System.now(), text.toString())
    }

    fun hover(log: Log) {
        _logs[logIndex(log)] = log.copy(hovering = !log.hovering)
    }

    fun focus(log: Log) {
        _logs[logIndex(log)] = log.copy(focusing = !log.focusing)
    }

    fun newLine() {
        log("")
    }

    fun clean() {
        _logs.clear()
    }

    private fun logIndex(log: Log) = _logs.indexOfFirst { it.time == log.time }
}

data class Log(
    val tag: String,
    val time: Instant,
    val text: String,
    val focusing: Boolean = false,
    val hovering: Boolean = false,
) {
    val timeMs get() = time.toEpochMilliseconds()
}
