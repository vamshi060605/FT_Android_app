package com.vt.ft_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vt.ft_android.model.Category
import com.vt.ft_android.model.TransactionType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: TransactionViewModel = viewModel()
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var editPercentages by remember { mutableStateOf(false) }
    var editedCategories by remember { mutableStateOf<List<Category>>(emptyList()) }

    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadTransactions()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF667eea),
            Color(0xFF764ba2)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Budget",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row {
                        IconButton(onClick = { showAddCategoryDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Category",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { 
                            // Reset to default categories
                            val defaultCategories = Category.createDefaultCategories()
                            defaultCategories.forEach { category ->
                                viewModel.updateCategory(category)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Reset to Default",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Budget Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        val transactions by viewModel.transactions.collectAsState()
                        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        val totalBudget = categories.sumOf { (it.percentage / 100.0) * totalIncome }
                        val totalSpent = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                        val progress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f
                        
                        // Income Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Income",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "₹${String.format("%.2f", totalIncome)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Income",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF2E7D32)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Monthly Budget",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${String.format("%.2f", totalBudget)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Budget",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF667eea)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = when {
                                progress >= 1.0 -> Color(0xFFE57373) // Red for exceeded
                                progress >= 0.8 -> Color(0xFFFFB74D) // Orange for warning
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: ₹${String.format("%.2f", totalSpent)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Remaining: ₹${String.format("%.2f", totalBudget - totalSpent)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Categories List
            items(categories.size) { idx ->
                val category = categories[idx]
                var percent by remember { mutableStateOf(category.percentage.toDouble().toString()) }
                
                val transactions by viewModel.transactions.collectAsState()
                val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                
                // Calculate spent amount for this category
                val categorySpent = transactions
                    .filter { it.type == TransactionType.EXPENSE && it.categoryName == category.name }
                    .sumOf { it.amount }
                
                val categoryBudget = (category.percentage / 100.0) * totalIncome
                val categoryProgress = if (categoryBudget > 0) (categorySpent / categoryBudget).toFloat() else 0f
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.icon,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "₹${String.format("%.2f", categoryBudget)}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Spent: ₹${String.format("%.2f", categorySpent)}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedTextField(
                                value = percent,
                                onValueChange = {
                                    percent = it.filter { c -> c.isDigit() || c == '.' }
                                    val newPercent = percent.toFloatOrNull() ?: 0f
                                    val updated = categories.toMutableList()
                                    updated[idx] = category.copy(percentage = newPercent.toDouble())
                                    // Auto-adjust others to keep total 100%
                                    val totalOther = updated.filterIndexed { i, _ -> i != idx }.sumOf { it.percentage }
                                    val remaining = 100.0 - newPercent.toDouble()
                                    if (totalOther > 0) {
                                        val scale = if (remaining > 0) remaining / totalOther else 0.0
                                        updated.forEachIndexed { i, c ->
                                            if (i != idx) {
                                                updated[i] = c.copy(percentage = (c.percentage * scale).coerceAtLeast(0.0))
                                            }
                                        }
                                    }
                                    editedCategories = updated
                                    editPercentages = true
                                },
                                label = { Text("%", color = Color.Gray) },
                                singleLine = true,
                                modifier = Modifier.width(64.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF667eea),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                textStyle = LocalTextStyle.current.copy(color = Color.Black)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (!category.isDefault) {
                                IconButton(onClick = { showDeleteDialog = category.id }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFF44336)
                                    )
                                }
                            }
                        }
                        
                        // Category progress bar
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { categoryProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = when {
                                categoryProgress >= 1.0 -> Color(0xFFE57373) // Red for exceeded
                                categoryProgress >= 0.8 -> Color(0xFFFFB74D) // Orange for warning
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        
                        // Progress percentage text
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%.1f", categoryProgress * 100)}% used",
                            fontSize = 10.sp,
                            color = when {
                                categoryProgress >= 1.0 -> Color(0xFFE57373)
                                categoryProgress >= 0.8 -> Color(0xFFFFB74D)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Save Changes Button
            if (editPercentages) {
                val percentSum = editedCategories.sumOf { it.percentage.toDouble() }
                item {
                    if (percentSum != 100.0) {
                        Text(
                            text = "Percentages must sum to 100% (Current: ${String.format("%.1f", percentSum)}%)",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            // Update categories with new percentages
                            editedCategories.forEach { category ->
                                viewModel.updateCategory(category)
                            }
                            editPercentages = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        enabled = percentSum == 100.0
                    ) {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Add Category Dialog
        if (showAddCategoryDialog) {
            AddCategoryDialog(
                onDismiss = { showAddCategoryDialog = false },
                onAddCategory = { categoryName, categoryIcon, percentage ->
                    val newCategory = Category(
                        id = java.util.UUID.randomUUID().toString(),
                        name = categoryName,
                        icon = categoryIcon,
                        percentage = percentage.toDouble(),
                        isDefault = false
                    )
                    viewModel.addCategory(newCategory)
                    showAddCategoryDialog = false
                }
            )
        }

        // Delete Category Dialog
        showDeleteDialog?.let { categoryId ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Category") },
                text = { Text("Are you sure you want to delete this category? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteCategory(categoryId)
                            showDeleteDialog = null
                        }
                    ) {
                        Text("Delete", color = Color(0xFFF44336))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAddCategory: (String, String, Double) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var categoryIcon by remember { mutableStateOf("") }
    var percentage by remember { mutableStateOf("") }
    val iconOptions = listOf("🏠", "🎯", "💰", "🍔", "🚗", "📚", "🎉", "🛒")
    var showIconPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category") },
        text = {
            Column {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Icon:", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = if (categoryIcon.isNotBlank()) categoryIcon else "Pick",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .clickable { showIconPicker = true }
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = percentage,
                    onValueChange = { percentage = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Percentage (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showIconPicker) {
                    AlertDialog(
                        onDismissRequest = { showIconPicker = false },
                        title = { Text("Pick an Icon") },
                        text = {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                iconOptions.forEach { icon ->
                                    Text(
                                        text = icon,
                                        fontSize = 28.sp,
                                        modifier = Modifier
                                            .clickable {
                                                categoryIcon = icon
                                                showIconPicker = false
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {}
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val percentValue = percentage.toDoubleOrNull() ?: 0.0
                    if (categoryName.isNotBlank() && categoryIcon.isNotBlank() && percentValue > 0) {
                        onAddCategory(categoryName, categoryIcon, percentValue)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 