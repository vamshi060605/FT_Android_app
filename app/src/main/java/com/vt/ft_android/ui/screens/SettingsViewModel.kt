package com.vt.ft_android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vt.ft_android.data.UserRepository
import com.vt.ft_android.data.TransactionRepository
import com.vt.ft_android.data.BudgetRepository
import com.vt.ft_android.model.User
import com.vt.ft_android.model.Transaction
import com.vt.ft_android.model.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * SettingsViewModel - ViewModel for the Settings screen
 * 
 * This ViewModel manages:
 * - User profile data and updates
 * - Theme preferences
 * - Notification settings
 * - Data export functionality
 * - Authentication state
 * 
 * Features:
 * - Real-time user data updates
 * - CSV data export with file sharing
 * - Profile editing (name, currency, avatar)
 * - Theme and notification preference management
 * - Secure logout functionality
 * 
 * Data Flow:
 * - Loads user data from Firestore
 * - Updates user preferences in real-time
 * - Exports financial data as CSV
 * - Manages authentication state
 */
class SettingsViewModel : ViewModel() {
    // Data repositories for different data types
    private val userRepository = UserRepository()
    private val transactionRepository = TransactionRepository()
    private val budgetRepository = BudgetRepository()
    
    // Firebase authentication instance
    private val auth = FirebaseAuth.getInstance()
    
    // User data state flow
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    /**
     * Load user data from Firestore
     * Called when the settings screen is initialized
     * 
     * @param uid User ID to load data for
     */
    fun loadUser(uid: String) {
        viewModelScope.launch {
            val result = userRepository.getUser(uid)
            result.onSuccess { user ->
                _user.value = user
            }
        }
    }
    
    /**
     * Update user name
     * Updates the user's display name in Firestore
     * 
     * @param uid User ID
     * @param newName New name to set
     */
    fun updateUserName(uid: String, newName: String) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(name = newName)
                val result = userRepository.updateUser(updatedUser)
                result.onSuccess {
                    _user.value = updatedUser
                }
            }
        }
    }
    
    /**
     * Update user email
     * Updates the user's email in Firestore
     * 
     * @param uid User ID
     * @param newEmail New email to set
     */
    fun updateUserEmail(uid: String, newEmail: String) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(email = newEmail)
                val result = userRepository.updateUser(updatedUser)
                result.onSuccess {
                    _user.value = updatedUser
                }
            }
        }
    }
    
    /**
     * Update user avatar
     * Updates the user's avatar selection in Firestore
     * 
     * @param uid User ID
     * @param newAvatar New avatar to set (avatar1-avatar8)
     */
    fun updateUserAvatar(uid: String, newAvatar: String) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(avatar = newAvatar)
                val result = userRepository.updateUser(updatedUser)
                result.onSuccess {
                    _user.value = updatedUser
                }
            }
        }
    }
    
    /**
     * Update user theme preference
     * Updates the user's theme preference (dark/light) in Firestore
     * 
     * @param uid User ID
     * @param newTheme New theme to set ("dark" or "light")
     */
    fun updateUserTheme(uid: String, newTheme: String) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(theme = newTheme)
                val result = userRepository.updateUser(updatedUser)
                result.onSuccess {
                    _user.value = updatedUser
                }
            }
        }
    }
    
    /**
     * Update user currency preference
     * Updates the user's currency preference in Firestore
     * 
     * @param uid User ID
     * @param newCurrency New currency to set (INR, USD, EUR, etc.)
     */
    fun updateUserCurrency(uid: String, newCurrency: String) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(currency = newCurrency)
                val result = userRepository.updateUser(updatedUser)
                result.onSuccess {
                    _user.value = updatedUser
                }
            }
        }
    }
    
    /**
     * Update user notification preferences
     * Updates the user's notification settings in Firestore
     * 
     * @param uid User ID
     * @param enabled Whether notifications should be enabled
     */
    fun updateUserNotifications(uid: String, enabled: Boolean) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(notifications = enabled)
                val result = userRepository.updateUser(updatedUser)
                result.onSuccess {
                    _user.value = updatedUser
                }
            }
        }
    }
    
    /**
     * Update user profile (comprehensive update)
     * Updates multiple user profile fields at once
     * 
     * @param uid User ID
     * @param newName New name to set
     * @param newCurrency New currency to set
     * @param newAvatar New avatar to set
     */
    fun updateUserProfile(uid: String, newName: String, newCurrency: String, newAvatar: String) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    name = newName,
                    currency = newCurrency,
                    avatar = newAvatar
                )
                val result = userRepository.updateUser(updatedUser)
                result.onSuccess {
                    _user.value = updatedUser
                }
            }
        }
    }
    
    /**
     * Export user's financial data as CSV
     * 
     * This function:
     * - Fetches all user transactions and budgets
     * - Creates a CSV file with the data
     * - Shares the file using Android's share intent
     * - Handles file creation and sharing permissions
     * 
     * CSV Format:
     * - Transactions section: Date, Type, Category, Amount, Description
     * - Budgets section: Category, Percentage, Spent, Remaining
     * 
     * @param context Android context for file operations and sharing
     */
    fun exportData(context: Context) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Fetch user's financial data
                    val transactionsResult = transactionRepository.getTransactions()
                    val budgetsResult = budgetRepository.getBudgets()
                    
                    val transactions = transactionsResult.getOrNull() ?: emptyList()
                    val budgets = budgetsResult.getOrNull() ?: emptyList()
                    
                    // Create CSV content with transactions and budgets
                    val csvContent = buildString {
                        // Transactions CSV section
                        appendLine("Transactions")
                        appendLine("Date,Type,Category,Amount,Description")
                        transactions.forEach { transaction ->
                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(transaction.date)
                            appendLine("$date,${transaction.type},${transaction.categoryName},${transaction.amount},${transaction.description}")
                        }
                        appendLine()
                        
                        // Budgets CSV section
                        appendLine("Budgets")
                        appendLine("Category,Percentage,Spent,Remaining")
                        budgets.forEach { budget ->
                            val spent = budget.spent ?: 0.0
                            val remaining = budget.amount - spent
                            appendLine("${budget.categoryName},${budget.percentage}%,$spent,$remaining")
                        }
                    }
                    
                    // Create temporary file in app's cache directory
                    val fileName = "finance_data_${System.currentTimeMillis()}.csv"
                    val file = File(context.cacheDir, fileName)
                    FileWriter(file).use { writer ->
                        writer.write(csvContent)
                    }
                    
                    // Create file URI using FileProvider for secure sharing
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    
                    // Create share intent with CSV file
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_SUBJECT, "Finance Tracker Data Export")
                        putExtra(Intent.EXTRA_TEXT, "Here's your exported financial data from Finance Tracker app.")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    
                    // Launch share intent
                    context.startActivity(Intent.createChooser(shareIntent, "Share Financial Data"))
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error exporting data", e)
            }
        }
    }
    
    /**
     * Logout user from the app
     * 
     * This function:
     * - Signs out the user from Firebase Authentication
     * - Clears the local user data
     * - Should be followed by navigation to auth screen
     */
    fun logout() {
        auth.signOut()
        _user.value = null
    }
} 