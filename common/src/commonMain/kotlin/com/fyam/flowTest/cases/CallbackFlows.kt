package com.fyam.flowTest.cases

import androidx.compose.runtime.Composable
import com.fyam.flowTest.components.JobButton
import com.fyam.flowTest.components.logger.LoggerState
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
                val oldAss = OldAss()
                oldAss.retrieveSomething(
                    onResult = { result ->
                        trySend(result)
                        close()
                    }
                )
                awaitClose {
                    oldAss.unregister()
                    logger.log("close")
                }
            }.collect {
                logger.log("collect-$it")
            }
        }
    )
}

@Suppress("NO_ACTUAL_FOR_EXPECT") // IDE bug
expect class OldAss() {
    fun unregister()
    fun retrieveSomething(onResult: (String) -> Unit)
}
