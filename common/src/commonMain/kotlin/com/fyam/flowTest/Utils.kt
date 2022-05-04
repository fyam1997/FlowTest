package com.fyam.flowTest

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.currentCoroutineContext

suspend fun currentCoroutineName() = currentCoroutineContext()[CoroutineName]?.name.orEmpty()
