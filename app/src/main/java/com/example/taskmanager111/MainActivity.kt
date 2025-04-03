package com.example.taskmanager111

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager111.ui.theme.themefiles.TaskManagerTheme
import com.example.taskmanager111.ui.theme.navigation.TaskManagerNavGraph
import com.example.taskmanager111.ui.theme.themefiles.rememberThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeManager = rememberThemeManager()
            val themeState by themeManager.themeState.collectAsState()

            TaskManagerTheme(
                darkTheme = when {
                    themeState.useSystemTheme -> null // Let system decide
                    else -> themeState.isDarkTheme
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskManagerNavGraph(
                        navController = rememberNavController(),
                        themeManager = themeManager
                    )
                }
            }
        }
    }
}