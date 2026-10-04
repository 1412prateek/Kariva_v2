package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.model.CustomerDetails
import com.example.ui.theme.*

private val StepGreen = Color(0xFF4CAF50)
private val TopHeaderTerracotta = Color(0xFFA64A30)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSignupDetailsScreen(
    initialEmail: String = "",
    isExistingUser: Boolean = false,
    existingDetails: CustomerDetails? = null,
    onBack: () -> Unit,
    onSubmit: (CustomerDetails, String, (String) -> Unit) -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    val focusManager = LocalFocusManager.current

    // Step 1: Basic Details
    var firstName by remember { mutableStateOf(existingDetails?.customer_first_name ?: "") }
    var middleName by remember { mutableStateOf(existingDetails?.customer_middle_name ?: "") }
    var lastName by remember { mutableStateOf(existingDetails?.customer_last_name ?: "") }
    var email by remember { mutableStateOf(existingDetails?.customer_email?.ifBlank { initialEmail } ?: initialEmail) }
    var mobileNo by remember { mutableStateOf(existingDetails?.customer_mobile_no ?: "") }
    var agreedToTerms by remember { mutableStateOf(true) }

    // Step 2: Personal (Password & Security)
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Step 3: Address
    var houseNo by remember { mutableStateOf(existingDetails?.customer_house_no ?: "") }
    var pincode by remember { mutableStateOf(existingDetails?.customer_zip_code ?: "") }
    var addressLine1 by remember { mutableStateOf(existingDetails?.customer_address_line_1 ?: "") }
    var addressLine2 by remember { mutableStateOf(existingDetails?.customer_address_line_2 ?: "") }
    var district by remember { mutableStateOf(existingDetails?.customer_district ?: "Gurugram") }
    var state by remember { mutableStateOf(existingDetails?.customer_state ?: "Haryana") }
    var country by remember { mutableStateOf(existingDetails?.customer_country?.ifBlank { "India" } ?: "India") }

    var isDistrictDropdownExpanded by remember { mutableStateOf(false) }
    var isStateDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val indianStates = listOf(
        "Andhra Pradesh", "Assam", "Bihar", "Chhattisgarh", "Delhi", "Goa",
        "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Odisha",
        "Punjab", "Rajasthan", "Tamil Nadu", "Telangana", "Uttar Pradesh",
        "Uttarakhand", "West Bengal"
    )

    val sampleDistricts = listOf(
        "Gurugram", "Faridabad", "Central Delhi", "South Delhi", "North Delhi",
        "Noida / Gautam Buddha Nagar", "Ghaziabad", "Jaipur", "Bengaluru Urban",
        "Mumbai City", "Pune", "Ahmedabad", "Chandigarh", "Lucknow", "Kolkata"
    )

    fun validateStep1(): Boolean {
        if (firstName.trim().isBlank()) {
            errorMessage = "Please enter your first name"
            return false
        }
        if (lastName.trim().isBlank()) {
            errorMessage = "Please enter your last name"
            return false
        }
        if (email.trim().isBlank() || !email.contains("@") || !email.contains(".")) {
            errorMessage = "Please enter a valid email address"
            return false
        }
        val cleanMobile = mobileNo.trim().filter { it.isDigit() }
        if (cleanMobile.length < 10) {
            errorMessage = "Please enter a valid 10-digit mobile number"
            return false
        }
        if (!agreedToTerms) {
            errorMessage = "Please agree to the Terms & Conditions and Privacy Policy"
            return false
        }
        errorMessage = null
        return true
    }

    fun validateStep2(): Boolean {
        if (isExistingUser) return true

        if (password.length < 8) {
            errorMessage = "Password must be at least 8 characters long"
            return false
        }
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            errorMessage = "Password must include uppercase, lowercase, number, and special character"
            return false
        }
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return false
        }
        errorMessage = null
        return true
    }

    fun validateStep3(): Boolean {
        if (houseNo.trim().isBlank()) {
            errorMessage = "Please enter your Floor/Door/Block No"
            return false
        }
        if (pincode.trim().filter { it.isDigit() }.length != 6) {
            errorMessage = "Please enter a valid 6-digit Pincode"
            return false
        }
        if (addressLine1.trim().isBlank()) {
            errorMessage = "Please enter Address Line 1"
            return false
        }
        if (district.trim().isBlank()) {
            errorMessage = "Please specify your District"
            return false
        }
        if (state.trim().isBlank()) {
            errorMessage = "Please specify your State"
            return false
        }
        errorMessage = null
        return true
    }

    fun handleContinue() {
        focusManager.clearFocus()
        when (currentStep) {
            1 -> {
                if (validateStep1()) {
                    if (isExistingUser) {
                        currentStep = 3
                    } else {
                        currentStep = 2
                    }
                }
            }
            2 -> {
                if (validateStep2()) {
                    currentStep = 3
                }
            }
            3 -> {
                if (validateStep3()) {
                    val customerData = CustomerDetails(
                        customer_first_name = firstName.trim(),
                        customer_middle_name = middleName.trim(),
                        customer_last_name = lastName.trim(),
                        customer_email = email.trim().lowercase(),
                        customer_mobile_no = mobileNo.trim(),
                        customer_house_no = houseNo.trim(),
                        customer_address_line_1 = addressLine1.trim(),
                        customer_address_line_2 = addressLine2.trim(),
                        customer_district = district.trim(),
                        customer_state = state.trim(),
                        customer_country = country.trim(),
                        customer_zip_code = pincode.trim(),
                        customer_location = null
                    )
                    isLoading = true
                    errorMessage = null
                    onSubmit(customerData, password) { err ->
                        isLoading = false
                        errorMessage = err
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TopHeaderTerracotta)
    ) {
        // Top Header Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // App Title & Back Button Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (currentStep > 1) {
                            currentStep -= 1
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .testTag("signup_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Kariva",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "Create an Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Please fill the details to complete the Process",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3-Step Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Step 1: BASIC
                StepIndicatorItem(
                    stepNumber = 1,
                    label = "BASIC",
                    isCompleted = currentStep > 1,
                    isActive = currentStep == 1
                )

                // Line 1 -> 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (currentStep > 1) StepGreen else Color.White.copy(alpha = 0.35f))
                )

                // Step 2: PERSONAL
                StepIndicatorItem(
                    stepNumber = 2,
                    label = "PERSONAL",
                    isCompleted = currentStep > 2,
                    isActive = currentStep == 2
                )

                // Line 2 -> 3
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (currentStep > 2) StepGreen else Color.White.copy(alpha = 0.35f))
                )

                // Step 3: ADDRESS
                StepIndicatorItem(
                    stepNumber = 3,
                    label = "ADDRESS",
                    isCompleted = false,
                    isActive = currentStep == 3
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // White Container with Rounded Top Corners
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .imePadding()
            ) {
                // Error Alert Banner
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFC62828),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                when (currentStep) {
                    1 -> {
                        // STEP 1: BASIC
                        DetailsInputField(
                            value = firstName,
                            onValueChange = { firstName = it; errorMessage = null },
                            placeholder = "First Name",
                            testTag = "input_first_name",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailsInputField(
                            value = middleName,
                            onValueChange = { middleName = it; errorMessage = null },
                            placeholder = "Middle name",
                            testTag = "input_middle_name",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailsInputField(
                            value = lastName,
                            onValueChange = { lastName = it; errorMessage = null },
                            placeholder = "Last Name",
                            testTag = "input_last_name",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailsInputField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            placeholder = "Email address",
                            testTag = "input_email",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailsInputField(
                            value = mobileNo,
                            onValueChange = { mobileNo = it; errorMessage = null },
                            placeholder = "Mobile number",
                            testTag = "input_mobile",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Terms & Conditions Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { agreedToTerms = !agreedToTerms }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = agreedToTerms,
                                onCheckedChange = { agreedToTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = TopHeaderTerracotta,
                                    checkmarkColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "By Selecting Agree and Continue. I agree to Terms & Conditions and Privacy Policy",
                                fontSize = 12.sp,
                                color = KarivaTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    2 -> {
                        // STEP 2: PERSONAL (Password & Security)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            placeholder = { Text("Password", color = Color.Gray) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = TopHeaderTerracotta,
                                unfocusedBorderColor = Color(0xFFD6D6D6)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_signup_password"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Password must be at least 8 characters long and include an uppercase letter, lowercase letter, number, and special character.",
                            fontSize = 12.sp,
                            color = Color(0xFF6E6E6E),
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; errorMessage = null },
                            placeholder = { Text("Confirm Password", color = Color.Gray) },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = TopHeaderTerracotta,
                                unfocusedBorderColor = Color(0xFFD6D6D6)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_confirm_password"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )
                    }

                    3 -> {
                        // STEP 3: ADDRESS
                        DetailsInputField(
                            value = houseNo,
                            onValueChange = { houseNo = it; errorMessage = null },
                            placeholder = "Floor/Door/Block No",
                            testTag = "input_house_no",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailsInputField(
                            value = pincode,
                            onValueChange = { pincode = it; errorMessage = null },
                            placeholder = "Pincode",
                            testTag = "input_pincode",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailsInputField(
                            value = addressLine1,
                            onValueChange = { addressLine1 = it; errorMessage = null },
                            placeholder = "Address Line 1",
                            testTag = "input_address_1",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailsInputField(
                            value = addressLine2,
                            onValueChange = { addressLine2 = it; errorMessage = null },
                            placeholder = "Address Line 2",
                            testTag = "input_address_2",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // District Selection Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isDistrictDropdownExpanded,
                            onExpandedChange = { isDistrictDropdownExpanded = !isDistrictDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it; errorMessage = null },
                                placeholder = { Text("District", color = Color.Gray) },
                                trailingIcon = {
                                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select District", tint = Color.Gray)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = TopHeaderTerracotta,
                                    unfocusedBorderColor = Color(0xFFD6D6D6)
                                ),
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                                    .fillMaxWidth()
                                    .testTag("input_district")
                            )

                            ExposedDropdownMenu(
                                expanded = isDistrictDropdownExpanded,
                                onDismissRequest = { isDistrictDropdownExpanded = false }
                            ) {
                                sampleDistricts.forEach { dist ->
                                    DropdownMenuItem(
                                        text = { Text(dist) },
                                        onClick = {
                                            district = dist
                                            isDistrictDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // State Selection Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isStateDropdownExpanded,
                            onExpandedChange = { isStateDropdownExpanded = !isStateDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = state,
                                onValueChange = { state = it; errorMessage = null },
                                placeholder = { Text("State", color = Color.Gray) },
                                trailingIcon = {
                                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select State", tint = Color.Gray)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = TopHeaderTerracotta,
                                    unfocusedBorderColor = Color(0xFFD6D6D6)
                                ),
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                                    .fillMaxWidth()
                                    .testTag("input_state")
                            )

                            ExposedDropdownMenu(
                                expanded = isStateDropdownExpanded,
                                onDismissRequest = { isStateDropdownExpanded = false }
                            ) {
                                indianStates.forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st) },
                                        onClick = {
                                            state = st
                                            isStateDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Country field
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            placeholder = { Text("Country", color = Color.Gray) },
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = TopHeaderTerracotta,
                                unfocusedBorderColor = Color(0xFFD6D6D6)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_country")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Continue / Submit Button
                Button(
                    onClick = { handleContinue() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("details_continue_btn"),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TopHeaderTerracotta,
                        contentColor = Color.White,
                        disabledContainerColor = TopHeaderTerracotta.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun DetailsInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    testTag: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = TopHeaderTerracotta,
            unfocusedBorderColor = Color(0xFFD6D6D6)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Composable
private fun StepIndicatorItem(
    stepNumber: Int,
    label: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> StepGreen
                        isActive -> Color.White
                        else -> Color.White.copy(alpha = 0.4f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isActive) TopHeaderTerracotta else Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive || isCompleted) Color.White else Color.White.copy(alpha = 0.65f)
        )
    }
}
