package com.umerfarooque.coroutinesandroidplayground

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentCoroutineBasicsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoroutineBasicsFragment : Fragment() {

    private lateinit var binding: FragmentCoroutineBasicsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoroutineBasicsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()

        binding.btnRunTask.setOnClickListener {
            if (binding.checkBackgroundThread.isChecked) {
                lifecycleScope.launch(Dispatchers.Default) {
                    longRunningTask()
                }
            } else {
                longRunningTask()
            }
        }
    }

    private fun playAnimation() {
        var translateX = 200F
        val animator = ObjectAnimator.ofFloat(
            binding.animView, "translationX", translateX
        ).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }
        binding.animLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.animLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                translateX =
                    binding.animLayout.width - binding.animView.width.toFloat()
                animator.setFloatValues(translateX)
                animator.start()
            }
        })
    }
}
