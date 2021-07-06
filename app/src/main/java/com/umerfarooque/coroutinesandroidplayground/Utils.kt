package com.umerfarooque.coroutinesandroidplayground

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
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

fun Fragment.toast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(@StringRes strRes: Int) {
    toast(getString(strRes))
}

/** Ensure invocation of this method on main thread. */
fun Job.showCompletionInView(
    scope: CoroutineScope,
    coroutineLayout: LayoutCoroutineBinding
) {
    invokeOnCompletion { throwable ->
        scope.launch(Dispatchers.Main.immediate) {
            coroutineLayout.btnPlay.isEnabled = true
            coroutineLayout.statusTv.setCompletionStatus(throwable)
        }
    }
}

// Hiding the play button and updating the label.
fun LayoutCoroutineBinding.showAsChildCoroutine() {
    btnPlay.visibility = View.GONE
    coroutineName.setText(R.string.child)
}

// Initial state for view. Mostly called when coroutine is launched.
fun updateJobStatus(scope: CoroutineScope, coroutineLayout: LayoutCoroutineBinding) {
    coroutineLayout.log.text = ""
    coroutineLayout.btnPlay.isEnabled = false
    coroutineLayout.statusTv.setJobStatus(scope)
}
