package com.fyam.flowTest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.fyam.flowTest.cases.CallbackFlows
import com.fyam.flowTest.cases.ChannelFlows
import com.fyam.flowTest.cases.FlowOperations
import com.fyam.flowTest.components.FlowRow
import com.fyam.flowTest.components.LogBoard
import com.fyam.flowTest.components.LoggerState
import com.fyam.flowTest.components.rememberLoggerState

@Composable
fun Root(
    modifier: Modifier = Modifier.fillMaxSize(),
    logger: LoggerState = rememberLoggerState(),
) = MaterialTheme(
    typography = MaterialTheme.typography.copy(defaultFontFamily = FontFamily.Monospace)
) {
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
                FlowOperations(logger = logger)
                CallbackFlows(logger = logger)
                ChannelFlows(logger = logger)
            }
        )
        LogBoard(
            modifier = Modifier.weight(1f),
            state = logger
        )
    }
}