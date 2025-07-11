package com.vt.ft_android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vt.ft_android.data.TransactionRepository
import com.vt.ft_android.data.CategoryRepository
import com.vt.ft_android.model.Category
import com.vt.ft_android.model.Transaction
import com.vt.ft_android.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TransactionViewModel : ViewModel() {
    private val repository = TransactionRepository()
    private val categoryRepository = CategoryRepository()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()
    
    private var currentFilter: TransactionType? = null
    private var currentCategoryId: String? = null
    private var currentStartDate: Date? = null
    private var currentEndDate: Date? = null

    init {
        loadTransactions()
        loadCategories()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTransactions(
                    categoryId = currentCategoryId,
                    startDate = currentStartDate,
                    endDate = currentEndDate
                )
                _transactions.value = result.getOrDefault(emptyList())
                _filteredTransactions.value = result.getOrDefault(emptyList())
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load transactions"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                // Initialize default categories if none exist
                categoryRepository.initializeDefaultCategories()
                
                val result = categoryRepository.getCategories()
                var loadedCategories = result.getOrDefault(emptyList())
                val percentSum = loadedCategories.sumOf { it.percentage.toDouble() }
                if (loadedCategories.isNotEmpty() && percentSum != 100.0) {
                    // Auto-scale all percentages to sum to 100
                    loadedCategories = loadedCategories.map { cat ->
                        cat.copy(percentage = ((cat.percentage.toDouble() / percentSum) * 100.0).toDouble())
                    }
                    // Update all categories in Firestore
                    loadedCategories.forEach { categoryRepository.updateCategory(it) }
                }
                _categories.value = loadedCategories
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load categories"
            }
        }
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        categoryId: String,
        categoryName: String,
        categoryIcon: String,
        description: String = "",
        date: Date = Date()
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val transaction = Transaction(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    categoryIcon = categoryIcon,
                    description = description,
                    date = date
                )
                
                repository.addTransaction(transaction)
                loadTransactions()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add transaction"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateTransaction(transaction)
                loadTransactions()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update transaction"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteTransaction(transactionId)
                loadTransactions()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete transaction"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterTransactions(
        type: TransactionType? = null,
        categoryId: String? = null,
        startDate: Date? = null,
        endDate: Date? = null
    ) {
        currentFilter = type
        currentCategoryId = categoryId
        currentStartDate = startDate
        currentEndDate = endDate
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTransactions(
                    categoryId = categoryId,
                    startDate = startDate,
                    endDate = endDate
                )
                _filteredTransactions.value = result.getOrDefault(emptyList())
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to filter transactions"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFilters() {
        currentFilter = null
        currentCategoryId = null
        currentStartDate = null
        currentEndDate = null
        _filteredTransactions.value = _transactions.value
    }

    fun getTransactionStats(): Map<String, Double> {
        val transactions = _transactions.value
        
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        
        val netAmount = totalIncome - totalExpense
        
        return mapOf(
            "totalIncome" to totalIncome,
            "totalExpense" to totalExpense,
            "netAmount" to netAmount
        )
    }

    fun getTransactionsByCategory(categoryId: String): List<Transaction> {
        return _transactions.value.filter { it.categoryId == categoryId }
    }

    fun getTransactionsByType(type: TransactionType): List<Transaction> {
        return _transactions.value.filter { it.type == type }
    }

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): List<Transaction> {
        return _transactions.value.filter { 
            it.date >= startDate && it.date <= endDate 
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(category)
                loadCategories()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update category"
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.addCategory(category)
                loadCategories()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add category"
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(categoryId)
                loadCategories()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete category"
            }
        }
    }
    
    fun clearAllTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.clearAllTransactions()
                loadTransactions()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to clear transactions"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 