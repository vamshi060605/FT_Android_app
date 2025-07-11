package com.vt.ft_android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.vt.ft_android.data.UserRepository
import com.vt.ft_android.data.CategoryRepository
import com.vt.ft_android.model.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val categoryRepository = CategoryRepository()

    // Email/Password sign-in
    fun signInWithEmail(onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        viewModelScope.launch {
                            handleUserLogin(user.uid, user.email ?: "")
                        }
                    onSuccess()
                    }
                } else {
                    errorMessage = task.exception?.localizedMessage
                }
            }
    }

    // Email/Password registration
    fun registerWithEmail(onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        viewModelScope.launch {
                            handleUserRegistration(user.uid, user.email ?: "")
                        }
                    onSuccess()
                    }
                } else {
                    errorMessage = task.exception?.localizedMessage
                }
            }
    }

    // Google Sign-In (to be triggered from Activity)
    fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        viewModelScope.launch {
                            handleUserLogin(user.uid, user.email ?: "")
                        }
                    onSuccess()
                    }
                } else {
                    errorMessage = task.exception?.localizedMessage
                }
            }
    }

    private suspend fun handleUserLogin(uid: String, email: String) {
        try {
            // Check if user exists in Firestore
            val userExists = userRepository.checkIfUserExists(uid)
            
            if (!userExists) {
                // First time user - create profile and default categories
                handleUserRegistration(uid, email)
            } else {
                // Update last login time
                userRepository.updateLastLogin(uid)
            }
        } catch (e: Exception) {
            errorMessage = "Error handling user login: ${e.message}"
        }
    }

    private suspend fun handleUserRegistration(uid: String, email: String) {
        try {
            // Create default user profile
            val defaultUser = User.createDefault(uid, email)
            val userResult = userRepository.createUser(defaultUser)
            
            if (userResult.isSuccess) {
                // Create default categories
                val categoriesResult = categoryRepository.initializeDefaultCategories()
                
                if (categoriesResult.isFailure) {
                    errorMessage = "Error creating default categories: ${categoriesResult.exceptionOrNull()?.message}"
                }
            } else {
                errorMessage = "Error creating user profile: ${userResult.exceptionOrNull()?.message}"
            }
        } catch (e: Exception) {
            errorMessage = "Error during user registration: ${e.message}"
        }
    }

    fun clearError() {
        errorMessage = null
    }
} 