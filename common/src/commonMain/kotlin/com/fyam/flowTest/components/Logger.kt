package com.fyam.flowTest.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
        items(state.logs) { logState ->
            val log by logState
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeLine(
    modifier: Modifier = Modifier,
    state: LoggerState,
) {
    if (state.logs.isEmpty()) return
    val start = state.logs.first().value.timeMs
    val end = state.logs.last().value.timeMs
    LazyVerticalGrid(
        // TODO better calculate largest tag
        cells = GridCells.Fixed(5),
        modifier = modifier
    ) {
        state.logsMap.forEach { (tag, logsOfTag) ->
            item({ GridItemSpan(1) }) {
                Text(tag)
            }
            item({ GridItemSpan(4) }) {
                TimeLine(
                    modifier = Modifier.height(32.dp),
                    logs = logsOfTag,
                    start = start,
                    end = end,
                    color = MaterialTheme.colors.primary,
                )
            }
        }
    }
}

@Composable
fun TimeLine(
    modifier: Modifier = Modifier,
    logs: List<State<Log>>,
    start: Long,
    end: Long,
    color: Color,
) = Canvas(
    modifier
        .fillMaxSize()
        .border(ButtonDefaults.outlinedBorder, MaterialTheme.shapes.medium)
        .clip(MaterialTheme.shapes.medium)
        .padding(4.dp)
        .clipToBounds()
) {
    val timeInterval = end - start
    val dotRadius = size.height / 4
    val indicatorRadius = dotRadius / 2
    val scale = (size.width - dotRadius * 2) / (timeInterval)

    val axisDot = size.height / 2
    val axisFocus = size.height / 8
    val axisHover = size.height * 7 / 8

    logs.forEach { logState ->
        val log by logState
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

@Composable
fun rememberLoggerState() = remember {
    LoggerState()
}

class LoggerState {
    private val _logs = mutableStateListOf<MutableState<Log>>()
    val logs: List<State<Log>> get() = _logs
    private val _logsMap = mutableStateMapOf<String, List<MutableState<Log>>>()
    val logsMap: Map<String, List<State<Log>>> get() = _logsMap

    fun log(text: Any?, tag: String = "default") {
        val log = mutableStateOf(
            Log(tag = tag, time = Clock.System.now(), text = text.toString())
        )
        _logs += log
        _logsMap[tag] = _logsMap.getOrElse(tag, ::emptyList) + log
    }

    fun hover(log: Log) {
        findLogState(log)?.value = log.copy(hovering = !log.hovering)
    }

    fun focus(log: Log) {
        findLogState(log)?.value = log.copy(focusing = !log.focusing)
    }

    fun newLine() {
        log("")
    }

    fun clean() {
        _logs.clear()
        _logsMap.clear()
    }

    private fun findLogState(log: Log) = _logs.find { it.value.time == log.time }

    override fun toString(): String {
        val mapString = logsMap.entries.joinToString("\n") { (tag, logsOfTag) ->
            "$tag : ${logsOfTag.joinToString(", ") { it.value.toString() }}"
        }
        return "${super.toString()}\n$mapString"
    }
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
