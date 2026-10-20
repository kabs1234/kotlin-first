package library.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import library.mobile.data.Genre

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E5E4E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7EBD5),
    onPrimaryContainer = Color(0xFF002117),
    secondary = Color(0xFF8A5A2B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDCC0),
    onSecondaryContainer = Color(0xFF2D1600),
    background = Color(0xFFFBFAF5),
    surface = Color(0xFFFBFAF5),
    surfaceContainerLow = Color(0xFFF3F1EA),
    error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9BCFB9),
    onPrimary = Color(0xFF003828),
    primaryContainer = Color(0xFF14463A),
    onPrimaryContainer = Color(0xFFB7EBD5),
    secondary = Color(0xFFF2BC8C),
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF6B4217),
    onSecondaryContainer = Color(0xFFFFDCC0),
    background = Color(0xFF141816),
    surface = Color(0xFF141816),
    surfaceContainerLow = Color(0xFF1C211F),
    error = Color(0xFFF2B8B5),
)

fun Genre.coverColor(): Color = when (this) {
    Genre.CLASSIC -> Color(0xFF7B4B94)
    Genre.FANTASY -> Color(0xFF2F7A8C)
    Genre.HISTORY -> Color(0xFF9C5B2E)
    Genre.PROGRAMMING -> Color(0xFF2E5E4E)
    Genre.SCIENCE -> Color(0xFF3B5BA5)
}

@Composable
fun LibraryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
