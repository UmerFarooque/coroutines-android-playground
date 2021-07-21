package com.umerfarooque.coroutinesandroidplayground.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.umerfarooque.coroutinesandroidplayground.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class JobStateTextView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setJobState(scope: CoroutineScope) {
        val job = scope.coroutineContext[Job]
        job?.let { setJobState(it) }
    }

    fun setJobState(job: Job) {
        when (job.getState()) {
            ACTIVE -> setStateText(context.getString(R.string.active), R.color.amber_700)
            COMPLETED -> setStateText(context.getString(R.string.completed), R.color.green_400)
            CANCELLED -> setStateText(context.getString(R.string.cancelled), R.color.red_800)
            CANCELLING -> setStateText(context.getString(R.string.cancelling), R.color.red_800)
        }
    }

    fun setCompletionState(throwable: Throwable?) {
        if (throwable == null) {
            setStateText(context.getString(R.string.completed), R.color.green_400)
        } else {
            setStateText(throwable.javaClass.simpleName, R.color.red_800)
        }
    }

    private fun setStateText(message: String, @ColorRes color: Int = R.color.black) {
        text = message
        setTextColor(ContextCompat.getColor(context, color))
    }
}
