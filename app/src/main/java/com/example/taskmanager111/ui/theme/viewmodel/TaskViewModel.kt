package com.example.taskmanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager111.data.Priority
import com.example.taskmanager111.data.Task
import com.example.taskmanager111.data.TaskDao
import com.example.taskmanager111.data.TaskDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()

    // Sorting and filtering options
    private val _sortOption = MutableStateFlow(SortOption.PRIORITY)
    private val _filterOption = MutableStateFlow(FilterOption.ALL)

    // Main tasks flow with sorting and filtering
    val tasks: Flow<List<Task>> = combine(
        taskDao.getAllTasks(),
        _sortOption,
        _filterOption
    ) { allTasks, sortOption, filterOption ->
        // First apply filtering
        val filteredTasks = when (filterOption) {
            FilterOption.ALL -> allTasks
            FilterOption.COMPLETED -> allTasks.filter { it.isCompleted }
            FilterOption.PENDING -> allTasks.filter { !it.isCompleted }
        }

        // Then apply sorting
        when (sortOption) {
            SortOption.PRIORITY -> filteredTasks.sortedBy { it.priority.ordinal }
            SortOption.DUE_DATE -> filteredTasks.sortedBy { it.dueDate }
            SortOption.TITLE -> filteredTasks.sortedBy { it.title }
        }
    }

    // Task operations
    fun insertTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
    }

    // Task reordering
    fun reorderTasks(fromPosition: Int, toPosition: Int) = viewModelScope.launch {
        val currentTasks = taskDao.getAllTasks().firstOrNull() ?: return@launch

        if (fromPosition in currentTasks.indices && toPosition in currentTasks.indices) {
            // Create a mutable copy of the list
            val updatedTasks = currentTasks.toMutableList()

            // Remove the task from its original position
            val movedTask = updatedTasks.removeAt(fromPosition)

            // Insert it at the new position
            updatedTasks.add(toPosition, movedTask)

            // Update all tasks with their new positions
            updatedTasks.forEachIndexed { index, task ->
                val updatedTask = task.copy() // Create a copy to trigger Room's update
                taskDao.update(updatedTask)
            }
        }
    }

    // Sorting and filtering options
    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun updateFilterOption(option: FilterOption) {
        _filterOption.value = option
    }

    // Helper function to get a single task by ID
    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id).firstOrNull()
    }
}

enum class SortOption(val displayName: String) {
    PRIORITY("Priority"),
    DUE_DATE("Due Date"),
    TITLE("Title")
}

enum class FilterOption(val displayName: String) {
    ALL("All"),
    COMPLETED("Completed"),
    PENDING("Pending")
}