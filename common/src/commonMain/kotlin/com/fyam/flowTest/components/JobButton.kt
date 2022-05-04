package com.fyam.flowTest.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@Composable
fun JobButton(
    modifier: Modifier = Modifier,
    title: String,
    scope: CoroutineScope = rememberCoroutineScope(),
    onDone: suspend (time: Duration) -> Unit = {},
    onClick: suspend CoroutineScope.() -> Unit,
) {
    var loading by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = title)
                AnimatedVisibility(visible = loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        },
        onClick = {
            scope.launch(
                context = CoroutineName("Launch scope [$title]") + Dispatchers.Default,
                block = {
                    loading = true
                    val time = measureTime {
                        onClick()
                    }
                    onDone(time)
                    loading = false
                }
            )
        }
    )
}
