package com.umerfarooque.coroutinesandroidplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentCoroutineBasicsBinding

class CoroutineBasicsFragment : Fragment() {

    private var binding: FragmentCoroutineBasicsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bindingInstance = FragmentCoroutineBasicsBinding.inflate(inflater)
        binding = bindingInstance
        return binding?.root
    }
}