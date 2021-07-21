package com.umerfarooque.coroutinesandroidplayground.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.R
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentSupervisionBinding
import com.umerfarooque.coroutinesandroidplayground.showAsChildCoroutine
import com.umerfarooque.coroutinesandroidplayground.showCompletionInView
import com.umerfarooque.coroutinesandroidplayground.toast
import kotlinx.coroutines.*

class SupervisionFragment : Fragment() {

    private lateinit var binding: FragmentSupervisionBinding
    private var customScope: CoroutineScope? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSupervisionBinding.inflate(inflater)
        binding.child1.showAsChildCoroutine()
        binding.child2.showAsChildCoroutine()
        binding.supervisorChild1.showAsChildCoroutine()
        binding.supervisorChild2.showAsChildCoroutine()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = CoroutineExceptionHandler { _, throwable ->
            toast(getString(R.string.caught_exception, throwable.javaClass.simpleName))
        }
        val context = Dispatchers.Main + SupervisorJob() + handler
        customScope = CoroutineScope(context)

        binding.supervisorParent.btnPlay.setOnClickListener {
            customScope?.launch {
                delay(500)
                throw RuntimeException()
            }?.showCompletionInView(lifecycleScope, binding.child1)

            customScope?.launch {
                delay(1500)
            }?.showCompletionInView(lifecycleScope, binding.child2)
        }

        binding.supervisorScope.btnPlay.setOnClickListener {
            lifecycleScope.launch {
                supervisorScopeExample()
            }
        }
    }

    private suspend fun supervisorScopeExample() = supervisorScope {
        coroutineContext[Job]?.showCompletionInView(lifecycleScope, binding.supervisorScope)
        val handler = CoroutineExceptionHandler { _, throwable ->
            toast(getString(R.string.caught_exception, throwable.javaClass.simpleName))
        }
        launch(handler) {
            delay(500)
            throw RuntimeException()
        }.showCompletionInView(lifecycleScope, binding.supervisorChild1)

        launch {
            delay(1500)
        }.showCompletionInView(lifecycleScope, binding.supervisorChild2)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        customScope?.cancel()
    }
}
