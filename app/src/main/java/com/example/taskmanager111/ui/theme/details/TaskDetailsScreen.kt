package com.example.taskmanager111.ui.theme.details

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.ui.components.PriorityIndicator
import com.example.taskmanager.ui.viewmodel.TaskViewModel
import com.example.taskmanager111.data.Priority
import com.example.taskmanager111.data.Task
import com.example.taskmanager111.ui.theme.viewmodel.TaskViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    taskId: Int?,
    navController: NavController,
    viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(LocalContext.current.applicationContext as Application)
    )

) {
    // Collect all tasks and find the one matching our ID
    val tasks by viewModel.tasks.collectAsState(emptyList())
    val task = remember(taskId, tasks) {
        tasks.find { it.id == taskId }
    }

    // Handle task not found
    if (task == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Task not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("addTask?taskId=${task.id}")
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = {
                        viewModel.deleteTask(task)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Task Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Priority and Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                PriorityIndicator(priority = task.priority)
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = task.isCompleted,
                    onClick = {
                        viewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                    },
                    label = { Text(if (task.isCompleted) "Completed" else "Pending") }
                )
            }

            // Due Date
            Text(
                text = "Due: ${formatDate(task.dueDate)}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Description
            if (!task.description.isNullOrEmpty()) {
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Created At
            Text(
                text = "Created: ${formatDate(task.dueDate)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        .format(Date(timestamp))
}

@Preview
@Composable
fun TaskDetailsScreenPreview() {
    val sampleTask = Task(
        id = 1,
        title = "Complete Task Manager App",
        description = "Implement all screens and functionality for the task manager application",
        priority = Priority.HIGH,
        dueDate = System.currentTimeMillis() + 86400000 * 3, // 3 days from now
        //dueDate = System.currentTimeMillis() - 86400000 * 2 // Created 2 days ago
    )

    // For preview, use rememberNavController()
    TaskDetailsScreen(
        taskId = sampleTask.id,
        navController = rememberNavController()
    )
}