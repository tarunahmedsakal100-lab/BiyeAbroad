package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.biyeAbroadTextFieldColors

@Composable
fun ForgotPasswordDialog(
    initialEmail: String = "",
    title: String = "Forgot Password & Account Recovery",
    onDismiss: () -> Unit,
    onSendReset: (String, (Boolean, String?) -> Unit) -> Unit
) {
    var emailInput by remember { mutableStateOf(initialEmail) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(WarmIvory, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = "Password Reset",
                        tint = BurgundyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep
                    )
                    Text(
                        text = "Secure Email-Based Password Reset",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Enter your registered email address. We will send an official password reset link directly to your inbox.",
                    fontSize = 13.sp,
                    color = DarkText,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        if (isSuccess) isSuccess = false
                        statusMessage = null
                    },
                    label = { Text("Registered Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = BurgundyPrimary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forgot_password_email_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = biyeAbroadTextFieldColors()
                )

                AnimatedVisibility(visible = statusMessage != null) {
                    Surface(
                        color = if (isSuccess) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSuccess) Color(0xFF81C784) else MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isSuccess) SuccessGreen else MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = statusMessage ?: "",
                                fontSize = 12.sp,
                                color = if (isSuccess) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onErrorContainer,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                if (isSuccess) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = WarmIvory,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Next Steps:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BurgundyDeep
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "1. Open the email from BiyeAbroad in your mail app.\n2. Tap the password reset link.\n3. Enter your new password and submit.\n4. Return to the app and sign in with your new credentials.",
                                fontSize = 11.sp,
                                color = DarkText,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isSuccess) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            statusMessage = null
                            onSendReset(emailInput) { ok, msg ->
                                isLoading = false
                                isSuccess = ok
                                statusMessage = msg
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BurgundyPrimary)
                    ) {
                        Text(
                            text = "Resend Email",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyPrimary
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BurgundyPrimary,
                            contentColor = PureWhite
                        )
                    ) {
                        Text(
                            text = "Done",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        statusMessage = null
                        onSendReset(emailInput) { ok, msg ->
                            isLoading = false
                            isSuccess = ok
                            statusMessage = msg
                        }
                    },
                    enabled = !isLoading && emailInput.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BurgundyPrimary,
                        contentColor = PureWhite
                    ),
                    modifier = Modifier.testTag("send_reset_email_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = GoldLight,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "Send Reset Link",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        },
        dismissButton = {
            if (!isSuccess) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }
            }
        }
    )
}
