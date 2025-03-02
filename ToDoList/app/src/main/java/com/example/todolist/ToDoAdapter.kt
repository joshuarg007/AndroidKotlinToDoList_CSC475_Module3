package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter(
    private val taskList: MutableList<ToDo>,
    private val deleteTask: (ToDo) -> Unit
) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTextView.text = task.task

        holder.deleteButton.setOnClickListener {
            deleteTask(task)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    class ToDoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTextView: TextView = view.findViewById(R.id.task_text)
        val deleteButton: Button = view.findViewById(R.id.delete_task)
    }
}
