package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KarivaButton
import com.example.ui.components.KarivaEmblem
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    initialIsCreator: Boolean = false,
    onCustomerLogin: (email: String, pass: String, onError: (String) -> Unit) -> Unit,
    onNavigateToCustomerSignUp: (email: String) -> Unit,
    onCreatorLogin: (email: String, pass: String, onError: (String) -> Unit) -> Unit,
    onBack: () -> Unit
) {
    var isCreatorMode by remember { mutableStateOf(initialIsCreator) }

    // Pristine, clean inputs without any prefilled credentials
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with discreet Creator Portal toggle in the top-right corner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, KarivaBorder, CircleShape)
                        .testTag("auth_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = KarivaCharcoal
                    )
                }

                // Discreet top-right Creator Mode toggle
                Surface(
                    onClick = {
                        isCreatorMode = !isCreatorMode
                        errorMessage = null
                        email = ""
                        password = ""
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isCreatorMode) KarivaTerracottaContainer else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCreatorMode) KarivaTerracotta else KarivaBorder
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("creator_login_corner_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCreatorMode) Icons.Filled.Storefront else Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = if (isCreatorMode) KarivaTerracotta else KarivaTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCreatorMode) "Creator Portal" else "Creator Login",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCreatorMode) KarivaTerracotta else KarivaCharcoal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logo & Header
            KarivaEmblem(size = 48)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Kariva",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                ),
                color = KarivaCharcoal
            )

            Text(
                text = if (isCreatorMode) "Atelier Creator Studio Login" else "Handmade with Love, Loop by Loop 🧶",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCreatorMode) KarivaTerracotta else KarivaTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Customer Tab Bar (Login / Sign Up) - Only shown for Shoppers
            if (!isCreatorMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = KarivaSurfaceCard
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(KarivaCharcoal)
                                .testTag("auth_tab_login"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Login",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Transparent)
                                .clickable {
                                    onNavigateToCustomerSignUp(email.trim())
                                }
                                .testTag("auth_tab_signup"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign Up",
                                fontWeight = FontWeight.Normal,
                                color = KarivaTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KarivaTerracottaContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaTerracotta.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 Creator Authorization Required\nSign in to manage product listings, inventory, and order fulfillment.",
                        fontSize = 12.sp,
                        color = KarivaTerracotta,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Error notice
            AnimatedVisibility(visible = errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KarivaAccentRed.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaAccentRed.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = KarivaAccentRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Input Fields
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text(if (isCreatorMode) "Creator Email Address" else "Email Address") },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = KarivaTextMuted)
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = if (isCreatorMode) KarivaTerracotta else KarivaCharcoal,
                    unfocusedBorderColor = KarivaBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = KarivaTextMuted)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = KarivaTextMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = if (isCreatorMode) KarivaTerracotta else KarivaCharcoal,
                    unfocusedBorderColor = KarivaBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remember me & Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = if (isCreatorMode) KarivaTerracotta else KarivaCharcoal,
                            checkmarkColor = Color.White
                        )
                    )
                    Text("Remember me", fontSize = 12.5.sp, color = KarivaTextPrimary)
                }

                if (!isCreatorMode) {
                    Text(
                        text = "Forgot Password?",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = KarivaCharcoal,
                        modifier = Modifier.clickable {
                            if (email.isNotBlank()) {
                                errorMessage = "Password reset instructions sent to $email"
                            } else {
                                errorMessage = "Please enter your email above first."
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action Button
            KarivaButton(
                text = if (isLoading) "Authenticating..." else (
                    if (isCreatorMode) "Log In to Creator Studio"
                    else "Log In"
                ),
                onClick = {
                    focusManager.clearFocus()
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter your email and password."
                        return@KarivaButton
                    }

                    if (isCreatorMode) {
                        isLoading = true
                        onCreatorLogin(email.trim(), password) { err ->
                            isLoading = false
                            errorMessage = err
                        }
                    } else {
                        isLoading = true
                        onCustomerLogin(email.trim(), password) { err ->
                            isLoading = false
                            errorMessage = err
                            if (err.contains("not registered", ignoreCase = true) || err.contains("register first", ignoreCase = true)) {
                                onNavigateToCustomerSignUp(email.trim())
                            }
                        }
                    }
                },
                enabled = !isLoading,
                isTerracotta = isCreatorMode,
                testTag = "auth_submit_btn"
            )

            // Direct link to 3-step registration screen
            if (!isCreatorMode) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 13.5.sp,
                        color = KarivaTextSecondary
                    )
                    Text(
                        text = "Sign Up",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarivaTerracotta,
                        modifier = Modifier
                            .clickable { onNavigateToCustomerSignUp(email.trim()) }
                            .testTag("auth_link_to_signup")
                    )
                }
            }

            // Switch to shopper if in creator mode
            if (isCreatorMode) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        isCreatorMode = false
                        errorMessage = null
                        email = ""
                        password = ""
                    }
                ) {
                    Text(
                        "← Back to Customer Shopping",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = KarivaCharcoal
                    )
                }
            }

            // Customer Social Login Options
            if (!isCreatorMode) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = KarivaBorder)
                    Text(
                        text = "  or  ",
                        fontSize = 12.sp,
                        color = KarivaTextMuted
                    )
                    Divider(modifier = Modifier.weight(1f), color = KarivaBorder)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    onClick = {
                        onNavigateToCustomerSignUp(email.trim())
                    },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_login_btn")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = KarivaCharcoal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = KarivaCharcoal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
