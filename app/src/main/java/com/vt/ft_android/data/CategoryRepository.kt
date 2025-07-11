package com.vt.ft_android.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vt.ft_android.model.Category
import kotlinx.coroutines.tasks.await
import java.util.Date

class CategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    private fun getCategoriesCollection() = 
        firestore.collection("users").document(getCurrentUserId()!!).collection("categories")
    
    // Initialize default categories for new user
    suspend fun initializeDefaultCategories(): Result<List<Category>> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            // Check if categories already exist
            val existingCategories = getCategories().getOrThrow()
            if (existingCategories.isNotEmpty()) {
                return Result.success(existingCategories)
            }
            
            // Create default categories
            val defaultCategories = Category.createDefaultCategories()
            defaultCategories.forEach { category ->
                addCategory(category)
            }
            
            Result.success(defaultCategories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Add category
    suspend fun addCategory(category: Category): Result<Category> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val categoryData = hashMapOf(
                "id" to category.id,
                "name" to category.name,
                "percentage" to category.percentage,
                "budget" to category.budget,
                "spent" to category.spent,
                "isDefault" to category.isDefault,
                "color" to category.color,
                "icon" to category.icon,
                "createdAt" to Date()
            )
            
            getCategoriesCollection().document(category.id).set(categoryData).await()
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get all categories
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val snapshot = getCategoriesCollection().get().await()
            val categories = snapshot.documents.mapNotNull { doc ->
                try {
                    Category(
                        id = doc.getString("id") ?: "",
                        name = doc.getString("name") ?: "",
                        percentage = doc.getDouble("percentage")?.toDouble() ?: 0.0,
                        budget = doc.getDouble("budget") ?: 0.0,
                        spent = doc.getDouble("spent") ?: 0.0,
                        isDefault = doc.getBoolean("isDefault") ?: false,
                        color = doc.getString("color") ?: "#9E9E9E",
                        icon = doc.getString("icon") ?: "📁"
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update category
    suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val categoryData = hashMapOf(
                "id" to category.id,
                "name" to category.name,
                "percentage" to category.percentage,
                "budget" to category.budget,
                "spent" to category.spent,
                "isDefault" to category.isDefault,
                "color" to category.color,
                "icon" to category.icon
            )
            
            getCategoriesCollection().document(category.id).update(categoryData.toMap()).await()
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Delete category (only non-default categories)
    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val category = getCategoryById(categoryId).getOrThrow()
            if (category?.isDefault == true) {
                throw Exception("Cannot delete default category")
            }
            
            getCategoriesCollection().document(categoryId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get category by ID
    suspend fun getCategoryById(categoryId: String): Result<Category?> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
            
            val doc = getCategoriesCollection().document(categoryId).get().await()
            
            if (doc.exists()) {
                val category = Category(
                    id = doc.getString("id") ?: "",
                    name = doc.getString("name") ?: "",
                    percentage = doc.getDouble("percentage")?.toDouble() ?: 0.0,
                    budget = doc.getDouble("budget") ?: 0.0,
                    spent = doc.getDouble("spent") ?: 0.0,
                    isDefault = doc.getBoolean("isDefault") ?: false,
                    color = doc.getString("color") ?: "#9E9E9E",
                    icon = doc.getString("icon") ?: "📁"
                )
                Result.success(category)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 