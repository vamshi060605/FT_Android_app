package com.vt.ft_android.model

import java.util.Date

/**
 * Budget - Data model representing a budget category in the Finance Tracker app
 * 
 * This model represents budget allocations for different spending categories:
 * - Percentage-based budget allocation
 * - Spending tracking and limits
 * - Category-specific budget management
 * - Progress monitoring and alerts
 * 
 * The model is used for:
 * - Budget planning and allocation
 * - Spending tracking against budgets
 * - Dashboard progress indicators
 * - Analytics and reporting
 * - Budget alerts and notifications
 * 
 * Budget System:
 * - Budgets are percentage-based (total = 100%)
 * - Each category gets a percentage of total income
 * - Spending is tracked against allocated amounts
 * - Progress bars show spending vs budget
 * - Alerts when approaching or exceeding limits
 * 
 * Default Categories:
 * - Food & Dining: 30% (default)
 * - Transportation: 20% (default)
 * - Shopping: 25% (default)
 * - Entertainment: 15% (default)
 * - Others: 10% (default)
 * 
 * @property id Unique budget identifier
 * @property userId User ID who owns this budget
 * @property categoryName Category name for this budget
 * @property categoryIcon Category icon for display
 * @property percentage Percentage allocation (0-100)
 * @property amount Calculated budget amount based on income
 * @property spent Amount spent in this category
 * @property period Budget period (weekly, monthly, yearly)
 * @property startDate Budget start date
 * @property endDate Budget end date
 * @property isActive Whether this budget is active
 * @property isDefault Whether this is a default budget category
 * @property createdAt Timestamp when budget was created
 * @property updatedAt Timestamp when budget was last modified
 */
data class Budget(
    val id: String = "",
    val userId: String = "",
    val categoryName: String = "",
    val categoryIcon: String = "",
    val percentage: Double = 0.0,
    val amount: Double = 0.0,
    val spent: Double = 0.0,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val isActive: Boolean = true,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Budget period enumeration
     */
    enum class BudgetPeriod {
        WEEKLY, MONTHLY, YEARLY
    }
    
    /**
     * Calculate remaining budget amount
     */
    val remaining: Double
        get() = amount - spent
    
    /**
     * Calculate progress percentage (0.0 to 1.0)
     */
    val progress: Double
        get() = if (amount > 0) (spent / amount) else 0.0
    
    /**
     * Get budget status based on progress
     */
    val status: String
        get() = when {
            progress >= 1.0 -> "Exceeded"
            progress >= 0.8 -> "Warning"
            else -> "Good"
        }
    
    /**
     * Get default budget categories
     */
    companion object {
        fun getDefaultBudgets(): List<Budget> {
            return listOf(
                Budget(
                    id = "needs",
                    categoryName = "Needs",
                    categoryIcon = "🏠",
                    percentage = 50.0,
                    isDefault = true
                ),
                Budget(
                    id = "wants", 
                    categoryName = "Wants",
                    categoryIcon = "🎯",
                    percentage = 30.0,
                    isDefault = true
                ),
                Budget(
                    id = "savings",
                    categoryName = "Savings", 
                    categoryIcon = "💰",
                    percentage = 20.0,
                    isDefault = true
                )
            )
        }
    }
} 