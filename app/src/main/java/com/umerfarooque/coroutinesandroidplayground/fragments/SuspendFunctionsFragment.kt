package com.umerfarooque.coroutinesandroidplayground.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentSuspendFunctionsBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuspendFunctionsFragment : Fragment() {

    private lateinit var binding: FragmentSuspendFunctionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSuspendFunctionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.suspendExample.btnPlay.setOnClickListener {
            lifecycleScope.launch {
                showProgress()
                val token = getToken()
                val valid = validateToken(token)
                val message = if (valid) "success" else "failed"
                binding.suspendExample.log.text = message
            }
        }
    }

    private suspend fun getToken(): String {
        delay(500)
        return "token_str"
    }

    private suspend fun validateToken(token: String): Boolean {
        delay(500)
        return token.isNotEmpty()
    }

    private fun showProgress() {
        val status = "loading"
        binding.suspendExample.log.text = status
    }
}