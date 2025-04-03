package com.example.taskmanager111.ui.theme.home

import android.app.Application
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.taskmanager.ui.components.TaskItem
import com.example.taskmanager.ui.viewmodel.FilterOption
import com.example.taskmanager.ui.viewmodel.SortOption
import com.example.taskmanager.ui.viewmodel.TaskViewModel
import com.example.taskmanager111.R
import com.example.taskmanager111.data.Task
import com.example.taskmanager111.ui.theme.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sign

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val tasks by viewModel.tasks.collectAsState(emptyList())
    var sortOption by remember { mutableStateOf(SortOption.PRIORITY) }
    var filterOption by remember { mutableStateOf(FilterOption.ALL) }
    var showSortFilterDialog by remember { mutableStateOf(false) }
    var showUndoSnackbar by remember { mutableStateOf(false) }
    var deletedTask by remember { mutableStateOf<Task?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current


    // Apply sorting and filtering
    val filteredTasks = remember(tasks, filterOption) {
        when (filterOption) {
            FilterOption.ALL -> tasks
            FilterOption.COMPLETED -> tasks.filter { it.isCompleted }
            FilterOption.PENDING -> tasks.filter { !it.isCompleted }
        }
    }

    val sortedTasks = remember(filteredTasks, sortOption) {
        when (sortOption) {
            SortOption.PRIORITY -> filteredTasks.sortedByDescending { it.priority.ordinal } // Changed to sortedByDescending
            SortOption.DUE_DATE -> filteredTasks.sortedBy { it.dueDate }
            SortOption.TITLE -> filteredTasks.sortedBy { it.title }
        }
    }

    // Drag and drop state
    var draggedItem by remember { mutableStateOf<Task?>(null) }
    var draggedItemInitialIndex by remember { mutableIntStateOf(-1) }

    // Circular progress animation
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = androidx.compose.animation.core.tween(1000),
        label = "progressAnimation"
    )

    // Empty state animation
    val emptyStateComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.emptystate)
    )
    val emptyStateProgress by animateLottieCompositionAsState(
        composition = emptyStateComposition,
        iterations = LottieConstants.IterateForever
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Task Manager",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = { showSortFilterDialog = true }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Sort/Filter",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addTask") },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Circular progress indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(120.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp
                )
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(120.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "Completed",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Active filters chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = { showSortFilterDialog = true },
                    label = { Text(sortOption.displayName()) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                FilterChip(
                    selected = true,
                    onClick = { showSortFilterDialog = true },
                    label = { Text(filterOption.displayName()) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            // Task list or empty state
            if (sortedTasks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LottieAnimation(
                        composition = emptyStateComposition,
                        progress = emptyStateProgress,
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = when (filterOption) {
                            FilterOption.ALL -> "No tasks yet!"
                            FilterOption.COMPLETED -> "No completed tasks"
                            FilterOption.PENDING -> "No pending tasks"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to add your first task",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = sortedTasks,
                        key = { task -> task.id }
                    ) { task ->
                        var offsetX by remember { mutableStateOf(0f) }
                        var isDragged by remember { mutableStateOf(false) }
                        val elevation by animateDpAsState(if (isDragged) 8.dp else 2.dp)

                        // Swipeable item
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement()
                        ) {
                            // Swipe background
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Complete action background (green)
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(80.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Complete",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                // Delete action background (red)
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(80.dp)
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            // Draggable task item
                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                                    .shadow(elevation, shape = MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = {
                                                isDragged = true
                                                draggedItem = task
                                                draggedItemInitialIndex = sortedTasks.indexOf(task)
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            },
                                            onDragEnd = {
                                                isDragged = false
                                                draggedItem = null
                                                when {
                                                    offsetX > 150 -> { // Swipe right to complete
                                                        viewModel.updateTask(task.copy(isCompleted = true))
                                                        offsetX = 0f
                                                    }
                                                    offsetX < -150 -> { // Swipe left to delete
                                                        deletedTask = task
                                                        viewModel.deleteTask(task)
                                                        showUndoSnackbar = true
                                                        offsetX = 0f
                                                    }
                                                    else -> { // Return to original position
                                                        offsetX = 0f
                                                    }
                                                }
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                offsetX += dragAmount.x
                                            }
                                        )
                                    }
                                    // In your HomeScreen composable, modify the draggable modifier:
                                    .draggable(
                                        orientation = Orientation.Vertical,
                                        state = rememberDraggableState { delta ->
                                            if (isDragged) {
                                                val currentIndex = sortedTasks.indexOf(task)
                                                val newIndex = currentIndex - delta.sign.toInt()

                                                if (newIndex in sortedTasks.indices && newIndex != currentIndex) {
                                                    viewModel.reorderTasks(currentIndex, newIndex)
                                                }
                                            }
                                        },
                                        onDragStarted = {
                                            isDragged = true
                                            draggedItem = task
                                            draggedItemInitialIndex = sortedTasks.indexOf(task)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onDragStopped = {
                                            isDragged = false
                                            draggedItem = null
                                        }
                                    )
                            ) {
                                TaskItem(
                                    task = task,
                                    onTaskClick = { navController.navigate("taskDetails/${task.id}") },
                                    onCompleteTask = {
                                        viewModel.updateTask(task.copy(isCompleted = true))
                                    },
                                    onDeleteTask = {
                                        deletedTask = task
                                        viewModel.deleteTask(task)
                                        showUndoSnackbar = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Sort/Filter Dialog
    if (showSortFilterDialog) {
        AlertDialog(
            onDismissRequest = { showSortFilterDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = {
                Text(
                    "Sort & Filter",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        "Sort by:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        SortOption.values().forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { sortOption = option }
                                    .padding(12.dp)
                            ) {
                                RadioButton(
                                    selected = sortOption == option,
                                    onClick = { sortOption = option },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = option.displayName(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Filter by:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        FilterOption.values().forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { filterOption = option }
                                    .padding(12.dp)
                            ) {
                                RadioButton(
                                    selected = filterOption == option,
                                    onClick = { filterOption = option },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = option.displayName(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showSortFilterDialog = false },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Apply")
                }
            }
        )
    }

    // Undo Snackbar
    if (showUndoSnackbar && deletedTask != null) {
        val taskToUndo = deletedTask!!
        LaunchedEffect(showUndoSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = "Task deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Long
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    viewModel.insertTask(taskToUndo)
                }
                SnackbarResult.Dismissed -> {
                    // Snackbar dismissed without undo
                }
            }
            showUndoSnackbar = false
            deletedTask = null
        }
    }
}

// Extension functions for enum display names
fun SortOption.displayName(): String {
    return when (this) {
        SortOption.PRIORITY -> "Priority"
        SortOption.DUE_DATE -> "Due Date"
        SortOption.TITLE -> "Alphabetical"
    }
}

fun FilterOption.displayName(): String {
    return when (this) {
        FilterOption.ALL -> "All"
        FilterOption.COMPLETED -> "Completed"
        FilterOption.PENDING -> "Pending"
    }
}