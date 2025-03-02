package com.example.todolist

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : ComponentActivity() {
    private lateinit var addTaskButton: MaterialButton
    private lateinit var enterTaskEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ToDoAdapter
    private val taskList = mutableListOf<ToDo>()
    private lateinit var dbHelper: ToDoDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        addTaskButton = findViewById(R.id.add_task_button)
        enterTaskEditText = findViewById(R.id.enter_task)
        recyclerView = findViewById(R.id.task_recycler_view)

        // Initialize the database helper
        dbHelper = ToDoDatabaseHelper(this)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ToDoAdapter(taskList) { task -> deleteTask(task) }
        recyclerView.adapter = adapter

        // Load tasks from the database
        loadTasks()

        // Set up button click listener
        addTaskButton.setOnClickListener {
            val taskText = enterTaskEditText.text.toString()
            if (taskText.isNotEmpty()) {
                addTask(taskText)
                enterTaskEditText.text.clear() // Clear the input field after adding
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTask(taskText: String) {
        // Create a new task and add to database
        val newTask = ToDo(taskText)
        val result = dbHelper.addTask(newTask)

        if (result != -1L) {
            // Add task to the list if insertion is successful
            taskList.add(newTask)

            // Notify RecyclerView about the new item inserted
            adapter.notifyItemInserted(taskList.size - 1)
        } else {
            Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(task: ToDo) {
        // Find the position of the task in the list
        val position = taskList.indexOf(task)

        // Remove task from the list
        val result = dbHelper.deleteTask(task)

        if (result > 0) {
            taskList.removeAt(position)

            // Notify RecyclerView about the item removed
            adapter.notifyItemRemoved(position)

            // If you want to handle animations as well, you could also use notifyItemRangeChanged
            // adapter.notifyItemRangeChanged(position, taskList.size)
        } else {
            Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTasks() {
        // Load tasks from the database
        val tasks = dbHelper.getAllTasks()
        taskList.clear()
        taskList.addAll(tasks)

        // Notify adapter about the changes (here, it's an entire list update)
        adapter.notifyItemRangeInserted(0, tasks.size)
    }
}
