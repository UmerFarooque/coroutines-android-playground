package com.umerfarooque.coroutinesandroidplayground.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umerfarooque.coroutinesandroidplayground.R
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentFlowBinding
import com.umerfarooque.coroutinesandroidplayground.databinding.LayoutCoroutineBinding
import com.umerfarooque.coroutinesandroidplayground.log
import com.umerfarooque.coroutinesandroidplayground.runExample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

class FlowFragment : Fragment() {

    private lateinit var binding: FragmentFlowBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFlowBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.suspendVsFlow.btnPlay.setOnClickListener {
            if (binding.flowCheck.isChecked) {
                runExample(binding.suspendVsFlow, ::flowExample)
            } else {
                runExample(binding.suspendVsFlow) { layout ->
                    layout.log.text = getListSuspend().toString()
                }
            }
        }

        binding.flowOperators.btnPlay.setOnClickListener {
            runExample(binding.flowOperators, ::operatorsExample)
        }

        binding.flowContext.btnPlay.setOnClickListener {
            runExample(binding.flowContext, ::flowOnDifferentContext)
        }

        binding.flowBufferConflate.btnPlay.setOnClickListener {
            runExample(binding.flowBufferConflate, ::bufferConflateExample)
        }
    }

    private suspend fun flowExample(layout: LayoutCoroutineBinding) {
        getListFlow().collect { layout.log.append("$it ") }
    }

    private suspend fun getListSuspend(): List<Int> {
        val list = mutableListOf<Int>()
        repeat(5) {
            delay(200)
            list.add(it)
        }
        return list
    }

    private fun getListFlow(): Flow<Int> = flow {
        log("Flow emission in context ${currentCoroutineContext()}")
        (1..5).onEach {
            delay(200)
            emit(it)
        }
    }

    private suspend fun operatorsExample(layout: LayoutCoroutineBinding) {
        (1..5).asFlow().onEach { delay(200) }
            .take(3)
            .map { it * it }
            .collect { value -> layout.log.append("$value ") }
    }

    private suspend fun flowOnDifferentContext(layout: LayoutCoroutineBinding) {
        (1..5).asFlow()
            .flowOn(Dispatchers.Default)
            .collect { value ->
                delay(300)
                layout.log.append("$value ")
                log("Flow collection in context $coroutineContext")
            }
    }

    private suspend fun bufferConflateExample(layout: LayoutCoroutineBinding) {
        val buffer = binding.flowBufferCheck.isChecked
        val conflate = binding.flowConflateCheck.isChecked
        var flow = (1..3).asFlow().onEach { delay(200) }
        if (buffer) flow = flow.buffer()
        if (conflate) flow = flow.conflate()
        val time = measureTimeMillis {
            flow.collect { delay(300) }
        }
        layout.log.text = getString(R.string.running_time, time)
    }
}
