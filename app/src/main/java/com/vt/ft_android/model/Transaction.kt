package com.vt.ft_android.model

import java.util.Date

/**
 * Transaction - Data model representing a financial transaction in the Finance Tracker app
 * 
 * This model represents individual financial transactions including:
 * - Income (money received)
 * - Expenses (money spent)
 * - Transaction details and categorization
 * - Timestamps and metadata
 * 
 * The model is used throughout the app for:
 * - Transaction list display and management
 * - Budget calculations and tracking
 * - Analytics and reporting
 * - Data export functionality
 * - Dashboard summaries
 * 
 * Transaction Types:
 * - INCOME: Money received (salary, gifts, returns, etc.)
 * - EXPENSE: Money spent (purchases, bills, services, etc.)
 * 
 * Categories:
 * - Predefined categories for common transaction types
 * - Custom categories can be added by users
 * - Categories are used for budget allocation and analytics
 * 
 * @property id Unique transaction identifier
 * @property userId User ID who owns this transaction
 * @property title Transaction title/name
 * @property type Transaction type (INCOME or EXPENSE)
 * @property amount Transaction amount (positive value)
 * @property categoryId Category ID for classification
 * @property categoryName Category name for classification
 * @property categoryIcon Category icon for display
 * @property description Optional description of the transaction
 * @property date Date when transaction occurred
 * @property createdAt Timestamp when transaction was recorded
 * @property updatedAt Timestamp when transaction was last modified
 */
data class Transaction(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: Double = 0.0,
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryIcon: String = "",
    val description: String = "",
    val date: Date = Date(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * TransactionType - Enumeration of transaction types
 * 
 * Defines the two main types of financial transactions:
 * - INCOME: Money received (positive impact on balance)
 * - EXPENSE: Money spent (negative impact on balance)
 */
enum class TransactionType {
    INCOME,   // Money received (salary, gifts, returns, etc.)
    EXPENSE   // Money spent (purchases, bills, services, etc.)
} 