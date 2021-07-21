package com.umerfarooque.coroutinesandroidplayground.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.*
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentCoroutineContextBinding
import com.umerfarooque.coroutinesandroidplayground.databinding.LayoutCoroutineBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineContextFragment : Fragment() {

    private lateinit var binding: FragmentCoroutineContextBinding
    private var newJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoroutineContextBinding.inflate(inflater)
        binding.jobInContextExample.coroutineName.text = getString(R.string.parent)
        binding.childJob.showAsChildCoroutine()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            lifecycleScope.launch {
                toast(getString(R.string.caught_exception, throwable.javaClass.simpleName))
            }
        }
        val newContext = Dispatchers.Default + exceptionHandler + CoroutineName("ExampleCoroutine")
        binding.newContextExample.btnPlay.setOnClickListener {
            newContextExample(newContext)
        }

        binding.jobInContextExample.btnPlay.setOnClickListener {
            runExample(binding.jobInContextExample, ::jobInContextExample)
        }
    }

    private fun newContextExample(newContext: CoroutineContext) {
        val throwException = binding.checkException.isChecked
        lifecycleScope.launch(newContext) {
            val scope = this
            withContext(Dispatchers.Main) { // Update UI on main thread
                updateJobState(scope, binding.newContextExample)
            }
            longRunningTask()
            if (throwException) throw RuntimeException()
        }.showCompletionInView(lifecycleScope, binding.newContextExample)
    }

    private suspend fun jobInContextExample(layout: LayoutCoroutineBinding): Unit = coroutineScope {
        val addNewJob = binding.checkNewJob.isChecked
        val context = if (addNewJob) newJob!! else EmptyCoroutineContext
        launch(context) {
            updateJobState(this, binding.childJob)
            delay(2000)
        }.showCompletionInView(lifecycleScope, binding.childJob)
        delay(500)
        layout.log.text = getString(R.string.work_done)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        newJob?.cancel()
    }
}
