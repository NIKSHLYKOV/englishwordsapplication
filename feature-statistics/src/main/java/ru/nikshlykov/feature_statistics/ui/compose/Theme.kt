package ru.nikshlykov.feature_statistics.ui.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun MyTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colors = Colors,
    typography = MaterialTheme.typography,
    shapes = MaterialTheme.shapes,
    content = content
  )
}

internal val Colors = lightColors(
  primary = Color(0xFF2D9AF3),
  primaryVariant = Color(0xFF227AD2),
  secondary = Color(0xFFFCD366)
)