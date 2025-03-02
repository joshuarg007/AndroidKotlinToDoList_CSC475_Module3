package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ToDoDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ToDoList.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TASK = "task"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TASK TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Add a task to the database
    fun addTask(task: ToDo): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK, task.task)
        }
        return try {
            db.insert(TABLE_NAME, null, values).also {
                db.close() // Close the db after operation
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            -1 // Return -1 if insertion failed
        }
    }

    // Get all tasks from the database
    fun getAllTasks(): List<ToDo> {
        val taskList = mutableListOf<ToDo>()
        val db = readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TASK),
            null, null, null, null, null
        )

        cursor?.use {
            val taskIndex = cursor.getColumnIndexOrThrow(COLUMN_TASK)
            while (cursor.moveToNext()) {
                val task = cursor.getString(taskIndex)
                taskList.add(ToDo(task))
            }
        } ?: run {
            // Log or handle the case if cursor is null (if needed)
        }
        db.close()
        return taskList
    }

    // Delete a task from the database
    fun deleteTask(task: ToDo): Int {
        val db = writableDatabase
        return try {
            val result = db.delete(TABLE_NAME, "$COLUMN_TASK = ?", arrayOf(task.task))
            db.close()
            result
        } catch (e: SQLException) {
            e.printStackTrace()
            0 // Return 0 if deletion failed
        }
    }
}
