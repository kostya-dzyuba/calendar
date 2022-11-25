package ru.kostya_dzyuba.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kostya_dzyuba.calendar.model.Task

class TasksAdapter(val onClick: (Long, Boolean) -> Unit) :
    ListAdapter<Task, TasksAdapter.TaskViewHolder>(DiffCallback) {
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            val checkedText = itemView as CheckedTextView
            checkedText.text = task.name
            checkedText.isChecked = task.completed
            checkedText.setOnClickListener { onClick(task.id, false) }
            checkedText.setOnLongClickListener { onClick(task.id, true); true }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = android.R.layout.simple_list_item_multiple_choice
        val view = inflater.inflate(layout, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}