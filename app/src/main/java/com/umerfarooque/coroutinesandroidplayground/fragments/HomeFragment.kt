package com.umerfarooque.coroutinesandroidplayground.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umerfarooque.coroutinesandroidplayground.R
import com.umerfarooque.coroutinesandroidplayground.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val labelsWithDestinations by lazy {
        with(resources) {
            val contents = getStringArray(R.array.contents)
            val fragmentIds = obtainTypedArray(R.array.fragments).run {
                (0 until length()).map { getResourceId(it, -1) }
            }
            contents.zip(fragmentIds)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val bindingInstance = FragmentHomeBinding.inflate(inflater, container, false)
        binding = bindingInstance
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.contentsRv?.apply {
            val contentsAdapter = ContentsAdapter().apply {
                setContents(labelsWithDestinations)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = contentsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

class ContentsAdapter : RecyclerView.Adapter<ContentsAdapter.ContentsViewHolder>() {

    private var contents: List<Pair<String, Int>>? = null

    fun setContents(contents: List<Pair<String, Int>>) {
        this.contents = contents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_content, parent, false)
        return ContentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentsViewHolder, position: Int) {
        contents?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount() = contents?.size ?: 0

    inner class ContentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(contentAndDestination: Pair<String, Int>) {
            itemView.findViewById<TextView>(R.id.title).apply {
                text = contentAndDestination.first
                setOnClickListener {
                    findNavController().navigate(contentAndDestination.second)
                }
            }
        }
    }
}
