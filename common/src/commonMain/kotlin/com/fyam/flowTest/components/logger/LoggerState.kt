package com.fyam.flowTest.components.logger

import androidx.compose.runtime.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun rememberLoggerState() = remember {
    LoggerState()
}

class LoggerState {
    private val _logs = mutableStateListOf<MutableState<Log>>()
    val logs: List<State<Log>> get() = _logs
    private val _logsMap = mutableStateMapOf<String, List<MutableState<Log>>>()
    val logsMap: Map<String, List<State<Log>>> get() = _logsMap

    fun log(text: Any?, tag: String = "default") {
        val log = mutableStateOf(
            Log(tag = tag, time = Clock.System.now(), text = text.toString())
        )
        _logs += log
        _logsMap[tag] = _logsMap.getOrElse(tag, ::emptyList) + log
    }

    fun hover(log: Log) {
        findLogState(log)?.value = log.copy(hovering = !log.hovering)
    }

    fun focus(log: Log) {
        findLogState(log)?.value = log.copy(focusing = !log.focusing)
    }

    fun newLine() {
        log("")
    }

    fun clean() {
        _logs.clear()
        _logsMap.clear()
    }

    private fun findLogState(log: Log) = _logs.find { it.value.time == log.time }

    override fun toString(): String {
        val mapString = logsMap.entries.joinToString("\n") { (tag, logsOfTag) ->
            "$tag : ${logsOfTag.joinToString(", ") { it.value.toString() }}"
        }
        return "${super.toString()}\n$mapString"
    }
}

data class Log(
    val tag: String,
    val time: Instant,
    val text: String,
    val focusing: Boolean = false,
    val hovering: Boolean = false,
) {
    val timeMs = time.toEpochMilliseconds()
    val timeText = with(time.toLocalDateTime(TimeZone.currentSystemDefault())) {
        listOf(hour, minute, second)
            .map { "$it".padStart(2, '0') }
            .plus("${nanosecond / 1_000_000}".padStart(3, '0'))
            .joinToString(":")
    }
}
