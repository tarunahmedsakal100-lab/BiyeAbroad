package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BiyeAbroadAdminLogo
import com.example.ui.components.ForgotPasswordDialog
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.biyeAbroadTextFieldColors
import com.example.ui.viewmodel.AccountType
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun AdminLoginScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf("tarunahmedsakal100@gmail.com") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
        ) {
            // Admin Hero Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(BurgundyDeep, BurgundyPrimary)
                        )
                    )
                    .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BiyeAbroadAdminLogo(size = 90.dp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "BiyeAbroad Admin",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Platform Governance & Security Console",
                        fontSize = 13.sp,
                        color = PureWhite.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = GoldAccent.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Status",
                                tint = GoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Restricted Administrator Portal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                }
            }

            // Login Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Administrator Sign In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDeep
                        )
                        Text(
                            text = "Enter your administrator credentials to manage users, verify biodatas, and configure platform settings.",
                            fontSize = 12.sp,
                            color = MutedText,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Email Input
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                localError = null
                            },
                            label = { Text("Admin Email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = BurgundyPrimary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_email_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = biyeAbroadTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password Input
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = {
                                passwordInput = it
                                localError = null
                            },
                            label = { Text("Admin Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = BurgundyPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = MutedText
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_password_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = biyeAbroadTextFieldColors()
                        )

                        // Forgot Password Link
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("admin_forgot_password_button")
                            ) {
                                Text(
                                    text = "Forgot Admin Password?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BurgundyPrimary
                                )
                            }
                        }

                        // Display Errors
                        val displayError = localError ?: errorMessage
                        AnimatedVisibility(visible = displayError != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = displayError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sign In Button
                        Button(
                            onClick = {
                                if (emailInput.isBlank() || passwordInput.isBlank()) {
                                    localError = "Please enter both admin email and password."
                                } else {
                                    localError = null
                                    authViewModel.signInWithEmail(emailInput.trim(), passwordInput)
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("admin_signin_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BurgundyPrimary,
                                contentColor = PureWhite
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = GoldLight,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "Sign In to Admin Console",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 1-Tap Admin Quick Access (Dedicated Admin UID)
                        OutlinedButton(
                            onClick = {
                                authViewModel.enterDemoMode(asAdmin = true)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("admin_1tap_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, BurgundyPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = PureWhite,
                                contentColor = BurgundyPrimary
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = BurgundyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "1-Tap Admin Demo Access (ZjO7...)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Security Policy Banner
                Surface(
                    color = BurgundyDeep.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = BurgundyDeep,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Zero-Trust Administrative Safeguards",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Only authorized administrators (tarunahmedsakal100@gmail.com) can access member management, ID verification records, and complaints. Non-admin users attempting login will be denied automatically.",
                                fontSize = 11.sp,
                                color = DarkText,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                initialEmail = emailInput,
                title = "Admin Account Recovery",
                onDismiss = { showForgotPasswordDialog = false },
                onSendReset = { email, callback ->
                    authViewModel.sendPasswordResetEmail(email, callback)
                }
            )
        }
    }
}

@Composable
fun AdminAccessDeniedScreen(
    userEmail: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BiyeAbroadAdminLogo(size = 70.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Access Restricted",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The account $userEmail is signed in, but does not have platform administrator privileges.\n\nThis application is dedicated strictly to BiyeAbroad Platform Directors. Please open the BiyeAbroad User App instead.",
                    fontSize = 13.sp,
                    color = DarkText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onSignOut,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BurgundyPrimary,
                        contentColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sign Out & Return",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
