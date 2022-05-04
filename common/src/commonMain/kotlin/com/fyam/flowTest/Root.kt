package com.fyam.flowTest

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fyam.flowTest.components.*
import kotlinx.coroutines.flow.*

@Composable
fun Root(
    modifier: Modifier = Modifier,
) {
    val logger = rememberLoggerState()
    LazyColumn(modifier) {
        item {
            Counter()
        }
        item {
            Button(onClick = logger::clean) { Text("Clean Log") }
            FlowRow(
                content = {
                    Buttons(logger)
                }
            )
        }
        item {
            Logger(state = logger)
        }
    }
}

@Composable
private fun Buttons(
    logger: LoggerState
) {
    JobButton(
        title = "Flow life cycle",
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
