package com.fyam.flowTest.cases

actual fun oldAssCall(onResult: (String) -> Unit) {
    val thread = Thread {
        Thread.sleep(2000)
        onResult("Hello")
    }
    thread.start()
    thread.join()
}
