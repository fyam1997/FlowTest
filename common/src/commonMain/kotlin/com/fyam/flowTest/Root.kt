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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
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
        logger = logger,
        onClick = {
            flowOf("1", "2", "3", "4")
                .onCompletion {
                    emit("onCompletion-A")
                }
                .onEach {
                    logger.log("onEach-$it")
                }
                .onStart {
                    logger.log("onStart")
                }
                .onCompletion {
                    emit("onCompletion-B")
                }
                .collect {
                    logger.log("collect-$it")
                }
        }
    )
    JobButton(
        title = "flowOn",
        logger = logger,
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
    JobButton(
        title = "error",
        logger = logger,
        onClick = {
            flow<String> {
                throw Exception("Error")
            }.retryWhen { cause, attempt ->
                emit("Retry: ${cause.message}-$attempt")
                delay(100)
                attempt < 3
            }.onCompletion {
                logger.log("onComplete: ${it?.message}")
            }.catch {
                emit("Catch: ${it.message}")
            }.collect {
                logger.log(it)
            }
        }
    )
    JobButton(
        title = "error outside",
        logger = logger,
        onClick = {
            flowOf("1")
                .catch {
                    logger.log("Catch: ${it.message}")
                }.onCompletion {
                    logger.log("onComplete: ${it?.message}")
                }.collect {
                    throw Exception("Error in collect")
                }
        }
    )
    JobButton(
        title = "emit when complete",
        logger = logger,
        onClick = {
            flowOf("1")
                .catch {
                    logger.log("Catch: ${it.message}")
                }.onCompletion {
                    emit("emit onComplete: ${it?.message}")
                    logger.log("onComplete: ${it?.message}")
                }.collect {
                    throw Exception("Error in collect")
                }
        }
    )
    JobButton(
        title = "Not Cancellable",
        logger = logger,
        onClick = {
            // asFlow/flowOf will call unsafeFlow, otoh flow{} will call safeFlow
            (0..20).asFlow()
                .collect {
                    logger.log("Received: $it")
                    currentCoroutineContext().cancel()
                }
        }
    )
    JobButton(
        title = "Cancellable",
        logger = logger,
        onClick = {
            (0..20).asFlow()
                .cancellable()
                .collect {
                    logger.log("Received: $it")
                    currentCoroutineContext().cancel()
                }
        }
    )
}
