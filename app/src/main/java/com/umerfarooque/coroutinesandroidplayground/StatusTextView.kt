package com.umerfarooque.coroutinesandroidplayground

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class StatusTextView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setJobStatus(scope: CoroutineScope) {
        val job = scope.coroutineContext[Job]
        job?.let { setJobStatus(it) }
    }

    fun setJobStatus(job: Job) {
        when (job.getState()) {
            ACTIVE -> setStatusText(context.getString(R.string.active), R.color.amber_700)
            COMPLETED -> setStatusText(context.getString(R.string.completed), R.color.green_400)
            CANCELLED -> setStatusText(context.getString(R.string.cancelled), R.color.red_800)
            CANCELLING -> setStatusText(context.getString(R.string.cancelling), R.color.red_800)
        }
    }

    fun setCompletionStatus(throwable: Throwable?) {
        if (throwable == null) {
            setStatusText(context.getString(R.string.completed), R.color.green_400)
        } else {
            setStatusText(throwable.javaClass.simpleName, R.color.red_800)
        }
    }

    private fun setStatusText(message: String, @ColorRes color: Int = R.color.black) {
        text = message
        setTextColor(ContextCompat.getColor(context, color))
    }
}

const val NEW = 0
const val ACTIVE = 1
const val COMPLETING = 2
const val CANCELLING = 3
const val CANCELLED = 4
const val COMPLETED = 5

@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
@IntDef(NEW, ACTIVE, COMPLETING, CANCELLING, CANCELLED, COMPLETED)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Status

fun CoroutineScope.getState(): @Status Int? {
    return coroutineContext[Job]?.getState()
}

fun Job.getState(): @Status Int {
    return when {
        isCancelled && !isCompleted -> CANCELLING
        isActive -> ACTIVE
        isCancelled -> CANCELLED
        isCompleted -> COMPLETED
        else -> NEW
    }
}
