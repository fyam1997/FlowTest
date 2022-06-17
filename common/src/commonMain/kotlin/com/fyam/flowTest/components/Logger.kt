package com.fyam.flowTest.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
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
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is HoverInteraction.Enter -> onHoverChanged(true)
                is HoverInteraction.Exit -> onHoverChanged(false)
            }
        }
    }

    val color = if (log.focusing) MaterialTheme.colors.onPrimary else Color.Unspecified
    val background = if (log.focusing)
        MaterialTheme.colors.primary.copy(ContentAlpha.medium)
    else Color.Unspecified
    Row(
        modifier = modifier
            .fillParentMaxWidth()
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .run {
                when {
                    log.hovering -> border(
                        ButtonDefaults.outlinedBorder.copy(width = 4.dp),
                        MaterialTheme.shapes.medium
                    )
                    else -> this
                }
            }
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
    val strokeWidth = LocalDensity.current.run {
        1.dp.toPx()
    }
    Canvas(
        modifier
            .fillMaxSize()
            .border(ButtonDefaults.outlinedBorder, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .padding(4.dp)
            .clipToBounds()
    ) {
        val y = size.height / 2
        val indicatorRadius = size.height / 2 - strokeWidth * 2
        val dotRadius = size.height / 3
        val indicatorSize = size.height
        val drawRange = size.width - indicatorSize

        val start = logs.first().time.toEpochMilliseconds()
        val end = logs.last().time.toEpochMilliseconds()
        val scale = drawRange / (end - start)

        logs.forEach { log ->
            val x = (log.time.toEpochMilliseconds() - start) * scale + indicatorSize / 2
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(x, y)
            )
            if (log.focusing) {
                drawCircle(
                    color = color,
                    radius = indicatorRadius,
                    style = Stroke(strokeWidth),
                    center = Offset(x, y)
                )
            }
            if (log.hovering) {
                drawCircle(
                    color = color,
                    radius = indicatorRadius,
                    style = Stroke(strokeWidth),
                    center = Offset(x, y)
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
)
