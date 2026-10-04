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
import androidx.compose.ui.draw.shadow
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
import com.example.model.UserRole
import com.example.ui.components.KarivaButton
import com.example.ui.components.KarivaEmblem
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    initialRole: UserRole = UserRole.CUSTOMER,
    onLoginSuccess: (isCreator: Boolean) -> Unit,
    onDirectRoleLogin: (UserRole) -> Unit,
    onBack: () -> Unit
) {
    var isSignUpTab by remember { mutableStateOf(false) }
    var isCreatorMode by remember { mutableStateOf(initialRole == UserRole.CREATOR) }

    var fullName by remember { mutableStateOf("") }
    var email by remember {
        mutableStateOf(if (isCreatorMode) "shikha@kariva.com" else "sana.ansari@gmail.com")
    }
    var password by remember {
        mutableStateOf(if (isCreatorMode) "Shikha@1810" else "Shopper@123")
    }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Sync credentials if creator mode toggled
    LaunchedEffect(isCreatorMode) {
        if (isCreatorMode) {
            email = "shikha@kariva.com"
            password = "Shikha@1810"
        }
    }

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

                // Discrect top-right Creator Mode toggle button requested by user
                Surface(
                    onClick = {
                        isCreatorMode = !isCreatorMode
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isCreatorMode) KarivaGoldContainer else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCreatorMode) KarivaGold else KarivaBorder
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
                            tint = if (isCreatorMode) KarivaGoldDark else KarivaTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCreatorMode) "Creator Portal Active" else "Creator Portal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCreatorMode) KarivaGoldDark else KarivaCharcoal
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
                text = if (isCreatorMode) "Crochet Creator Studio" else "Handmade with Love, Loop by Loop 🧶",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCreatorMode) KarivaTerracotta else KarivaTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab bar (Login / Sign Up)
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
                            .background(if (!isSignUpTab) KarivaCharcoal else Color.Transparent)
                            .clickable { isSignUpTab = false }
                            .testTag("auth_tab_login"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
                            fontWeight = if (!isSignUpTab) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (!isSignUpTab) Color.White else KarivaTextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSignUpTab) KarivaCharcoal else Color.Transparent)
                            .clickable { isSignUpTab = true }
                            .testTag("auth_tab_signup"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Up",
                            fontWeight = if (isSignUpTab) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSignUpTab) Color.White else KarivaTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error notice if any
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

            // Input Fields matching screenshot
            if (isSignUpTab) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = KarivaTextMuted)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KarivaCharcoal,
                        unfocusedBorderColor = KarivaBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text(if (isCreatorMode) "Creator Email" else "Email or Phone Number") },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = KarivaTextMuted)
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = KarivaCharcoal,
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
                    focusedBorderColor = KarivaCharcoal,
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
                            checkedColor = KarivaCharcoal,
                            checkmarkColor = Color.White
                        )
                    )
                    Text("Remember me", fontSize = 12.5.sp, color = KarivaTextPrimary)
                }

                Text(
                    text = "Forgot Password?",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = KarivaCharcoal,
                    modifier = Modifier.clickable {
                        errorMessage = "Password reset instructions sent to $email"
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Action Button
            KarivaButton(
                text = if (isLoading) "Authenticating..." else (if (isSignUpTab) "Create Account" else "Login"),
                onClick = {
                    focusManager.clearFocus()
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both email and password"
                        return@KarivaButton
                    }
                    val isCreator = email.trim().equals("shikha@kariva.com", ignoreCase = true)
                    onLoginSuccess(isCreator)
                },
                enabled = !isLoading,
                testTag = "auth_submit_btn"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Demo Credentials chips for testing convenience
            Text(
                text = "⚡ QUICK DEMO ACCESS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = KarivaTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        email = "sana.ansari@gmail.com"
                        password = "Password@123"
                        isCreatorMode = false
                        onDirectRoleLogin(UserRole.CUSTOMER)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KarivaCharcoal),
                    modifier = Modifier.weight(1f).testTag("quick_login_customer")
                ) {
                    Text("🛍️ Shopper", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        email = "shikha@kariva.com"
                        password = "Shikha@1810"
                        isCreatorMode = true
                        onDirectRoleLogin(UserRole.CREATOR)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = KarivaGoldContainer,
                        contentColor = KarivaGoldDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaGold),
                    modifier = Modifier.weight(1f).testTag("quick_login_creator")
                ) {
                    Text("👑 Creator (Shikha)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Logins (Google / Apple) matching screenshot
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

            // Continue with Google
            Surface(
                onClick = {
                    onLoginSuccess(false)
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

            Spacer(modifier = Modifier.height(10.dp))

            // Continue with Apple
            Surface(
                onClick = {
                    onLoginSuccess(false)
                },
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apple_login_btn")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhoneIphone,
                        contentDescription = null,
                        tint = KarivaCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with Apple",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = KarivaCharcoal
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
