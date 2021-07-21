package com.umerfarooque.coroutinesandroidplayground

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.databinding.LayoutCoroutineBinding
import kotlinx.coroutines.*

suspend fun longRunningTaskInBackground(duration: Long = 500) {
    withContext(Dispatchers.Default) {
        longRunningTask(duration)
    }
}

fun longRunningTask(duration: Long = 500) {
    var now = System.currentTimeMillis()
    val next = now + duration
    while (now < next) {
        now = System.currentTimeMillis()
    }
}

// Call to update the view when coroutine is launched.
fun updateJobState(scope: CoroutineScope, coroutineLayout: LayoutCoroutineBinding) {
    coroutineLayout.log.text = ""
    coroutineLayout.btnPlay.isEnabled = false
    coroutineLayout.stateTv.setJobState(scope)
}

const val TAG = "C_A_P"

fun log(message: String) {
    Log.d(TAG, message)
}

// Scope utils

const val NEW = 0
const val ACTIVE = 1
const val COMPLETING = 2
const val CANCELLING = 3
const val CANCELLED = 4
const val COMPLETED = 5

@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
@IntDef(NEW, ACTIVE, COMPLETING, CANCELLING, CANCELLED, COMPLETED)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class State

fun Job.getState(): @State Int {
    return when {
        isCancelled && !isCompleted -> CANCELLING
        isActive -> ACTIVE
        isCancelled -> CANCELLED
        isCompleted -> COMPLETED
        else -> NEW
    }
}

// Extension functions

fun Fragment.toast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(@StringRes strRes: Int) {
    toast(getString(strRes))
}

fun Job.showCompletionInView(scope: CoroutineScope, coroutineLayout: LayoutCoroutineBinding) {
    invokeOnCompletion { throwable ->
        scope.launch(Dispatchers.Main.immediate) {
            coroutineLayout.btnPlay.isEnabled = true
            coroutineLayout.stateTv.setCompletionState(throwable)
        }
    }
}

// Hiding the play button and updating the label.
fun LayoutCoroutineBinding.showAsChildCoroutine() {
    btnPlay.visibility = View.GONE
    coroutineName.setText(R.string.child)
}

inline fun Fragment.runExample(
    layout: LayoutCoroutineBinding,
    crossinline block: suspend (LayoutCoroutineBinding) -> Unit,
) {
    layout.log.text = ""
    lifecycleScope.launch {
        updateJobState(this, layout)
        block(layout)
    }.showCompletionInView(lifecycleScope, layout)
}
