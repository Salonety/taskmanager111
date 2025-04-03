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
    val (containerColor, textColor, text) = when (priority) {
        Priority.HIGH -> Triple(
            Color(0xFFD32F2F),  // Dark red container
            Color(0xFFFFCDD2),  // Light red text
            "High"
        )
        Priority.MEDIUM -> Triple(
            Color(0xFFF57C00),  // Dark orange container
            Color(0xFFFFE0B2),  // Light orange text
            "Medium"
        )
        Priority.LOW -> Triple(
            Color(0xFF388E3C),  // Dark green container
            Color(0xFFC8E6C9),  // Light green text
            "Low"
        )
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
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