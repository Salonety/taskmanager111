package com.example.taskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanager111.data.Priority
import com.example.taskmanager111.data.Task

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCompleteTask: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onTaskClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                PriorityIndicator(priority = task.priority)
            }

            task.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Due: ${task.dueDate}", // Format this date properly in production
                    style = MaterialTheme.typography.labelSmall
                )

                if (task.isCompleted) {
                    Text("Completed", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun PriorityIndicator(priority: Priority) {
    val (color, text) = when (priority) {
        Priority.HIGH -> Pair(Color.Red, "High")
        Priority.MEDIUM -> Pair(Color.Yellow, "Medium")
        Priority.LOW -> Pair(Color.Green, "Low")
    }

    Text(
        text = text,
        color = color
    )
}

@Preview
@Composable
fun TaskItemPreview() {
    TaskItem(
        task = Task(
            id = 1,
            title = "Sample Task",
            description = "This is a sample task description",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis()
        ),
        onTaskClick = {},
        onCompleteTask = {},
        onDeleteTask = {}
    )
}