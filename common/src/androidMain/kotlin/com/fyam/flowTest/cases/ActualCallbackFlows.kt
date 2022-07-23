package com.fyam.flowTest.cases

actual class OldAss {
    private val thread = Thread {
        Thread.sleep(500)
        callback?.invoke("Hello")
    }
    private var callback: ((String) -> Unit)? = null

    actual fun unregister() {
        callback = null
        thread.join()
    }

    actual fun retrieveSomething(onResult: (String) -> Unit) {
        callback = onResult
        thread.start()
    }
}
