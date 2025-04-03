package com.example.taskmanager111.ui.theme.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager111.R
import com.example.taskmanager111.ui.theme.themefiles.ThemeManager
import com.example.taskmanager111.ui.theme.themefiles.rememberThemeManager
import com.example.taskmanager111.ui.theme.themefiles.TaskManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    themeManager: ThemeManager = rememberThemeManager()
) {
    val themeState by themeManager.themeState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Theme Mode Section
            Text(
                text = stringResource(R.string.theme_mode),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ThemeSelectionCard(
                title = stringResource(R.string.light_theme),
                isSelected = !themeState.isDarkTheme && !themeState.useSystemTheme,
                onClick = {
                    themeManager.setDarkTheme(false)
                    themeManager.setSystemTheme(false)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ThemeSelectionCard(
                title = stringResource(R.string.dark_theme),
                isSelected = themeState.isDarkTheme && !themeState.useSystemTheme,
                onClick = {
                    themeManager.setDarkTheme(true)
                    themeManager.setSystemTheme(false)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ThemeSelectionCard(
                title = stringResource(R.string.system_theme),
                isSelected = themeState.useSystemTheme,
                onClick = {
                    themeManager.setSystemTheme(true)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Color Section
            if (themeManager.isDynamicColorAvailable()) {
                DynamicColorOption(
                    text = stringResource(R.string.dynamic_color),
                    selected = themeState.useDynamicColor,
                    onClick = { themeManager.setDynamicColor(!themeState.useDynamicColor) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Color Palette Selection
                if (!themeState.useDynamicColor) {
                    Text(
                        text = stringResource(R.string.accent_color),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ColorPaletteGrid(
                        colors = listOf(
                            Color(0xFF6750A4) to "Purple",
                            Color(0xFFE8175D) to "Pink",
                            Color(0xFF2196F3) to "Blue",
                            Color(0xFF4CAF50) to "Green",
                            Color(0xFFFF9800) to "Orange",
                            Color(0xFF9C27B0) to "Deep Purple"
                        ),
                        selectedColor = themeState.customColor,
                        onColorSelected = { color ->
                            themeManager.setCustomColor(color)
                            themeManager.setDynamicColor(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSelectionCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun DynamicColorOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = selected,
                onCheckedChange = { onClick() }
            )
        }
    }
}

@Composable
private fun ColorPaletteGrid(
    colors: List<Pair<Color, String>>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column {
        colors.chunked(3).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowColors.forEach { (color, name) ->
                    ColorSelectionButton(
                        color = color,
                        name = name,
                        isSelected = selectedColor == color,
                        onClick = { onColorSelected(color) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ColorSelectionButton(
    color: Color,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { onClick() }
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    TaskManagerTheme {
        SettingsScreen(navController = rememberNavController())
    }
}