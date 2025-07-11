package com.vt.ft_android.model

/**
 * User - Data model representing a user in the Finance Tracker app
 * 
 * This model contains all user-related information including:
 * - Basic profile information (name, email, avatar)
 * - App preferences (theme, currency, notifications)
 * - Account settings and metadata
 * - Firebase integration fields
 * 
 * The model is designed to work with:
 * - Firebase Authentication for user identity
 * - Firestore for data persistence
 * - Jetpack Compose for UI display
 * - Settings screen for profile management
 * 
 * @property id Unique user identifier (Firebase UID)
 * @property name User's display name
 * @property email User's email address
 * @property avatar User's selected avatar (avatar1-avatar8)
 * @property currency User's preferred currency (INR, USD, EUR, etc.)
 * @property theme User's theme preference (dark/light)
 * @property notifications Whether user wants notifications
 * @property defaultSplit Default budget split percentages
 * @property createdAt Timestamp when user account was created
 * @property lastLoginAt Timestamp when user last logged in
 */
data class User(
    val id: String = "",
    val name: String = "Anonymous",
    val email: String = "",
    val avatar: String = "avatar1",
    val currency: String = "INR",
    val theme: String = "dark",
    val notifications: Boolean = true,
    val defaultSplit: List<Int> = listOf(50, 30, 20),
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    /**
     * Create default user profile
     */
    companion object {
        fun createDefault(id: String, email: String): User {
            return User(
                id = id,
                name = "Anonymous",
                email = email,
                avatar = "avatar1",
                currency = "INR",
                theme = "dark",
                notifications = true,
                defaultSplit = listOf(50, 30, 20),
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
        }
    }
}