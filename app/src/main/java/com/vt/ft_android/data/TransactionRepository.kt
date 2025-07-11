package com.vt.ft_android.data

import com.vt.ft_android.model.Transaction
import com.vt.ft_android.model.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * TransactionRepository - Repository for managing transaction data in the Finance Tracker app
 * 
 * This repository handles all transaction-related data operations including:
 * - Transaction CRUD operations
 * - Real-time data synchronization
 * - Transaction filtering and querying
 * - Data aggregation for analytics
 * 
 * Features:
 * - Real-time transaction updates
 * - Transaction categorization
 * - Income and expense tracking
 * - Data validation and error handling
 * - Firestore integration
 * 
 * Collections:
 * - "transactions": User transaction data
 * 
 * Data Flow:
 * - Firestore for data persistence
 * - StateFlow for reactive UI updates
 * - Result types for error handling
 */
class TransactionRepository {
    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    // State flow for transactions
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions
    
    /**
     * Get current user ID
     * 
     * @return Current user ID or null if not authenticated
     */
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Get all transactions for the current user
     * 
     * @return Result containing list of transactions or error
     */
    suspend fun getTransactions(): Result<List<Transaction>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .get()
                .await()
            
            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Transaction>()?.copy(id = doc.id)
            }
            
            _transactions.value = transactions
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get transactions with filtering options
     *
     * @param categoryId Category ID to filter by (optional)
     * @param categoryName Category name to filter by (optional)
     * @param startDate Start date for filtering (optional)
     * @param endDate End date for filtering (optional)
     * @return Result containing filtered list of transactions or error
     */
    suspend fun getTransactions(
        categoryId: String? = null,
        categoryName: String? = null,
        startDate: Date? = null,
        endDate: Date? = null
    ): Result<List<Transaction>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .get()
                .await()

            var transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Transaction>()?.copy(id = doc.id)
            }

            // Apply filters
            if (categoryId != null) {
                transactions = transactions.filter { it.categoryId == categoryId }
            }
            if (categoryName != null) {
                transactions = transactions.filter { it.categoryName == categoryName }
            }
            if (startDate != null) {
                transactions = transactions.filter { it.date >= startDate }
            }
            if (endDate != null) {
                transactions = transactions.filter { it.date <= endDate }
            }

            _transactions.value = transactions
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add a new transaction
     * 
     * @param transaction Transaction to add
     * @return Result containing success status or error
     */
    suspend fun addTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .document()
            
            val newTransaction = transaction.copy(
                id = docRef.id,
                userId = userId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            docRef.set(newTransaction).await()
            
            // Update local state
            val currentTransactions = _transactions.value.toMutableList()
            currentTransactions.add(newTransaction)
            _transactions.value = currentTransactions
            
            Result.success(newTransaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing transaction
     * 
     * @param transaction Transaction to update
     * @return Result containing success status or error
     */
    suspend fun updateTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            
            val updatedTransaction = transaction.copy(
                updatedAt = System.currentTimeMillis()
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .document(transaction.id)
                .set(updatedTransaction)
                .await()
            
            // Update local state
            val currentTransactions = _transactions.value.toMutableList()
            val index = currentTransactions.indexOfFirst { it.id == transaction.id }
            if (index != -1) {
                currentTransactions[index] = updatedTransaction
                _transactions.value = currentTransactions
            }
            
            Result.success(updatedTransaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a transaction
     * 
     * @param transactionId ID of transaction to delete
     * @return Result containing success status or error
     */
    suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            
            firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .document(transactionId)
                .delete()
                .await()
            
            // Update local state
            val currentTransactions = _transactions.value.toMutableList()
            currentTransactions.removeAll { it.id == transactionId }
            _transactions.value = currentTransactions
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get transactions by type (income or expense)
     * 
     * @param type Transaction type to filter by
     * @return List of transactions of the specified type
     */
    fun getTransactionsByType(type: TransactionType): List<Transaction> {
        return _transactions.value.filter { it.type == type }
    }
    
    /**
     * Get transactions by category
     * 
     * @param categoryName Category name to filter by
     * @return List of transactions in the specified category
     */
    fun getTransactionsByCategory(categoryName: String): List<Transaction> {
        return _transactions.value.filter { it.categoryName == categoryName }
    }
    
    /**
     * Get total income
     * 
     * @return Total income amount
     */
    fun getTotalIncome(): Double {
        return _transactions.value
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }
    
    /**
     * Get total expenses
     * 
     * @return Total expense amount
     */
    fun getTotalExpenses(): Double {
        return _transactions.value
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }
    
    /**
     * Get net balance (income - expenses)
     * 
     * @return Net balance amount
     */
    fun getNetBalance(): Double {
        return getTotalIncome() - getTotalExpenses()
    }
    
    /**
     * Get recent transactions
     * 
     * @param limit Number of recent transactions to return
     * @return List of recent transactions
     */
    fun getRecentTransactions(limit: Int = 10): List<Transaction> {
        return _transactions.value
            .sortedByDescending { it.date }
            .take(limit)
    }
    
    /**
     * Clear all transactions (for testing purposes)
     * 
     * @return Result containing success status or error
     */
    suspend fun clearAllTransactions(): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .get()
                .await()
            
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
            
            // Clear local state
            _transactions.value = emptyList()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 