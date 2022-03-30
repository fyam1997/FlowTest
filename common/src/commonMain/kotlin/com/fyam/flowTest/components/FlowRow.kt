package com.fyam.flowTest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    rowVerticalAlignment: Alignment.Vertical = Alignment.Top,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) = Layout(
    modifier = modifier,
    content = content,
    measurePolicy = { measurables, constraints ->
        val childConstrains = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(childConstrains) }
        val horizontalSpacePx = horizontalArrangement.spacing.toPx().toInt()
        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight

        val rows = mutableListOf(mutableListOf<Placeable>())
        placeables.foldIndexed(0) { i, occupiedWidth, placeable ->
            rows.last() += placeable
            val newWidth = occupiedWidth + placeables[i].width + horizontalSpacePx
            if (i != placeables.lastIndex && newWidth + placeables[i + 1].width > maxWidth) {
                rows += mutableListOf<Placeable>()
                0
            } else newWidth
        }

        val heights = rows.map { row -> row.maxOfOrNull { it.height } ?: 0 }.toIntArray()
        val widths = rows.map { row -> row.map { it.width }.toIntArray() }

        val totalVerticalSpacing = verticalArrangement.spacing.toPx() * (heights.size - 1)
        val totalHeight = (heights.sum() + totalVerticalSpacing.toInt())
            .coerceIn(constraints.minHeight, maxHeight)
        val totalWidth = widths.maxOf {
            it.sum() + horizontalSpacePx * (it.size - 1)
        }.coerceIn(constraints.minWidth, maxWidth)

        layout(width = totalWidth, height = totalHeight) {
            val rowPositions = IntArray(heights.size).also {
                with(verticalArrangement) {
                    arrange(
                        totalSize = maxHeight,
                        sizes = heights,
                        outPositions = it
                    )
                }
            }
            rows.forEachIndexed { rowIndex, row ->
                val colPositions = IntArray(row.size).also {
                    with(horizontalArrangement) {
                        arrange(
                            totalSize = maxWidth,
                            sizes = widths[rowIndex],
                            layoutDirection = layoutDirection,
                            outPositions = it
                        )
                    }
                }
                row.forEachIndexed { colIndex, col ->
                    val offset = rowVerticalAlignment.align(col.height, heights[rowIndex])
                    col.placeRelative(colPositions[colIndex], rowPositions[rowIndex] + offset)
                }
            }
        }
    }
)
