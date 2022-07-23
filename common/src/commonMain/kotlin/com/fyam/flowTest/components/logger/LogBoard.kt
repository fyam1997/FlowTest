package com.fyam.flowTest.components.logger

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

@Composable
fun LogBoard(
    modifier: Modifier = Modifier,
    state: LoggerState,
) = BoxWithConstraints(modifier) {
    var largestCellWidth by remember { mutableStateOf(0) }
    LazyColumn(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(state.logs) { logState ->
            val log by logState
            LogItem(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val cellWidth = measurable.measure(constraints).width
                        if (cellWidth > largestCellWidth) largestCellWidth = cellWidth
                        val cellMinWidth = maxOf(maxWidth.roundToPx(), largestCellWidth)
                        val placeable = measurable.measure(
                            constraints.copy(minWidth = cellMinWidth)
                        )
                        layout(cellMinWidth, placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    },
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
private fun LogItem(
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
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .clip(MaterialTheme.shapes.medium)
            .background(background)
            .padding(4.dp)
    ) {
        Text(color = color, text = "${log.timeText}: ")
        Text(color = color, text = log.text)
    }
}
