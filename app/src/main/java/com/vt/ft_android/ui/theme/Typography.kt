package com.vt.ft_android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography - Text style definitions for the Finance Tracker app
 * 
 * This object defines consistent text styles across the app following Material Design 3 guidelines.
 * Each style is designed for specific use cases and maintains readability and hierarchy.
 * 
 * Typography Hierarchy:
 * - Display: Large, prominent text (headlines, hero sections)
 * - Headline: Section titles and major headings
 * - Title: Card titles, dialog titles, navigation labels
 * - Body: Main content text, descriptions, paragraphs
 * - Label: Form labels, buttons, small text elements
 */
val Typography = Typography(
    /**
     * Body Large - Primary content text
     * Used for: Main paragraphs, descriptions, card content
     * Size: 16sp with 24sp line height for comfortable reading
     */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    
    /**
     * Body Medium - Secondary content text
     * Used for: Supporting text, captions, secondary information
     * Size: 14sp with 20sp line height for compact but readable text
     */
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    
    /**
     * Body Small - Tertiary content text
     * Used for: Fine print, metadata, timestamps, small labels
     * Size: 12sp with 16sp line height for compact information
     */
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    /**
     * Title Large - Major section titles
     * Used for: Page titles, major headings, important labels
     * Size: 22sp with 28sp line height for prominent headings
     */
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    /**
     * Title Medium - Section titles
     * Used for: Card titles, dialog titles, navigation labels
     * Size: 16sp with 24sp line height for clear hierarchy
     */
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    
    /**
     * Title Small - Subsection titles
     * Used for: Subheadings, minor titles, compact labels
     * Size: 14sp with 20sp line height for secondary titles
     */
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    /**
     * Headline Large - Main page headlines
     * Used for: App title, major page headings, hero text
     * Size: 32sp with 40sp line height for maximum impact
     */
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    
    /**
     * Headline Medium - Section headlines
     * Used for: Major section headings, important titles
     * Size: 28sp with 36sp line height for strong hierarchy
     */
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    
    /**
     * Headline Small - Subsection headlines
     * Used for: Subsection headings, card titles, dialog titles
     * Size: 24sp with 32sp line height for clear section breaks
     */
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    /**
     * Label Large - Primary labels
     * Used for: Button text, form labels, navigation items
     * Size: 14sp with 20sp line height for interactive elements
     */
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    /**
     * Label Medium - Secondary labels
     * Used for: Secondary buttons, small labels, metadata
     * Size: 12sp with 16sp line height for compact labels
     */
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    
    /**
     * Label Small - Tertiary labels
     * Used for: Fine print, small metadata, compact information
     * Size: 11sp with 16sp line height for minimal text
     */
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
) 