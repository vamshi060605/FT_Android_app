package com.vt.ft_android.data

import com.vt.ft_android.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

/**
 * UserRepository - Repository for managing user data in the Finance Tracker app
 * 
 * This repository handles all user-related data operations including:
 * - User profile CRUD operations
 * - Firebase Authentication integration
 * - Firestore data persistence
 * - User preferences management
 * - Real-time data synchronization
 * 
 * Features:
 * - Secure user authentication
 * - Profile data management
 * - Preference synchronization
 * - Error handling and result types
 * - Real-time data updates
 * 
 * Data Flow:
 * - Firebase Auth for user authentication
 * - Firestore for user profile data
 * - Result types for error handling
 * - Flow for reactive data updates
 * 
 * Collections:
 * - "users": User profile data and preferences
 * 
 * Security:
 * - Users can only access their own data
 * - Firestore security rules enforce data isolation
 * - Authentication required for all operations
 */
class UserRepository {
    // Firebase instances for authentication and database
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    // Firestore collection reference for user data
    private val usersCollection = firestore.collection("users")
    
    /**
     * Get current authenticated user ID
     * 
     * @return Current user ID or null if not authenticated
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Get current authenticated user email
     * 
     * @return Current user email or null if not authenticated
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
    
    /**
     * Check if user is currently authenticated
     * 
     * @return true if user is authenticated, false otherwise
     */
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Create a new user profile in Firestore
     * 
     * This function:
     * - Creates a new user document in Firestore
     * - Sets default values for new users
     * - Handles authentication state
     * 
     * @param user User object to create
     * @return Result containing success status or error
     */
    suspend fun createUser(user: User): Result<User> {
        return try {
            // Ensure user has an ID (use Firebase UID if available)
            val userId = user.id.ifEmpty { auth.currentUser?.uid ?: return Result.failure(Exception("No authenticated user")) }
            val userWithId = user.copy(id = userId)
            
            // Save user to Firestore
            usersCollection.document(userId).set(userWithId).await()
            
            Result.success(userWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user profile by ID
     * 
     * @param userId User ID to retrieve
     * @return Result containing user data or error
     */
    suspend fun getUser(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject<User>()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Failed to parse user data"))
                }
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile in Firestore
     * 
     * @param user Updated user object
     * @return Result containing success status or error
     */
    suspend fun updateUser(user: User): Result<User> {
        return try {
            val updatedUser = user.copy(lastLoginAt = System.currentTimeMillis())
            usersCollection.document(user.id).set(updatedUser).await()
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete user profile from Firestore
     * 
     * @param userId User ID to delete
     * @return Result containing success status or error
     */
    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user profile as Flow for reactive updates
     * 
     * @param userId User ID to observe
     * @return Flow of user data updates
     */
    fun getUserFlow(userId: String): Flow<User?> {
        return callbackFlow {
            val document = usersCollection.document(userId)
            val listener = document.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject<User>()
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
            
            // Clean up listener when flow is cancelled
            awaitClose { listener.remove() }
        }
    }
    
    /**
     * Create default user profile for new users
     * 
     * This function creates a user profile with default values
     * when a new user signs up for the first time.
     * 
     * @param email User's email address
     * @param name User's display name (optional)
     * @return Result containing created user or error
     */
    suspend fun createDefaultUser(email: String, name: String = "Anonymous"): Result<User> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No authenticated user"))
            
            val defaultUser = User(
                id = userId,
                name = name,
                email = email,
                avatar = "avatar1",
                currency = "INR",
                theme = "dark",
                notifications = true,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )
            
            createUser(defaultUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update specific user field
     * 
     * @param userId User ID to update
     * @param field Field name to update
     * @param value New value for the field
     * @return Result containing success status or error
     */
    suspend fun updateUserField(userId: String, field: String, value: Any): Result<Unit> {
        return try {
            val updates = mapOf(
                field to value,
                "lastLoginAt" to System.currentTimeMillis()
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if user exists in Firestore
     * 
     * @param userId User ID to check
     * @return true if user exists, false otherwise
     */
    suspend fun checkIfUserExists(userId: String): Boolean {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Update user's last login timestamp
     * 
     * @param userId User ID to update
     * @return Result containing success status or error
     */
    suspend fun updateLastLogin(userId: String): Result<Unit> {
        return updateUserField(userId, "lastLoginAt", System.currentTimeMillis())
    }
} 