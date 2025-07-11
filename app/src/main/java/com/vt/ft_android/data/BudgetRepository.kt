package com.vt.ft_android.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vt.ft_android.model.Budget
import com.vt.ft_android.model.Transaction
import com.vt.ft_android.model.TransactionType
import kotlinx.coroutines.tasks.await
import java.util.Date

class BudgetRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val transactionRepository = TransactionRepository()
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    private fun getBudgetsCollection() = 
        firestore.collection("users").document(getCurrentUserId()!!).collection("budgets")
    
    // Initialize default budgets for new user
    suspend fun initializeDefaultBudgets(): Result<List<Budget>> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            // Check if budgets already exist
            val existingBudgets = getBudgets().getOrThrow()
            if (existingBudgets.isNotEmpty()) {
                return Result.success(existingBudgets)
            }
            
            // Create default budgets
            val defaultBudgets = Budget.getDefaultBudgets()
            defaultBudgets.forEach { budget ->
                addBudget(budget)
            }
            
            Result.success(defaultBudgets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Create budget
    suspend fun addBudget(budget: Budget): Result<Budget> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val budgetData = hashMapOf(
                "id" to budget.id,
                "categoryName" to budget.categoryName,
                "categoryIcon" to budget.categoryIcon,
                "percentage" to budget.percentage,
                "amount" to budget.amount,
                "spent" to budget.spent,
                "period" to budget.period.name,
                "createdAt" to Date(),
                "updatedAt" to Date()
            )
            
            getBudgetsCollection().document(budget.id).set(budgetData).await()
            Result.success(budget)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get all budgets
    suspend fun getBudgets(): Result<List<Budget>> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val snapshot = getBudgetsCollection().get().await()
            val budgets = snapshot.documents.mapNotNull { doc ->
                try {
                    Budget(
                        id = doc.getString("id") ?: "",
                        categoryName = doc.getString("categoryName") ?: "",
                        categoryIcon = doc.getString("categoryIcon") ?: "",
                        percentage = doc.getDouble("percentage") ?: 0.0,
                        amount = doc.getDouble("amount") ?: 0.0,
                        spent = doc.getDouble("spent") ?: 0.0,
                        period = Budget.BudgetPeriod.valueOf(doc.getString("period") ?: "MONTHLY"),
                        isDefault = doc.getBoolean("isDefault") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(budgets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update budget percentages and recalculate amounts
    suspend fun updateBudgetPercentages(budgets: List<Budget>, totalIncome: Double): Result<List<Budget>> {
        return try {
            val updatedBudgets = budgets.map { budget ->
                budget.copy(amount = (budget.percentage / 100.0) * totalIncome)
            }
            
            // Update all budgets in Firestore
            updatedBudgets.forEach { budget ->
                updateBudget(budget)
            }
            
            Result.success(updatedBudgets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update budget
    suspend fun updateBudget(budget: Budget): Result<Budget> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val budgetData = hashMapOf(
                "id" to budget.id,
                "categoryName" to budget.categoryName,
                "categoryIcon" to budget.categoryIcon,
                "percentage" to budget.percentage,
                "amount" to budget.amount,
                "spent" to budget.spent,
                "period" to budget.period.name,
                "updatedAt" to Date()
            )
            
            getBudgetsCollection().document(budget.id).update(budgetData.toMap()).await()
            Result.success(budget)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Delete budget (only non-default budgets)
    suspend fun deleteBudget(budgetId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val budget = getBudgetById(budgetId).getOrThrow()
            if (budget?.isDefault == true) {
                throw Exception("Cannot delete default budget")
            }
            
            getBudgetsCollection().document(budgetId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get budget by ID
    suspend fun getBudgetById(budgetId: String): Result<Budget?> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val doc = getBudgetsCollection().document(budgetId).get().await()
            
            if (doc.exists()) {
                val budget = Budget(
                    id = doc.getString("id") ?: "",
                    categoryName = doc.getString("categoryName") ?: "",
                    categoryIcon = doc.getString("categoryIcon") ?: "",
                    percentage = doc.getDouble("percentage") ?: 0.0,
                    amount = doc.getDouble("amount") ?: 0.0,
                    spent = doc.getDouble("spent") ?: 0.0,
                    period = Budget.BudgetPeriod.valueOf(doc.getString("period") ?: "MONTHLY"),
                    isDefault = doc.getBoolean("isDefault") ?: false
                )
                Result.success(budget)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Calculate total monthly income
    suspend fun getTotalMonthlyIncome(): Result<Double> {
        return try {
            val currentDate = Date()
            val startOfMonth = Date(currentDate.time - (30L * 24 * 60 * 60 * 1000)) // 30 days ago
            
            val transactions = transactionRepository.getTransactions(
                startDate = startOfMonth,
                endDate = currentDate
            ).getOrThrow()
            
            val totalIncome = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            
            Result.success(totalIncome)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update budget spent amount based on transactions
    suspend fun updateBudgetSpending(budgetId: String): Result<Double> {
        return try {
            val budget = getBudgetById(budgetId).getOrThrow() ?: throw Exception("Budget not found")
            
            // Calculate budget period dates
            val currentDate = Date()
            val startDate = when (budget.period) {
                Budget.BudgetPeriod.WEEKLY -> Date(currentDate.time - (7L * 24 * 60 * 60 * 1000))
                Budget.BudgetPeriod.MONTHLY -> Date(currentDate.time - (30L * 24 * 60 * 60 * 1000))
                Budget.BudgetPeriod.YEARLY -> Date(currentDate.time - (365L * 24 * 60 * 60 * 1000))
            }
            
            // Get transactions for this category within budget period
            val transactions = transactionRepository.getTransactions(
                categoryName = budget.categoryName,
                startDate = startDate,
                endDate = currentDate
            ).getOrThrow()
            
            val totalSpent = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            
            // Update budget with new spent amount
            val updatedBudget = budget.copy(spent = totalSpent)
            updateBudget(updatedBudget)
            
            Result.success(totalSpent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Reset to default 50/30/20 split
    suspend fun resetToDefaultSplit(): Result<List<Budget>> {
        return try {
            val defaultBudgets = Budget.getDefaultBudgets()
            val totalIncome = getTotalMonthlyIncome().getOrThrow()
            
            val updatedBudgets = defaultBudgets.map { budget ->
                budget.copy(amount = (budget.percentage / 100.0) * totalIncome)
            }
            
            // Update all budgets in Firestore
            updatedBudgets.forEach { budget ->
                updateBudget(budget)
            }
            
            Result.success(updatedBudgets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 