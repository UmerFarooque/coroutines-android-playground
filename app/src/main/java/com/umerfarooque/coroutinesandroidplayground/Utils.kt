package com.umerfarooque.coroutinesandroidplayground

fun longRunningTask(duration: Long = 500) {
    var now = System.currentTimeMillis()
    val next = now + duration
    while (now < next) {
        now = System.currentTimeMillis()
    }
}
