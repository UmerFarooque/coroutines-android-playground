package com.umerfarooque.coroutinesandroidplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentCoroutineContextBinding
import kotlinx.coroutines.*

class CoroutineContextFragment : Fragment() {

    private lateinit var binding: FragmentCoroutineContextBinding
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoroutineContextBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            lifecycleScope.launch { toast("Caught ${throwable.javaClass.simpleName} in handler") }
        }
        val newContext = Dispatchers.Default + exceptionHandler + CoroutineName("ExampleCoroutine")
        binding.newContextExample.btnPlay.setOnClickListener {
            val throwException = binding.checkException.isChecked
            lifecycleScope.launch(newContext) {
                val scope = this
                withContext(Dispatchers.Main) {
                    updateJobStatus(scope, binding.newContextExample)
                }
                longRunningTask()
                if (throwException) throw RuntimeException()
            }.showCompletionInView(lifecycleScope, binding.newContextExample)
        }

        binding.jobExample1.coroutineName.text = getString(R.string.parent)
        binding.childJob.run {
            coroutineName.text = getString(R.string.child)
            btnPlay.visibility = View.GONE
        }
        binding.jobExample1.btnPlay.setOnClickListener {
            val addNewJob = binding.checkNewJob.isChecked
            lifecycleScope.launch {
                updateJobStatus(this, binding.jobExample1)
                if (addNewJob) {
                    job = Job()
                    launch(job!!) {
                        updateJobStatus(this, binding.childJob)
                        delay(2000)
                    }.showCompletionInView(lifecycleScope, binding.childJob)
                } else {
                    launch {
                        updateJobStatus(this, binding.childJob)
                        delay(2000)
                    }.showCompletionInView(lifecycleScope, binding.childJob)
                }
                delay(500)
                binding.jobExample1.log.text = getString(R.string.work_done)
            }.showCompletionInView(lifecycleScope, binding.jobExample1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }
}
