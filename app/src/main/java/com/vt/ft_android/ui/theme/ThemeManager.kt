package com.vt.ft_android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * ThemeManager - Centralized theme management for the Finance Tracker app
 * 
 * This object manages:
 * - Dark/Light mode state
 * - Theme switching functionality
 * - Color scheme definitions
 * - Global theme access
 * 
 * Usage:
 * - Check current theme: ThemeManager.isDarkMode
 * - Toggle theme: ThemeManager.toggleTheme()
 * - Set specific theme: ThemeManager.setTheme(dark: Boolean)
 */
object ThemeManager {
    // Current theme state (true = dark mode, false = light mode)
    var isDarkMode by mutableStateOf(true)
        private set
    
    /**
     * Toggle between dark and light themes
     * This will trigger recomposition of all themed components
     */
    fun toggleTheme() {
        isDarkMode = !isDarkMode
    }
    
    /**
     * Set the theme to a specific mode
     * @param dark true for dark mode, false for light mode
     */
    fun setTheme(dark: Boolean) {
        isDarkMode = dark
    }
}

/**
 * Light theme color scheme
 * 
 * Color palette designed for:
 * - Professional finance app appearance
 * - Good readability and accessibility
 * - Consistent with Material Design 3 guidelines
 * - Blue-based primary colors for trust and professionalism
 */
private val LightColors = lightColorScheme(
    // Primary colors - Main brand colors
    primary = Color(0xFF1976D2),                    // Main blue
    onPrimary = Color.White,                        // Text on primary
    primaryContainer = Color(0xFFBBDEFB),           // Light blue background
    onPrimaryContainer = Color(0xFF0D47A1),         // Text on primary container
    
    // Secondary colors - Supporting colors
    secondary = Color(0xFF424242),                  // Dark gray
    onSecondary = Color.White,                      // Text on secondary
    secondaryContainer = Color(0xFFE0E0E0),         // Light gray background
    onSecondaryContainer = Color(0xFF212121),       // Text on secondary container
    
    // Tertiary colors - Accent colors
    tertiary = Color(0xFF4CAF50),                   // Green for success/positive
    onTertiary = Color.White,                       // Text on tertiary
    tertiaryContainer = Color(0xFFC8E6C9),          // Light green background
    onTertiaryContainer = Color(0xFF2E7D32),        // Text on tertiary container
    
    // Background colors
    background = Color(0xFFFAFAFA),                 // Main background
    onBackground = Color(0xFF212121),               // Text on background
    surface = Color.White,                          // Card/surface background
    onSurface = Color(0xFF212121),                  // Text on surface
    
    // Surface variants
    surfaceVariant = Color(0xFFF5F5F5),             // Alternative surface
    onSurfaceVariant = Color(0xFF424242),           // Text on surface variant
    
    // Outline colors
    outline = Color(0xFFBDBDBD),                    // Border color
    outlineVariant = Color(0xFFE0E0E0),             // Alternative border
    
    // Utility colors
    scrim = Color(0xFF000000),                      // Overlay color
    inverseSurface = Color(0xFF212121),             // Inverse surface
    inverseOnSurface = Color(0xFFFAFAFA),           // Text on inverse surface
    inversePrimary = Color(0xFF90CAF9),             // Inverse primary
    surfaceTint = Color(0xFF1976D2)                 // Surface tint
)

/**
 * Dark theme color scheme
 * 
 * Color palette designed for:
 * - Dark mode comfort and reduced eye strain
 * - High contrast for accessibility
 * - Consistent with Material Design 3 dark theme guidelines
 * - Blue-based primary colors matching light theme
 */
private val DarkColors = darkColorScheme(
    // Primary colors - Main brand colors (adjusted for dark mode)
    primary = Color(0xFF90CAF9),                    // Light blue for dark mode
    onPrimary = Color(0xFF0D47A1),                  // Dark blue text on primary
    primaryContainer = Color(0xFF1976D2),           // Dark blue background
    onPrimaryContainer = Color.White,               // White text on primary container
    
    // Secondary colors - Supporting colors
    secondary = Color(0xFFBDBDBD),                  // Light gray
    onSecondary = Color(0xFF212121),                // Dark text on secondary
    secondaryContainer = Color(0xFF424242),         // Dark gray background
    onSecondaryContainer = Color.White,             // White text on secondary container
    
    // Tertiary colors - Accent colors
    tertiary = Color(0xFF81C784),                   // Light green
    onTertiary = Color(0xFF2E7D32),                 // Dark green text on tertiary
    tertiaryContainer = Color(0xFF4CAF50),          // Green background
    onTertiaryContainer = Color.White,              // White text on tertiary container
    
    // Background colors
    background = Color(0xFF121212),                 // Dark background
    onBackground = Color.White,                     // White text on background
    surface = Color(0xFF1E1E1E),                    // Dark surface
    onSurface = Color.White,                        // White text on surface
    
    // Surface variants
    surfaceVariant = Color(0xFF2D2D2D),             // Alternative dark surface
    onSurfaceVariant = Color(0xFFBDBDBD),           // Light text on surface variant
    
    // Outline colors
    outline = Color(0xFF424242),                    // Dark border
    outlineVariant = Color(0xFF2D2D2D),             // Alternative dark border
    
    // Utility colors
    scrim = Color(0xFF000000),                      // Overlay color
    inverseSurface = Color(0xFFFAFAFA),             // Inverse surface (light)
    inverseOnSurface = Color(0xFF212121),           // Dark text on inverse surface
    inversePrimary = Color(0xFF1976D2),             // Inverse primary
    surfaceTint = Color(0xFF90CAF9)                 // Surface tint
)

/**
 * Main theme composable for the Finance Tracker app
 * 
 * This composable:
 * - Applies the appropriate color scheme based on ThemeManager.isDarkMode
 * - Provides typography from the Typography object
 * - Wraps the content with MaterialTheme
 * 
 * @param content The composable content to be themed
 */
@Composable
fun FTAndroidTheme(
    content: @Composable () -> Unit
) {
    // Select color scheme based on current theme state
    val colorScheme = if (ThemeManager.isDarkMode) DarkColors else LightColors
    
    // Apply Material3 theme with selected colors and typography
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 