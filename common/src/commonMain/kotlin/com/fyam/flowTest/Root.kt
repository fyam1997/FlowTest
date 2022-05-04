package com.fyam.flowTest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.fyam.flowTest.components.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.*

@Composable
fun Root(
    modifier: Modifier = Modifier,
) = MaterialTheme(typography = Typography(defaultFontFamily = FontFamily.Monospace)) {
    val logger = rememberLoggerState()
    Column(modifier.padding(horizontal = 8.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                IconButton(
                    onClick = logger::clean,
                    content = {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Clean Logger",
                            tint = MaterialTheme.colors.primary,
                        )
                    },
                )
                Buttons(logger)
            }
        )
        LogBoard(
            modifier = Modifier.weight(1f),
            state = logger
        )
    }
}


@Composable
private fun Buttons(
    logger: LoggerState
) {
    JobButton(
        title = "Flow life cycle",
        log = logger::log,
        onClick = {
            flowOf(1, 2, 3, 4)
                .onCompletion {
                    logger.log("onCompletion")
                }
                .onEach {
                    logger.log("onEach-$it")
                }
                .onStart {
                    logger.log("onStart")
                }
                .collect {
                    logger.log("collect-$it")
                }
        }
    )
    JobButton(
        title = "flowOn",
        log = logger::log,
        onClick = {
            flowOf(1)
                // Context A
                .onEach {
                    logger.log("onEach-$it-${currentCoroutineName()}")
                }
                .onEach {
                    logger.log("onEach-$it-${currentCoroutineName()}")
                }
                .flowOn(CoroutineName("context A"))

                // Context B
                .onEach {
                    logger.log("onEach-$it-${currentCoroutineName()}")
                }
                .flowOn(CoroutineName("context B"))

                // Launch context
                .onEach {
                    logger.log("onEach-$it-${currentCoroutineName()}")
                }.collect {
                    logger.log("collect-$it-${currentCoroutineName()}")
                }
        }
    )
}
