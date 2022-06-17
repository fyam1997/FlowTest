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
                focused = log in state.focusing,
                hovered = log in state.hovering,
                onClick = {
                    if (log in state.focusing)
                        state.focusing -= log
                    else state.focusing += log
                },
                onHoverChanged = { hovered ->
                    if (hovered)
                        state.hovering += log
                    else state.hovering -= log
                },
            )
        }
    }
}

@Composable
private fun LazyItemScope.LogItem(
    modifier: Modifier = Modifier,
    log: Log,
    focused: Boolean,
    hovered: Boolean,
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

    val color = if (focused) MaterialTheme.colors.onPrimary else Color.Unspecified
    val background = if (focused)
        MaterialTheme.colors.primary.copy(ContentAlpha.medium)
    else Color.Unspecified
    Row(
        modifier = modifier
            .fillParentMaxWidth()
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .run {
                when {
                    hovered -> border(
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
    state: LoggerState,
) {
    if (state.logs.isEmpty()) return
    val normalColor = MaterialTheme.colors.primary
    val hoverColor = MaterialTheme.colors.secondary
    val stroke = LocalDensity.current.run {
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
        val indicatorRadius = size.height / 2 - stroke * 2
        val dotRadius = size.height / 3
        val indicatorSize = size.height
        val drawRange = size.width - indicatorSize

        val start = state.logs.first().time.toEpochMilliseconds()
        val end = state.logs.last().time.toEpochMilliseconds()
        val scale = drawRange / (end - start)

        fun drawLog(log: Log, highlight: Boolean, color: Color) {
            val x = (log.time.toEpochMilliseconds() - start) * scale + indicatorSize / 2
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(x, y)
            )
            if (highlight) {
                drawCircle(
                    color = color,
                    radius = indicatorRadius,
                    style = Stroke(stroke),
                    center = Offset(x, y)
                )
            }
        }
        state.logs.forEach { log ->
            drawLog(log = log, highlight = log in state.focusing, color = normalColor)
        }
        state.hovering.forEach { log ->
            drawLog(log = log, highlight = true, color = hoverColor)
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
    var focusing = mutableStateListOf<Log>()
    var hovering = mutableStateListOf<Log>()

    infix fun log(text: Any?) {
        _logs += Log(Clock.System.now(), text.toString())
    }

    fun newLine() {
        log("")
    }

    fun clean() {
        _logs.clear()
        focusing.clear()
        hovering.clear()
    }
}

data class Log(
    val time: Instant,
    val text: String
)
