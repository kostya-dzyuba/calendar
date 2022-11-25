package ru.kostya_dzyuba.calendar.fragment

import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.kostya_dzyuba.calendar.MainViewModel
import ru.kostya_dzyuba.calendar.adapter.TasksAdapter
import ru.kostya_dzyuba.calendar.databinding.FragmentMainBinding
import java.time.LocalDate
import java.util.*

class MainFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentMainBinding
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
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.adapter = adapter
        setListeners()
        observe()
    }

    override fun onClick(view: View) {
        val editText = EditText(requireContext())
        editText.hint = "Задача"
        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        val layout = FrameLayout(requireContext())
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()
        layout.setPadding(margin, 0, margin, 0)
        layout.addView(editText)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить задачу")
            .setView(layout)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.add(editText.text.toString())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.show()
        editText.requestFocus()
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun setListeners() {
        binding.add.setOnClickListener(this)
        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            viewModel.selected.value = LocalDate.of(year, month + 1, dayOfMonth)
            viewModel.filter()
        }
    }

    private fun observe() {
        viewModel.dayTasks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.empty.isVisible = it.isEmpty()
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            viewModel.filter()
        }
        viewModel.selected.observe(viewLifecycleOwner) {
            val calendar = GregorianCalendar(it.year, it.monthValue - 1, it.dayOfMonth)
            binding.calendar.date = calendar.timeInMillis
        }
    }
}