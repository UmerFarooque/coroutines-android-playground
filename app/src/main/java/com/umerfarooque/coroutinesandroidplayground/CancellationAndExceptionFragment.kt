package com.umerfarooque.coroutinesandroidplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentCancellationAndExceptionBinding
import com.umerfarooque.coroutinesandroidplayground.databinding.LayoutCoroutineBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class CancellationAndExceptionFragment : Fragment() {

    private lateinit var binding: FragmentCancellationAndExceptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCancellationAndExceptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancellation.btnPlay.setOnClickListener {
            runExample(binding.cancellation, ::cancellationExample)
        }

        binding.child1.showAsChildCoroutine()
        binding.child2.showAsChildCoroutine()
        binding.parentJob.btnPlay.setOnClickListener {
            val handler = CoroutineExceptionHandler { _, throwable ->
                toast(getString(R.string.caught_exception, throwable.javaClass.simpleName))
            }
            lifecycleScope.launch(handler) {    // Root Coroutine
                launch {    // Child 1
                    throw RuntimeException()
                }.showCompletionInView(lifecycleScope, binding.child1)

                launch {    // Child 2
                    delay(Long.MAX_VALUE)
                }.showCompletionInView(lifecycleScope, binding.child2)
            }.showCompletionInView(lifecycleScope, binding.parentJob)
        }

        binding.flowException.btnPlay.setOnClickListener {
            runExample(binding.flowException, ::flowExceptionExample)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun cancellationExample(layout: LayoutCoroutineBinding) {
        val cooperative = binding.cooperativeCheck.isChecked
        var now = System.currentTimeMillis()
        val next = now + 3000L
        val cancelAt = now + 500
        withContext(Dispatchers.Default) {
            while (now < next) {
                if (cooperative && !isActive) break
                now = System.currentTimeMillis()
                if (cancelAt < now) cancel()
            }
        }
    }

    private suspend fun flowExceptionExample(layout: LayoutCoroutineBinding) {
        (1..5).asFlow()
            .onEach {
                if (it == 4) throw IllegalStateException()
                layout.log.append("$it ")
            }
            .catch {
                toast(getString(R.string.caught_exception, it.javaClass.simpleName))
            }
            .collect()
    }
}
