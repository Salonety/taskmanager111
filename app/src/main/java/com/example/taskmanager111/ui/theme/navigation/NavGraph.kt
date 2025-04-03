package com.example.taskmanager111.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskmanager111.ui.theme.themefiles.ThemeManager
import com.example.taskmanager111.ui.theme.addtask.AddTaskScreen
import com.example.taskmanager111.ui.theme.details.TaskDetailsScreen
import com.example.taskmanager111.ui.theme.home.HomeScreen
import com.example.taskmanager111.ui.theme.settings.SettingsScreen

@Composable
fun TaskManagerNavGraph(
    navController: NavHostController,
    themeManager: ThemeManager
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }

        composable("addTask") {
            AddTaskScreen(navController)
        }

        composable("addTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            AddTaskScreen(navController)
        }

        composable("taskDetails/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            TaskDetailsScreen(taskId, navController)
        }

        composable("settings") {
            SettingsScreen(navController, themeManager)
        }
    }
}