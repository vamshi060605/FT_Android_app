package com.vt.ft_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview

/**
 * AuthScreen - Authentication screen for the Finance Tracker app
 * 
 * This screen provides:
 * - Email/password authentication (sign in and sign up)
 * - Google Sign-In integration
 * - Toggle between sign in and sign up modes
 * - Error handling and loading states
 * - Responsive scrollable layout
 * 
 * Features:
 * - Modern Material3 design with gradient background
 * - Form validation and error display
 * - Loading indicators during authentication
 * - Seamless switching between auth modes
 * - Google Sign-In button with proper styling
 * 
 * Authentication Flow:
 * 1. User enters email and password
 * 2. App validates input and shows loading state
 * 3. Firebase authentication is performed
 * 4. Success callback navigates to main app
 * 5. Error callback displays error message
 * 
 * @param viewModel AuthViewModel for authentication logic
 * @param onAuthenticated Callback when authentication succeeds
 * @param onGoogleSignIn Callback for Google Sign-In flow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthenticated: () -> Unit,
    onGoogleSignIn: ((String) -> Unit) -> Unit
) {
    // State to toggle between sign in and sign up modes
    var isSignUp by remember { mutableStateOf(false) }
    
    // Main screen container with gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Deep blue
                        Color(0xFF0D47A1), // Medium blue
                        Color(0xFF01579B)  // Light blue
                    )
                )
            )
    ) {
        // Scrollable content using LazyColumn for better UX
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top spacing for better centering on different screen sizes
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
            
            // App Logo/Icon
            item {
                AppLogo()
            }
            
            // Welcome Text Section
            item {
                WelcomeText(isSignUp = isSignUp)
            }
            
            // Authentication Form Card
            item {
                AuthFormCard(
                    viewModel = viewModel,
                    isSignUp = isSignUp,
                    onToggleMode = { isSignUp = !isSignUp },
                    onAuthenticated = onAuthenticated,
                    onGoogleSignIn = onGoogleSignIn
                )
            }
            
            // Bottom spacing for better UX
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
} 

/**
 * App Logo - Displays the app icon/logo
 * 
 * Features:
 * - Circular card design with app icon
 * - Consistent branding across the app
 * - Professional appearance with proper sizing
 */
@Composable
private fun AppLogo() {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(60.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "🔐",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

/**
 * Welcome Text - Displays welcome message and description
 * 
 * @param isSignUp Whether the user is in sign up mode
 */
@Composable
private fun WelcomeText(isSignUp: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main heading
        Text(
            if (isSignUp) "Create Account" else "Welcome Back!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle/description
        Text(
            if (isSignUp) "Sign up to start tracking your finances" else "Sign in to access your financial dashboard",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Authentication Form Card - Contains all authentication UI elements
 * 
 * @param viewModel AuthViewModel for authentication logic
 * @param isSignUp Whether the user is in sign up mode
 * @param onToggleMode Callback to switch between sign in/sign up
 * @param onAuthenticated Callback when authentication succeeds
 * @param onGoogleSignIn Callback for Google Sign-In
 */
@Composable
private fun AuthFormCard(
    viewModel: AuthViewModel,
    isSignUp: Boolean,
    onToggleMode: () -> Unit,
    onAuthenticated: () -> Unit,
    onGoogleSignIn: ((String) -> Unit) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Email Input Field
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { 
                    Text(
                        "Email",
                        color = Color.White.copy(alpha = 0.9f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White.copy(alpha = 0.7f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White
                ),
                singleLine = true
            )
            
            // Password Input Field
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { 
                    Text(
                        "Password",
                        color = Color.White.copy(alpha = 0.9f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White.copy(alpha = 0.7f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Main Authentication Button
            Button(
                onClick = {
                    if (isSignUp) {
                        viewModel.registerWithEmail {
                            onAuthenticated()
                        }
                    } else {
                        viewModel.signInWithEmail {
                            onAuthenticated()
                        }
                    }
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                // Loading indicator when authentication is in progress
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isSignUp) "Create Account" else "Sign In",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Toggle Button - Switch between sign in and sign up
            OutlinedButton(
                onClick = onToggleMode,
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
            ) {
                Text(
                    if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Divider with "OR" text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.3f)
                )
                Text(
                    "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.3f)
                )
            }
            
            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    onGoogleSignIn { idToken ->
                        viewModel.firebaseAuthWithGoogle(idToken) {
                            onAuthenticated()
                        }
                    }
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
            ) {
                Text("🔍", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Continue with Google",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Error Message Display
            if (viewModel.errorMessage != null) {
                ErrorMessageCard(errorMessage = viewModel.errorMessage)
            }
        }
    }
}

/**
 * Error Message Card - Displays authentication errors
 * 
 * @param errorMessage Error message to display
 */
@Composable
private fun ErrorMessageCard(errorMessage: String?) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE57373).copy(alpha = 0.2f)
        )
    ) {
        Text(
            errorMessage ?: "",
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFE57373),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Preview function for development and testing
 */
@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen(
        onAuthenticated = {},
        onGoogleSignIn = {}
    )
} 