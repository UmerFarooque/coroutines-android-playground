package com.umerfarooque.coroutinesandroidplayground

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.umerfarooque.coroutinesandroidplayground.databinding.LayoutCoroutineBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun longRunningTaskSuspend(duration: Long = 500) {
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

fun Fragment.toast(@StringRes strRes: Int) {
    Toast.makeText(context, getString(strRes), Toast.LENGTH_SHORT).show()
}

fun LayoutCoroutineBinding.showAsChildCoroutine() {
    btnPlay.visibility = View.GONE
    coroutineName.setText(R.string.child)
}
