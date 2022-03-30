package com.fyam.flowTest.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach

@Composable
fun Counter() {
    val counter by remember {
        (0..Int.MAX_VALUE).asFlow().onEach { delay(100) }
    }.collectAsState(0)
    Text("Counter = $counter")
}
