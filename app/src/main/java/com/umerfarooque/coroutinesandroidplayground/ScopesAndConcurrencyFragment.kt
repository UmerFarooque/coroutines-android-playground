package com.umerfarooque.coroutinesandroidplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentScopesAndConcurrencyBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class ScopesAndConcurrencyFragment : Fragment() {

    private lateinit var binding: FragmentScopesAndConcurrencyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScopesAndConcurrencyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.example1.btnPlay.setOnClickListener {
            lifecycleScope.launch {
                updateJobStatus(this, binding.example1)
                val runningTime = measureTimeMillis {
                    longRunningTaskSuspend()
                    longRunningTaskSuspend()
                }
                binding.example1.log.text = getString(R.string.running_time, runningTime)
            }.showCompletionInView(lifecycleScope, binding.example1)
        }

        binding.example2.btnPlay.setOnClickListener {
            lifecycleScope.launch {
                updateJobStatus(this, binding.example2)
                val runningTime = measureTimeMillis {
                    val jobs = (1..3).map { launch { longRunningTaskSuspend() } }
                    jobs.joinAll()
                }
                binding.example2.log.text = getString(R.string.running_time, runningTime)
            }.showCompletionInView(lifecycleScope, binding.example2)
        }

        var customScope: CoroutineScope? = null
        binding.btnCreateScope.setOnClickListener {
            // TODO: Add some info on context. i.e. Why Dispatchers.MAIN
            val customContext = Job() + Dispatchers.Main
            customScope = CoroutineScope(customContext)
        }
        binding.btnCancelScope.setOnClickListener { customScope?.cancel() }
        binding.example3.btnPlay.setOnClickListener {
            customScope?.launch {
                updateJobStatus(this, binding.example3)
                val runningTime = measureTimeMillis {
                    longRunningTaskSuspend()
                }
                binding.example3.log.text = getString(R.string.running_time, runningTime)
            }?.showCompletionInView(lifecycleScope, binding.example3)
                ?: toast(R.string.scope_not_created)
        }

        binding.child1.showAsChildCoroutine()
        binding.child2.showAsChildCoroutine()
        binding.child2.btnPlay.visibility = View.GONE
        binding.example4.btnPlay.setOnClickListener {
            lifecycleScope.launch {
                updateJobStatus(this, binding.example4)
                someLongRunningTask()
                binding.example4.log.text = getString(R.string.returned_from_function)
            }.showCompletionInView(lifecycleScope, binding.example4)
        }
    }

    private suspend fun someLongRunningTask() = coroutineScope {
        launch {
            updateJobStatus(this, binding.child1)
            longRunningTaskSuspend(2000)
        }.showCompletionInView(this, binding.child1)
        launch {
            updateJobStatus(this, binding.child2)
            longRunningTaskSuspend()
        }.showCompletionInView(this, binding.child2)
    }
}
