package com.fyam.flowTest.components.logger

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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
                Text(tag, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
