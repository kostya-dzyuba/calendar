package ru.kostya_dzyuba.calendar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.kostya_dzyuba.calendar.MainViewModel
import ru.kostya_dzyuba.calendar.adapter.TasksAdapter
import ru.kostya_dzyuba.calendar.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        adapter = TasksAdapter { id, long ->
            if (long) {
                viewModel.delete(id)
            } else {
                viewModel.check(id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recycler.adapter = adapter
        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
            binding.empty.isVisible = it.isEmpty()
        }
    }
}