package com.vt.ft_android.model

/**
 * Category - Data model representing transaction categories in the Finance Tracker app
 * 
 * This model defines categories for organizing and classifying transactions:
 * - Predefined categories for common transaction types
 * - Custom categories that users can create
 * - Category icons and colors for visual identification
 * - Category types (income vs expense categories)
 * 
 * The model is used for:
 * - Transaction categorization and organization
 * - Budget allocation and tracking
 * - Analytics and reporting
 * - UI display with icons and colors
 * - Data filtering and grouping
 * 
 * Category System:
 * - Each transaction must be assigned to a category
 * - Categories help users understand spending patterns
 * - Categories are used for budget allocation
 * - Analytics show spending by category
 * - Icons and colors improve visual recognition
 * 
 * Default Categories:
 * Income Categories:
 * - Salary: 💰
 * - Freelance: 💼
 * - Investment: 📈
 * - Gift: 🎁
 * - Other Income: 💵
 * 
 * Expense Categories:
 * - Food & Dining: 🍽️
 * - Transportation: 🚗
 * - Shopping: 🛍️
 * - Entertainment: 🎬
 * - Healthcare: 🏥
 * - Education: 📚
 * - Utilities: ⚡
 * - Others: 📦
 * 
 * @property id Unique category identifier
 * @property name Category name
 * @property percentage Budget percentage allocation
 * @property budget Budget amount for this category
 * @property spent Amount spent in this category
 * @property icon Emoji icon for the category
 * @property color Color code for the category
 * @property type Category type (INCOME or EXPENSE)
 * @property isDefault Whether this is a default category
 * @property createdAt Timestamp when category was created
 * @property updatedAt Timestamp when category was last modified
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val percentage: Double = 0.0,
    val budget: Double = 0.0,
    val spent: Double = 0.0,
    val icon: String = "",
    val color: String = "#1976D2",
    val type: CategoryType = CategoryType.EXPENSE,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Create default categories for new users
     */
    companion object {
        fun createDefaultCategories(): List<Category> {
            return listOf(
                Category(
                    name = "Needs",
                    percentage = 50.0,
                    budget = 0.0,
                    spent = 0.0,
                    isDefault = true,
                    color = "#E57373",
                    icon = "🏠"
                ),
                Category(
                    name = "Wants",
                    percentage = 30.0,
                    budget = 0.0,
                    spent = 0.0,
                    isDefault = true,
                    color = "#81C784",
                    icon = "🎯"
                ),
                Category(
                    name = "Savings",
                    percentage = 20.0,
                    budget = 0.0,
                    spent = 0.0,
                    isDefault = true,
                    color = "#64B5F6",
                    icon = "💰"
                )
            )
        }
    }
}

/**
 * CategoryType - Enumeration of category types
 * 
 * Defines the two main types of categories:
 * - INCOME: Categories for income transactions
 * - EXPENSE: Categories for expense transactions
 */
enum class CategoryType {
    INCOME,   // Categories for income transactions
    EXPENSE   // Categories for expense transactions
} 