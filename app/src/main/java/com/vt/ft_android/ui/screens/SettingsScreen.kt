package com.vt.ft_android.ui.screens

import com.vt.ft_android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vt.ft_android.data.UserRepository
import com.vt.ft_android.model.User
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import com.vt.ft_android.ui.theme.ThemeManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.unit.sp

/**
 * SettingsScreen - Comprehensive settings and profile management screen
 * 
 * This screen provides:
 * - User profile display and editing
 * - Theme preferences (dark/light mode)
 * - Notification settings
 * - Data export functionality
 * - Support and help links
 * - About app information
 * - Account management (logout)
 * 
 * Features:
 * - Scrollable layout with organized sections
 * - Edit profile dialog with avatar selection
 * - Real-time theme switching
 * - CSV data export with file sharing
 * - External link handling (email, web, social media)
 * - Confirmation dialogs for important actions
 * 
 * @param navController Navigation controller for screen navigation
 * @param viewModel SettingsViewModel for data management
 */
@Composable
fun SettingsScreen(
    navController: NavController? = null,
    viewModel: SettingsViewModel = viewModel()
) {
    // Data repositories and context
    val userRepository = remember { UserRepository() }
    val currentUserId = userRepository.getCurrentUserId()
    val context = LocalContext.current
    
    // Collect user data from ViewModel
    val user by viewModel.user.collectAsState()
    
    // Initialize user data when screen loads
    LaunchedEffect(currentUserId) {
        currentUserId?.let { uid ->
            viewModel.loadUser(uid)
        }
    }

    // Dialog state management
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    
    // Edit profile state variables
    var editName by remember { mutableStateOf("") }
    var editCurrency by remember { mutableStateOf("INR") }
    var editAvatar by remember { mutableStateOf("avatar1") }
    
    // Update local state when user data changes
    LaunchedEffect(user) {
        user?.let {
            editName = it.name
            editCurrency = it.currency
            editAvatar = it.avatar
        }
    }

    // Main screen layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Header Card - Shows user avatar, name, email, and currency
            item {
                ProfileHeaderCard(user = user)
            }

            // Profile Card - Displays user information (read-only)
            item {
                ProfileInfoCard(user = user)
            }
            
            // Preferences Card - Theme and notification settings
            item {
                PreferencesCard(
                    user = user,
                    currentUserId = currentUserId,
                    viewModel = viewModel
                )
            }
            
            // Data & Export Card - Data export functionality
            item {
                DataExportCard(
                    onExportData = { viewModel.exportData(context) }
                )
            }
            
            // Support Card - Help, contact, and external links
            item {
                SupportCard(context = context)
            }
            
            // About Card - App information and links
            item {
                AboutCard(
                    context = context,
                    onShowAbout = { showAboutDialog = true }
                )
            }
            
            // Edit Profile Button - Opens comprehensive edit dialog
            item {
                EditProfileButton(
                    onClick = { showEditProfileDialog = true }
                )
            }
            
            // Account Card - Logout functionality
            item {
                AccountCard(
                    onLogout = { showLogoutDialog = true }
                )
            }
            
            // Bottom spacing for better UX
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    // Edit Profile Dialog - Comprehensive profile editing
    if (showEditProfileDialog) {
        EditProfileDialog(
            editName = editName,
            onEditNameChange = { editName = it },
            editCurrency = editCurrency,
            onEditCurrencyChange = { editCurrency = it },
            editAvatar = editAvatar,
            onEditAvatarChange = { editAvatar = it },
            onSave = {
                currentUserId?.let { uid ->
                    viewModel.updateUserProfile(uid, editName, editCurrency, editAvatar)
                }
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                viewModel.logout()
                navController?.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
    
    // About Dialog - App information
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}

/**
 * Profile Header Card - Displays user avatar, name, email, and currency
 * 
 * @param user Current user data
 */
@Composable
private fun ProfileHeaderCard(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar display with dynamic resource loading
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Map avatar string to drawable resource
                    val avatarResource = when (user?.avatar) {
                        "avatar1" -> R.drawable.avatar1
                        "avatar2" -> R.drawable.avatar2
                        "avatar3" -> R.drawable.avatar3
                        "avatar4" -> R.drawable.avatar4
                        "avatar5" -> R.drawable.avatar5
                        "avatar6" -> R.drawable.avatar6
                        "avatar7" -> R.drawable.avatar7
                        "avatar8" -> R.drawable.avatar8
                        else -> R.drawable.avatar1
                    }
                    Image(
                        painter = painterResource(id = avatarResource),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User name
            Text(
                user?.name ?: "Anonymous",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // User email
            Text(
                user?.email ?: "No email",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // User currency
            Text(
                "Currency: ${user?.currency ?: "INR"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Profile Info Card - Displays user information in read-only format
 * 
 * @param user Current user data
 */
@Composable
private fun ProfileInfoCard(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Display user information items
            ProfileItem(
                icon = "👤",
                title = "Name",
                value = user?.name ?: "Anonymous"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ProfileItem(
                icon = "📧",
                title = "Email",
                value = user?.email ?: "No email"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ProfileItem(
                icon = "💱",
                title = "Currency",
                value = user?.currency ?: "INR"
            )
        }
    }
}

/**
 * Preferences Card - Theme and notification settings
 * 
 * @param user Current user data
 * @param currentUserId Current user ID for updates
 * @param viewModel SettingsViewModel for data updates
 */
@Composable
private fun PreferencesCard(
    user: User?,
    currentUserId: String?,
    viewModel: SettingsViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Preferences",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dark Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Theme icon
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌙", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Theme description
                    Column {
                        Text(
                            "Dark Theme",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Use dark mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Theme toggle switch
                Switch(
                    checked = ThemeManager.isDarkMode,
                    onCheckedChange = { isDark ->
                        ThemeManager.setTheme(isDark)
                        currentUserId?.let { uid ->
                            viewModel.updateUserTheme(uid, if (isDark) "dark" else "light")
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notifications Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification icon
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔔", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Notification description
                    Column {
                        Text(
                            "Notifications",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Get budget alerts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Notification toggle switch
                Switch(
                    checked = user?.notifications == true,
                    onCheckedChange = { enabled ->
                        currentUserId?.let { uid ->
                            viewModel.updateUserNotifications(uid, enabled)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

/**
 * Data Export Card - CSV export functionality
 * 
 * @param onExportData Callback for data export
 */
@Composable
private fun DataExportCard(onExportData: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Data & Export",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SupportItem(
                icon = "📊",
                title = "Export Data",
                description = "Download your financial data as CSV",
                onClick = onExportData
            )
        }
    }
}

/**
 * Support Card - Help, contact, and external links
 * 
 * @param context Android context for intent handling
 */
@Composable
private fun SupportCard(context: android.content.Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Support",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Support items with external links
            SupportItem(
                icon = "❓",
                title = "Help & FAQ",
                description = "Get help and find answers",
                onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://finance-tracker-app.com/help"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            SupportItem(
                icon = "📧",
                title = "Contact Support",
                description = "Get in touch with our team",
                onClick = { 
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@finance-tracker-app.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Finance Tracker App Support")
                    }
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SupportItem(
                icon = "⭐",
                title = "Rate App",
                description = "Rate us on Google Play",
                onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vt.ft_android"))
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SupportItem(
                icon = "📄",
                title = "Privacy Policy",
                description = "Read our privacy policy",
                onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://finance-tracker-app.com/privacy"))
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SupportItem(
                icon = "📋",
                title = "Terms of Service",
                description = "Read our terms of service",
                onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://finance-tracker-app.com/terms"))
                    context.startActivity(intent)
                }
            )
        }
    }
}

/**
 * About Card - App information and links
 * 
 * @param context Android context for intent handling
 * @param onShowAbout Callback to show about dialog
 */
@Composable
private fun AboutCard(
    context: android.content.Context,
    onShowAbout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "About",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SupportItem(
                icon = "ℹ️",
                title = "App Version",
                description = "Version 1.0.0",
                onClick = onShowAbout
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SupportItem(
                icon = "🔗",
                title = "Website",
                description = "Visit our website",
                onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://finance-tracker-app.com"))
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SupportItem(
                icon = "🐦",
                title = "Follow Us",
                description = "Follow us on social media",
                onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/financetracker"))
                    context.startActivity(intent)
                }
            )
        }
    }
}

/**
 * Edit Profile Button - Opens comprehensive edit dialog
 * 
 * @param onClick Callback when button is clicked
 */
@Composable
private fun EditProfileButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text("✏️", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Edit Profile",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Account Card - Logout functionality
 * 
 * @param onLogout Callback when logout is requested
 */
@Composable
private fun AccountCard(onLogout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE57373).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                )
            ) {
                Text("🚪", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Logout",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Edit Profile Dialog - Comprehensive profile editing
 * 
 * @param editName Current name value
 * @param onEditNameChange Callback for name changes
 * @param editCurrency Current currency value
 * @param onEditCurrencyChange Callback for currency changes
 * @param editAvatar Current avatar value
 * @param onEditAvatarChange Callback for avatar changes
 * @param onSave Callback when save is clicked
 * @param onDismiss Callback when dialog is dismissed
 */
@Composable
private fun EditProfileDialog(
    editName: String,
    onEditNameChange: (String) -> Unit,
    editCurrency: String,
    onEditCurrencyChange: (String) -> Unit,
    editAvatar: String,
    onEditAvatarChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar Selection
                Text(
                    "Choose Avatar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    val avatars = listOf("avatar1", "avatar2", "avatar3", "avatar4", "avatar5", "avatar6", "avatar7", "avatar8")
                    items(avatars) { avatar ->
                        val avatarResource = when (avatar) {
                            "avatar1" -> R.drawable.avatar1
                            "avatar2" -> R.drawable.avatar2
                            "avatar3" -> R.drawable.avatar3
                            "avatar4" -> R.drawable.avatar4
                            "avatar5" -> R.drawable.avatar5
                            "avatar6" -> R.drawable.avatar6
                            "avatar7" -> R.drawable.avatar7
                            "avatar8" -> R.drawable.avatar8
                            else -> R.drawable.avatar1
                        }
                        
                        Card(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .clickable { onEditAvatarChange(avatar) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (editAvatar == avatar) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surface
                            ),
                            border = if (editAvatar == avatar) 
                                androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                            else null
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = avatarResource),
                                    contentDescription = "Avatar $avatar",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
                
                // Name Input
                OutlinedTextField(
                    value = editName,
                    onValueChange = onEditNameChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Currency Selection
                var currencyExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = editCurrency,
                        onValueChange = { },
                        label = { Text("Currency") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { currencyExpanded = true }) {
                                Text("▼")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = currencyExpanded,
                        onDismissRequest = { currencyExpanded = false }
                    ) {
                        listOf("INR", "USD", "EUR", "GBP", "JPY").forEach { currency ->
                            DropdownMenuItem(
                                text = { Text(currency) },
                                onClick = {
                                    onEditCurrencyChange(currency)
                                    currencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Logout Confirmation Dialog
 * 
 * @param onConfirm Callback when logout is confirmed
 * @param onDismiss Callback when dialog is dismissed
 */
@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Logout",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to logout? You will need to sign in again.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                )
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * About Dialog - App information
 * 
 * @param onDismiss Callback when dialog is dismissed
 */
@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "About Finance Tracker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Version: 1.0.0")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Build: 2024.1.0")
                Spacer(modifier = Modifier.height(8.dp))
                Text("A modern finance tracking app built with Jetpack Compose and Firebase.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Features:")
                Text("• Track income and expenses")
                Text("• Set and monitor budgets")
                Text("• View detailed analytics")
                Text("• Secure cloud sync")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

/**
 * Profile Item - Displays a single profile information item
 * 
 * @param icon Emoji icon for the item
 * @param title Item title
 * @param value Item value
 */
@Composable
private fun ProfileItem(
    icon: String,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Support Item - Displays a single support item with click action
 * 
 * @param icon Emoji icon for the item
 * @param title Item title
 * @param description Item description
 * @param onClick Click callback
 */
@Composable
private fun SupportItem(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
} 

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
} 