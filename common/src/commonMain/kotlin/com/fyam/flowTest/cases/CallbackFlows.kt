package com.fyam.flowTest.cases

import androidx.compose.runtime.Composable
import com.fyam.flowTest.components.JobButton
import com.fyam.flowTest.components.LoggerState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@Composable
fun CallbackFlows(
    logger: LoggerState,
) {
    JobButton(
        title = "CallbackFlow",
        logger = logger,
        onClick = {
            callbackFlow {
                oldAssCall {
                    trySend(it)
                    close()
                }
                awaitClose {
                    logger.log("close")
                }
            }.collect {
                logger.log("collect-$it")
            }
        }
    )
}

expect fun oldAssCall(onResult: (String) -> Unit)
