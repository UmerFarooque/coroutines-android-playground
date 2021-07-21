package com.umerfarooque.coroutinesandroidplayground.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.*
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentScopesAndConcurrencyBinding
import com.umerfarooque.coroutinesandroidplayground.databinding.LayoutCoroutineBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class ScopesAndConcurrencyFragment : Fragment() {

    private lateinit var binding: FragmentScopesAndConcurrencyBinding
    private var customScope: CoroutineScope? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScopesAndConcurrencyBinding.inflate(inflater)
        binding.child1.showAsChildCoroutine()
        binding.child2.showAsChildCoroutine()
        binding.btnCreateScope.setOnClickListener {
            val customContext = Job() + Dispatchers.Main
            customScope = CoroutineScope(customContext)
        }
        binding.btnCancelScope.setOnClickListener { customScope?.cancel() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sequentialTasksExample.btnPlay.setOnClickListener {
            runExample(binding.sequentialTasksExample, ::sequentialTasksExample)
        }

        binding.parallelTasksExample.btnPlay.setOnClickListener {
            runExample(binding.parallelTasksExample, ::parallelTasksExample)
        }

        binding.customScopeExample.btnPlay.setOnClickListener {
            customScope?.launch {
                updateJobState(this, binding.customScopeExample)
                val runningTime = measureTimeMillis {
                    longRunningTaskInBackground()
                }
                binding.customScopeExample.log.text = getString(R.string.running_time, runningTime)
            }?.showCompletionInView(lifecycleScope, binding.customScopeExample)
                ?: toast(R.string.scope_not_created)
        }

        binding.scopedCoroutineExample.btnPlay.setOnClickListener {
            runExample(binding.scopedCoroutineExample, ::scopedCoroutineExample)
        }

        binding.asyncExample.btnPlay.setOnClickListener {
            runExample(binding.asyncExample, ::asyncExample)
        }

        binding.lazyAsync.btnPlay.setOnClickListener {
            runExample(binding.lazyAsync, ::lazyAsyncExample)
        }
    }

    private suspend fun sequentialTasksExample(layout: LayoutCoroutineBinding) {
        val runningTime = measureTimeMillis {
            longRunningTaskInBackground()
            longRunningTaskInBackground()
        }
        layout.log.text = getString(R.string.running_time, runningTime)
    }

    private suspend fun parallelTasksExample(layout: LayoutCoroutineBinding) =
        coroutineScope {
            val runningTime = measureTimeMillis {
                val jobs = (1..3).map { launch { longRunningTaskInBackground() } }
                jobs.joinAll()
            }
            layout.log.text = getString(R.string.running_time, runningTime)
        }

    private suspend fun scopedCoroutineExample(layout: LayoutCoroutineBinding) {
        someLongRunningTask()
        layout.log.text = getString(R.string.returned_from_function)
    }

    private suspend fun asyncExample(layout: LayoutCoroutineBinding) =
        coroutineScope {
            val deferred1 = async { taskWithResult() }
            val deferred2 = async { taskWithResult() }
            layout.log.text = (deferred1.await() + deferred2.await()).toString()
        }

    private suspend fun lazyAsyncExample(layout: LayoutCoroutineBinding) = coroutineScope {
        layout.btnPlay.isEnabled = false
        layout.stateTv.text = ""
        val deferred = async(start = CoroutineStart.LAZY) {
            updateJobState(this, binding.lazyAsync)
            taskWithResult()
        }
        deferred.showCompletionInView(lifecycleScope, binding.lazyAsync)
        delay(1000)
        deferred.start()
    }

    private suspend fun someLongRunningTask() = coroutineScope {
        launch {
            updateJobState(this, binding.child1)
            longRunningTaskInBackground(2000)
        }.showCompletionInView(this, binding.child1)
        launch {
            updateJobState(this, binding.child2)
            longRunningTaskInBackground()
        }.showCompletionInView(this, binding.child2)
    }

    private suspend fun taskWithResult(): Int {
        delay(500)
        return 10
    }

    override fun onDestroyView() {
        super.onDestroyView()

        customScope?.cancel()
    }
}
