package com.vt.ft_android.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.vt.ft_android.model.*

object FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // region User
    fun getUser(id: String, onResult: (User?) -> Unit): ListenerRegistration {
        return db.collection("users").document(id)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject<User>()
                onResult(user)
            }
    }
    fun setUser(user: User, onResult: (Boolean) -> Unit) {
        db.collection("users").document(user.id)
            .set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    // endregion

    // region Transaction
    fun getTransactions(userId: String, onResult: (List<Transaction>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("transactions")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject<Transaction>() } ?: emptyList()
                onResult(list)
            }
    }
    fun addTransaction(userId: String, transaction: Transaction, onResult: (Boolean) -> Unit) {
        val doc = db.collection("users").document(userId)
            .collection("transactions").document()
        val tx = transaction.copy(id = doc.id)
        doc.set(tx)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    fun updateTransaction(userId: String, transaction: Transaction, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .collection("transactions").document(transaction.id)
            .set(transaction)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    fun deleteTransaction(userId: String, transactionId: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .collection("transactions").document(transactionId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    // endregion

    // region Budget
    fun getBudgets(userId: String, onResult: (List<Budget>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("budgets")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject<Budget>() } ?: emptyList()
                onResult(list)
            }
    }
    fun setBudget(userId: String, budget: Budget, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .collection("budgets").document(budget.id)
            .set(budget)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    // endregion

    // region Category
    fun getCategories(userId: String, onResult: (List<Category>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("categories")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject<Category>() } ?: emptyList()
                onResult(list)
            }
    }
    fun setCategory(userId: String, category: Category, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .collection("categories").document(category.id)
            .set(category)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    // endregion
} 