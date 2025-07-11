package com.vt.ft_android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vt.ft_android.data.BudgetRepository
import com.vt.ft_android.model.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class BudgetViewModel : ViewModel() {
    private val budgetRepository = BudgetRepository()

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBudgets()
    }

    fun loadBudgets() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Initialize default budgets if none exist
                budgetRepository.initializeDefaultBudgets()
                
                // Get total income for calculations
                val income = budgetRepository.getTotalMonthlyIncome().getOrThrow()
                _totalIncome.value = income
                
                // Get budgets and calculate amounts based on percentages
                val result = budgetRepository.getBudgets()
                val budgets = result.getOrThrow()
                
                // Update budget amounts based on percentages and total income
                val updatedBudgets = budgets.map { budget ->
                    budget.copy(amount = (budget.percentage / 100.0) * income)
                }
                
                _budgets.value = updatedBudgets
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load budgets"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addBudget(categoryName: String, categoryIcon: String, percentage: Double) {
        viewModelScope.launch {
            try {
                val newBudget = Budget(
                    id = UUID.randomUUID().toString(),
                    categoryName = categoryName,
                    categoryIcon = categoryIcon,
                    percentage = percentage,
                    isDefault = false
                )
                budgetRepository.addBudget(newBudget)
                loadBudgets()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add budget"
            }
        }
    }

    fun updateBudgetPercentages(budgets: List<Budget>) {
        viewModelScope.launch {
            try {
                val totalIncome = _totalIncome.value
                budgetRepository.updateBudgetPercentages(budgets, totalIncome)
                loadBudgets()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update budget percentages"
            }
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                budgetRepository.updateBudget(budget)
                loadBudgets()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update budget"
            }
        }
    }

    fun deleteBudget(budgetId: String) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budgetId)
                loadBudgets()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete budget"
            }
        }
    }

    fun resetToDefaultSplit() {
        viewModelScope.launch {
            try {
                budgetRepository.resetToDefaultSplit()
                loadBudgets()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to reset to default split"
            }
        }
    }

    fun refreshTotalIncome() {
        viewModelScope.launch {
            try {
                val income = budgetRepository.getTotalMonthlyIncome().getOrThrow()
                _totalIncome.value = income
                loadBudgets() // Reload budgets with new income
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to refresh total income"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 