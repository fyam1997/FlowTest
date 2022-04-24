package com.fyam.flowTest

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fyam.flowTest.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.DurationUnit

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
            FlowRow(
                content = {
                    repeat(10) {

                        JobButton(
                            title = "Button-$it",
                            onDone = { time ->
                                logger log time.toString(DurationUnit.MILLISECONDS)
                            },
                            onClick = {
                            }
                        )
                    }
                }
            )
        }
        item {
            Logger(state = logger)
        }
    }
}