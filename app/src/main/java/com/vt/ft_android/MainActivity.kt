package com.vt.ft_android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vt.ft_android.ui.theme.FTAndroidTheme
import com.vt.ft_android.ui.theme.ThemeManager
import com.vt.ft_android.ui.screens.*
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * MainActivity - Entry point of the Finance Tracker Android App
 * 
 * This activity handles:
 * - App initialization and theme setup
 * - Google Sign-In integration
 * - Navigation between different screens (Onboarding -> Auth -> Main App)
 * - Navigation drawer and top app bar
 * - State management for authentication flow
 * 
 * App Flow:
 * 1. Onboarding Screen (first time users)
 * 2. Authentication Screen (email/password or Google Sign-In)
 * 3. Main App with Navigation Drawer (Dashboard, Transactions, Budget, Analytics, Settings)
 */
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    // Google Sign-In client for handling Google authentication
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display for modern Android UI
        enableEdgeToEdge()
        
        // Initialize Google Sign-In components
        initializeGoogleSignIn()

        setContent {
            // State management for app flow
            var showOnboarding by remember { mutableStateOf(true) }
            var isAuthenticated by remember { mutableStateOf(false) }
            var googleSignInCallback by remember { mutableStateOf<((String) -> Unit)?>(null) }
            
            // Google Sign-In launcher for handling authentication result
            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val idToken = account.idToken
                        if (idToken != null) {
                            googleSignInCallback?.invoke(idToken)
                        }
                    } catch (e: ApiException) {
                        // Handle sign-in failure
                        Log.e("MainActivity", "Google sign-in failed", e)
                    }
                }
            }

            // Apply the app theme (light/dark mode)
            FTAndroidTheme {
                // App flow logic based on user state
                when {
                    // Show onboarding for first-time users
                    showOnboarding -> {
                        OnboardingScreen(onFinish = { showOnboarding = false })
                    }
                    // Show authentication screen for unauthenticated users
                    !isAuthenticated -> {
                        AuthScreen(
                            onAuthenticated = { isAuthenticated = true },
                            onGoogleSignIn = { callback ->
                                googleSignInCallback = callback
                                // Launch Google Sign-In flow
                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            }
                        )
                    }
                    // Show main app for authenticated users
                    else -> {
                        MainAppContent()
                    }
                }
            }
        }
    }

    /**
     * Initialize Google Sign-In configuration
     * Sets up the client and request parameters for Google authentication
     */
    private fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("377340528995-uhmgbchs7upgr7c8c5dvojqge1a91tlt.apps.googleusercontent.com")
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * Main app content with navigation drawer and screens
     * This is shown after successful authentication
     */
    @Composable
    private fun MainAppContent() {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        
        // Navigation drawer with app menu
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    onDestinationClicked = { route ->
                        // Navigate to selected screen
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    darkTheme = ThemeManager.isDarkMode,
                    onThemeToggle = { ThemeManager.toggleTheme() }
                )
            }
        ) {
            // Main app scaffold with top bar and content
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = { Text("Finance Tracker") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    AppNavHost(navController = navController, onGoogleSignIn = null)
                }
            }
        }
    }
}

/**
 * Navigation host that defines all the screens in the app
 * 
 * Available routes:
 * - "auth": Authentication screen
 * - "dashboard": Main dashboard with financial overview
 * - "transactions": Transaction list and management
 * - "budget": Budget planning and tracking
 * - "analytics": Financial analytics and charts
 * - "settings": App settings and user profile
 * - "add_transaction": Add new transaction screen
 * 
 * @param navController Navigation controller for screen navigation
 * @param onGoogleSignIn Callback for Google Sign-In (used in auth flow)
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    onGoogleSignIn: (() -> Unit)? = null
) {
    NavHost(navController = navController, startDestination = "dashboard") {
        // Authentication screen (used for logout flow)
        composable("auth") {
            AuthScreen(
                onAuthenticated = { /* This won't be used here */ },
                onGoogleSignIn = { /* This won't be used here */ }
            )
        }
        
        // Main app screens
        composable("dashboard") { 
            DashboardScreen(navController) 
        }
        composable("transactions") { 
            TransactionsScreen(navController) 
        }
        composable("budget") { 
            BudgetScreen() 
        }
        composable("analytics") { 
            AnalyticsScreen(navController) 
        }
        composable("settings") { 
            SettingsScreen(navController) 
        }
        composable("add_transaction") {
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}