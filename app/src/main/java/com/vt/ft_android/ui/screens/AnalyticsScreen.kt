package com.vt.ft_android.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vt.ft_android.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    navController: NavController,
    viewModel: TransactionViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTransactions()
        viewModel.loadCategories()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A237E),
            Color(0xFF0D47A1),
            Color(0xFF01579B)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
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
                            text = "Analytics",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Summary Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                        val netAmount = totalIncome - totalExpense

                        SummaryCard(
                            title = "Total Income",
                            amount = totalIncome,
                            icon = Icons.Default.KeyboardArrowUp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Total Expenses",
                            amount = totalExpense,
                            icon = Icons.Default.KeyboardArrowDown,
                            color = Color(0xFFF44336),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    val netAmount = totalIncome - totalExpense

                    SummaryCard(
                        title = "Net Savings",
                        amount = netAmount,
                        icon = Icons.Default.AccountBalance,
                        color = if (netAmount >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Spending Distribution by Category
                item {
                    SpendingDistributionCard(transactions = transactions, categories = categories)
                }

                // Monthly Trends
                item {
                    MonthlyTrendsCard(transactions = transactions)
                }

                // Category Analysis
                item {
                    CategoryAnalysisCard(transactions = transactions, categories = categories)
                }

                // Insights
                item {
                    InsightsCard(transactions = transactions)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹${String.format("%.0f", amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SpendingDistributionCard(
    transactions: List<com.vt.ft_android.model.Transaction>,
    categories: List<com.vt.ft_android.model.Category>
) {
    val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
    val totalExpense = expenseTransactions.sumOf { it.amount }
    
    val categoryData = categories.map { category ->
        val categorySpent = expenseTransactions
            .filter { it.categoryName == category.name }
            .sumOf { it.amount }
        val percentage = if (totalExpense > 0) (categorySpent / totalExpense) * 100 else 0.0
        Triple(category.name, percentage.toFloat(), category.icon)
    }.filter { it.second > 0 }.sortedByDescending { it.second }

    val colors = listOf(
        Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6),
        Color(0xFFFFB74D), Color(0xFFBA68C8), Color(0xFF4DB6AC)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Spending Distribution",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (categoryData.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(
                        percentages = categoryData.map { it.second },
                        colors = colors.take(categoryData.size)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryData.forEachIndexed { index, (name, percentage, icon) ->
                        LegendItem(
                            icon = icon,
                            name = name,
                            percentage = percentage,
                            color = colors[index % colors.size]
                        )
                    }
                }
            } else {
                Text(
                    text = "No expense data available",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MonthlyTrendsCard(transactions: List<com.vt.ft_android.model.Transaction>) {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val monthlyData = (0..5).map { monthOffset ->
        calendar.set(currentYear, currentMonth - monthOffset, 1)
        val startOfMonth = calendar.time
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endOfMonth = calendar.time

        val monthTransactions = transactions.filter { 
            it.date >= startOfMonth && it.date <= endOfMonth 
        }
        val income = monthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = monthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(startOfMonth)
        Triple(monthName, income.toFloat(), expense.toFloat())
    }.reversed()

    val maxAmount = monthlyData.maxOfOrNull { maxOf(it.second, it.third) } ?: 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Monthly Trends",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                monthlyData.forEach { (month, income, expense) ->
                    MonthlyTrendItem(
                        month = month,
                        income = income,
                        expense = expense,
                        maxAmount = maxAmount
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryAnalysisCard(
    transactions: List<com.vt.ft_android.model.Transaction>,
    categories: List<com.vt.ft_android.model.Category>
) {
    val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
    val totalExpense = expenseTransactions.sumOf { it.amount }

    val categoryAnalysis = categories.map { category ->
        val categoryTransactions = expenseTransactions.filter { it.categoryName == category.name }
        val totalSpent = categoryTransactions.sumOf { it.amount }
        val avgAmount = if (categoryTransactions.isNotEmpty()) totalSpent / categoryTransactions.size else 0.0
        val percentage = if (totalExpense > 0) (totalSpent / totalExpense) * 100 else 0.0

        CategoryAnalysisItem(
            name = category.name,
            icon = category.icon,
            totalSpent = totalSpent,
            avgAmount = avgAmount,
            percentage = percentage,
            transactionCount = categoryTransactions.size
        )
    }.filter { it.totalSpent > 0 }.sortedByDescending { it.totalSpent }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Category Analysis",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (categoryAnalysis.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categoryAnalysis.forEach { item ->
                        CategoryAnalysisRow(item = item)
                    }
                }
            } else {
                Text(
                    text = "No category data available",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun InsightsCard(transactions: List<com.vt.ft_android.model.Transaction>) {
    val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
    val incomeTransactions = transactions.filter { it.type == TransactionType.INCOME }
    
    val totalExpense = expenseTransactions.sumOf { it.amount }
    val totalIncome = incomeTransactions.sumOf { it.amount }
    val netAmount = totalIncome - totalExpense
    
    val avgExpense = if (expenseTransactions.isNotEmpty()) totalExpense / expenseTransactions.size else 0.0
    val avgIncome = if (incomeTransactions.isNotEmpty()) totalIncome / incomeTransactions.size else 0.0
    
    val insights = mutableListOf<InsightItem>()
    
    if (netAmount < 0) {
        insights.add(InsightItem("⚠️", "Negative Net Savings", "Your expenses exceed your income. Consider reducing spending."))
    } else if (netAmount > 0) {
        insights.add(InsightItem("✅", "Positive Savings", "Great job! You're saving money consistently."))
    }
    
    if (expenseTransactions.isNotEmpty()) {
        val highestExpense = expenseTransactions.maxByOrNull { it.amount }
        if (highestExpense != null) {
            insights.add(InsightItem("💰", "Highest Expense", "${highestExpense.title}: ₹${String.format("%.0f", highestExpense.amount)}"))
        }
    }
    
    if (incomeTransactions.isNotEmpty()) {
        val highestIncome = incomeTransactions.maxByOrNull { it.amount }
        if (highestIncome != null) {
            insights.add(InsightItem("📈", "Highest Income", "${highestIncome.title}: ₹${String.format("%.0f", highestIncome.amount)}"))
        }
    }
    
    if (avgExpense > 0) {
        insights.add(InsightItem("📊", "Average Expense", "₹${String.format("%.0f", avgExpense)} per transaction"))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Insights",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (insights.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    insights.forEach { insight ->
                        InsightRow(insight = insight)
                    }
                }
            } else {
                Text(
                    text = "Add more transactions to see insights",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PieChart(
    percentages: List<Float>,
    colors: List<Color>
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        var startAngle = 0f
        percentages.forEachIndexed { index, percentage ->
            val sweepAngle = (percentage / 100f) * 360f
            
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
    }
}

@Composable
fun LegendItem(
    icon: String,
    name: String,
    percentage: Float,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${String.format("%.1f", percentage)}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun MonthlyTrendItem(
    month: String,
    income: Float,
    expense: Float,
    maxAmount: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            month,
            modifier = Modifier.width(40.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Income bar
            if (income > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(income / maxAmount)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF4CAF50))
                    )
                }
                Text(
                    "Income: ₹${String.format("%.0f", income)}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Expense bar
            if (expense > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(expense / maxAmount)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF44336))
                    )
                }
                Text(
                    "Expense: ₹${String.format("%.0f", expense)}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

data class CategoryAnalysisItem(
    val name: String,
    val icon: String,
    val totalSpent: Double,
    val avgAmount: Double,
    val percentage: Double,
    val transactionCount: Int
)

@Composable
fun CategoryAnalysisRow(item: CategoryAnalysisItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${item.transactionCount} transactions",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${String.format("%.0f", item.totalSpent)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${String.format("%.1f", item.percentage)}%",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

data class InsightItem(
    val icon: String,
    val title: String,
    val description: String
)

@Composable
fun InsightRow(insight: InsightItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = insight.icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = insight.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = insight.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
} 