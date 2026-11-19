package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.MatchmakerProfile
import com.example.data.model.UserProfile
import androidx.compose.material3.AlertDialog
import com.example.ui.components.BiyeAbroadDatePickerDialog
import com.example.ui.components.BiyeAbroadLogo
import com.example.ui.components.BiyeAbroadAdminLogo
import com.example.ui.components.ForgotPasswordDialog
import com.example.ui.components.GenderSelectionCards
import com.example.ui.components.ProfilePhotoPickerSection
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import com.example.ui.viewmodel.AccountType
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 1000,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
        kotlinx.coroutines.delay(1000)
        onTimeout()
    }

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF121212) else PureWhite
    val softGrayColor = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Logo centered with gentle fade-in animation
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(alpha = alphaAnim.value)
        ) {
            BiyeAbroadLogo(size = 180.dp)
        }

        // Founder credit at bottom, clear of safe area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Founded by",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = softGrayColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "TARUN AHMED",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = softGrayColor,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun WelcomeScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val scrollState = rememberScrollState()
    var showMatchmakerRegModal by remember { mutableStateOf(false) }

    // Unified Auth Form States
    var authTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Register
    var selectedRole by remember { mutableStateOf("user") } // "user" or "matchmaker"
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var fullNameInput by remember { mutableStateOf("") }
    var mmPhone by remember { mutableStateOf("+880 1") }
    var mmCountry by remember { mutableStateOf("Bangladesh") }
    var mmCity by remember { mutableStateOf("Dhaka") }
    var mmLanguages by remember { mutableStateOf("Bengali, English") }
    var mmExperienceYears by remember { mutableIntStateOf(3) }
    var mmBio by remember { mutableStateOf("") }
    var mmReferenceInfo by remember { mutableStateOf("") }
    var mmTermsConsent by remember { mutableStateOf(false) }
    var localValidationMsg by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Matchmaker / Ghotok Bottom Portal Sign-In & Recovery States
    var mmLoginEmail by remember { mutableStateOf("") }
    var mmLoginPassword by remember { mutableStateOf("") }
    var mmLoginPassVisible by remember { mutableStateOf(false) }
    var mmLoginError by remember { mutableStateOf<String?>(null) }
    var showMmForgotPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.attemptAutoSignIn(context)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Brand Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(BurgundyDeep, BurgundyPrimary)
                        )
                    )
                    .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BiyeAbroadLogo(size = 110.dp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "BiyeAbroad",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Find Your Life Partner — Together Forever",
                        fontSize = 13.sp,
                        color = PureWhite.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Trust & Value Propositions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Bangladesh's Premier Worldwide Matrimony Network",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyDeep,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Designed for Bangladeshi adults worldwide seeking sincere marriage, family values, and genuine compatibility.",
                    fontSize = 13.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Unified Interactive Authentication Card
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Tab Selector: Sign In vs Create Account
                        TabRow(
                            selectedTabIndex = authTab,
                            containerColor = WarmIvory,
                            contentColor = BurgundyDeep,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Tab(
                                selected = authTab == 0,
                                onClick = {
                                    authTab = 0
                                    localValidationMsg = null
                                    successMessage = null
                                },
                                text = {
                                    Text(
                                        text = "Sign In",
                                        fontSize = 14.sp,
                                        fontWeight = if (authTab == 0) FontWeight.Bold else FontWeight.Medium,
                                        color = if (authTab == 0) BurgundyPrimary else MutedText
                                    )
                                }
                            )
                            Tab(
                                selected = authTab == 1,
                                onClick = {
                                    authTab = 1
                                    localValidationMsg = null
                                    successMessage = null
                                },
                                text = {
                                    Text(
                                        text = "Create Account",
                                        fontSize = 14.sp,
                                        fontWeight = if (authTab == 1) FontWeight.Bold else FontWeight.Medium,
                                        color = if (authTab == 1) BurgundyPrimary else MutedText
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ROLE SELECTOR (Only in Create Account tab)
                        if (authTab == 1) {
                            Text(
                                text = "Select Account Type:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Normal User Option
                                Surface(
                                    onClick = { selectedRole = "user" },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedRole == "user") BurgundyPrimary.copy(alpha = 0.08f) else PureWhite,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (selectedRole == "user") 1.8.dp else 1.dp,
                                        if (selectedRole == "user") BurgundyPrimary else GoldBorder.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("role_selector_normal_user")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedRole == "user",
                                            onClick = { selectedRole = "user" },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = BurgundyPrimary,
                                                unselectedColor = MutedText
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(
                                                text = "Member",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BurgundyDeep
                                            )
                                            Text(
                                                text = "Candidate / Family",
                                                fontSize = 11.sp,
                                                color = MutedText
                                            )
                                        }
                                    }
                                }

                                // Matchmaker / Ghotok Option
                                Surface(
                                    onClick = { selectedRole = "matchmaker" },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedRole == "matchmaker") BurgundyPrimary.copy(alpha = 0.08f) else PureWhite,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (selectedRole == "matchmaker") 1.8.dp else 1.dp,
                                        if (selectedRole == "matchmaker") BurgundyPrimary else GoldBorder.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("role_selector_matchmaker")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedRole == "matchmaker",
                                            onClick = { selectedRole = "matchmaker" },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = BurgundyPrimary,
                                                unselectedColor = MutedText
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(
                                                text = "Matchmaker",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BurgundyDeep
                                            )
                                            Text(
                                                text = "Professional Ghotok",
                                                fontSize = 11.sp,
                                                color = MutedText
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Full Name (in Create Account tab)
                        if (authTab == 1) {
                            OutlinedTextField(
                                value = fullNameInput,
                                onValueChange = { fullNameInput = it },
                                label = { Text(if (selectedRole == "matchmaker") "Matchmaker Full Name" else "Full Name") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = BurgundyPrimary
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = biyeAbroadTextFieldColors(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (authTab == 0) {
                            Text(
                                text = "Sign in to your account with your email and password",
                                fontSize = 12.sp,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Email Field
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = BurgundyPrimary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = biyeAbroadTextFieldColors(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password (min 6 characters)") },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = biyeAbroadTextFieldColors(),
                            singleLine = true
                        )

                        // Forgot Password Link in Sign In tab
                        if (authTab == 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showForgotPasswordDialog = true },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("forgot_password_button")
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BurgundyPrimary
                                    )
                                }
                            }
                        }

                        // Extra Matchmaker Fields
                        if (authTab == 1 && selectedRole == "matchmaker") {
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = mmPhone,
                                onValueChange = { mmPhone = it },
                                label = { Text("Phone Number (with Country Code)") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = BurgundyPrimary
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_mm_phone_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = biyeAbroadTextFieldColors(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = mmCountry,
                                    onValueChange = { mmCountry = it },
                                    label = { Text("Country") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("reg_mm_country_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = biyeAbroadTextFieldColors(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = mmCity,
                                    onValueChange = { mmCity = it },
                                    label = { Text("City") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("reg_mm_city_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = biyeAbroadTextFieldColors(),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = mmLanguages,
                                onValueChange = { mmLanguages = it },
                                label = { Text("Languages Spoken (e.g. English, Bengali)") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = BurgundyPrimary
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_mm_languages_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = biyeAbroadTextFieldColors(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Matchmaking Experience: $mmExperienceYears years",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BurgundyDeep,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = { if (mmExperienceYears > 0) mmExperienceYears-- },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmIvory, contentColor = BurgundyDeep),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape
                                ) {
                                    Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { mmExperienceYears++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmIvory, contentColor = BurgundyDeep),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape
                                ) {
                                    Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = mmBio,
                                onValueChange = { mmBio = it },
                                label = { Text("Professional Bio & Specialization") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_mm_bio_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = biyeAbroadTextFieldColors(),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = mmReferenceInfo,
                                onValueChange = { mmReferenceInfo = it },
                                label = { Text("Reference or Community Info (Optional)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_mm_ref_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = biyeAbroadTextFieldColors(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { mmTermsConsent = !mmTermsConsent },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = mmTermsConsent,
                                    onCheckedChange = { mmTermsConsent = it },
                                    colors = CheckboxDefaults.colors(checkedColor = BurgundyPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I agree to BiyeAbroad Matchmaker Terms & Code of Conduct.",
                                    fontSize = 11.sp,
                                    color = DarkText,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // Success Message Banner (e.g. after Matchmaker account creation)
                        AnimatedVisibility(visible = successMessage != null) {
                            Surface(
                                color = androidx.compose.ui.graphics.Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFF81C784)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = successMessage ?: "",
                                        color = androidx.compose.ui.graphics.Color(0xFF1B5E20),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Local / Server Error Display
                        val displayError = localValidationMsg ?: errorMessage
                        AnimatedVisibility(visible = displayError != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
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

                        // Submit Button (Sign In or Create Account)
                        Button(
                            onClick = {
                                localValidationMsg = null
                                successMessage = null
                                if (authTab == 0) {
                                    // SIGN IN
                                    if (emailInput.isBlank() || passwordInput.isBlank()) {
                                        localValidationMsg = "Please enter your email and password."
                                    } else {
                                        authViewModel.signInWithEmail(emailInput.trim(), passwordInput)
                                    }
                                } else {
                                    // CREATE ACCOUNT
                                    if (selectedRole == "user") {
                                        if (fullNameInput.isBlank() || emailInput.isBlank() || passwordInput.length < 6) {
                                            localValidationMsg = "Please provide your full name, email, and password (minimum 6 characters)."
                                        } else {
                                            val registeredEmail = emailInput.trim()
                                            authViewModel.signUpMemberWithEmail(
                                                email = registeredEmail,
                                                pass = passwordInput,
                                                fullName = fullNameInput.trim(),
                                                requireLoginAfterCreate = false
                                            ) { success ->
                                                if (success) {
                                                    // Successfully authenticated and logged in into the app!
                                                    localValidationMsg = null
                                                }
                                            }
                                        }
                                    } else {
                                        // MATCHMAKER REGISTRATION
                                        if (fullNameInput.isBlank() || emailInput.isBlank() || passwordInput.length < 6) {
                                            localValidationMsg = "Please provide your full name, email, and password (minimum 6 characters)."
                                        } else if (mmPhone.isBlank() || mmPhone.length < 7) {
                                            localValidationMsg = "Please enter a valid phone number with country code."
                                        } else if (!mmTermsConsent) {
                                            localValidationMsg = "Please agree to the Matchmaker Terms & Code of Conduct."
                                        } else {
                                            val newMmProfile = MatchmakerProfile(
                                                matchmakerId = "",
                                                fullName = fullNameInput.trim(),
                                                email = emailInput.trim(),
                                                phone = mmPhone.trim(),
                                                country = mmCountry.trim(),
                                                city = mmCity.trim(),
                                                languages = mmLanguages.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                                experienceYears = mmExperienceYears,
                                                bio = mmBio.trim(),
                                                referenceInfo = mmReferenceInfo.trim(),
                                                verificationStatus = "pending",
                                                currentPlanId = "plan_free_launch",
                                                maxManagedProfiles = 10,
                                                termsAccepted = true
                                            )
                                            authViewModel.registerMatchmakerWithCredentials(
                                                email = emailInput.trim(),
                                                pass = passwordInput,
                                                profile = newMmProfile,
                                                requireLoginAfterCreate = false
                                            ) { success ->
                                                if (success) {
                                                    // Successfully authenticated into Matchmaker portal!
                                                    localValidationMsg = null
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BurgundyPrimary,
                                contentColor = PureWhite
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = GoldLight,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = if (authTab == 0) {
                                        "Sign In"
                                    } else if (selectedRole == "user") {
                                        "Create Member Account"
                                    } else {
                                        "Register as Matchmaker"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = GoldBorder.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "  OR  ",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = GoldBorder.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Google Sign-In Button
                        androidx.compose.material3.OutlinedButton(
                            onClick = { authViewModel.signInWithGoogle(context) },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("google_signin_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = PureWhite,
                                contentColor = BurgundyDeep
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Sign in icon",
                                    tint = BurgundyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Continue with Google",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BurgundyDeep
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Feature Highlights
                FeatureCard(
                    icon = Icons.Default.Public,
                    title = "Global Bangladeshi Diaspora",
                    description = "Connect with qualified individuals living in UK, USA, Canada, Australia, Europe, Japan, Bangladesh and beyond."
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureCard(
                    icon = Icons.Default.Security,
                    title = "Verified Profiles & Trust",
                    description = "Government ID and passport verification with strict community safety and anti-harassment safeguards."
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureCard(
                    icon = Icons.Default.Favorite,
                    title = "Respectful & Culturally Inclusive",
                    description = "Welcoming adults of all backgrounds with privacy controls, optional family involvement, and zero spam."
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Matchmaker / Ghotok Email & Password Sign-In & Recovery Section

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = GoldBorder.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "  🤝 Matchmaker (Ghotok) Portal Sign In  ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = GoldBorder.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Already registered a Matchmaker account? Sign in directly with your Ghotok email and password:",
                            fontSize = 12.sp,
                            color = MutedText,
                            lineHeight = 16.sp
                        )

                        OutlinedTextField(
                            value = mmLoginEmail,
                            onValueChange = {
                                mmLoginEmail = it
                                mmLoginError = null
                            },
                            label = { Text("Matchmaker Email Address") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = BurgundyPrimary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("matchmaker_bottom_email_input")
                        )

                        OutlinedTextField(
                            value = mmLoginPassword,
                            onValueChange = {
                                mmLoginPassword = it
                                mmLoginError = null
                            },
                            label = { Text("Matchmaker Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = BurgundyPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { mmLoginPassVisible = !mmLoginPassVisible }) {
                                    Icon(
                                        imageVector = if (mmLoginPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (mmLoginPassVisible) "Hide password" else "Show password",
                                        tint = MutedText
                                    )
                                }
                            },
                            visualTransformation = if (mmLoginPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("matchmaker_bottom_password_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showMmForgotPasswordDialog = true },
                                modifier = Modifier.testTag("matchmaker_forgot_password_button")
                            ) {
                                Text(
                                    text = "Forgot Password / Recover Account?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BurgundyPrimary
                                )
                            }
                        }

                        if (mmLoginError != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mmLoginError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (mmLoginEmail.isBlank() || mmLoginPassword.isBlank()) {
                                    mmLoginError = "Please enter your matchmaker email and password."
                                } else {
                                    mmLoginError = null
                                    authViewModel.signInWithEmail(
                                        email = mmLoginEmail.trim(),
                                        pass = mmLoginPassword,
                                        preferMatchmakerPortal = true
                                    ) { ok, err ->
                                        if (!ok) {
                                            mmLoginError = err ?: "Invalid matchmaker credentials."
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("matchmaker_bottom_signin_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BurgundyDeep,
                                contentColor = GoldLight
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = GoldLight,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "Sign In to Matchmaker Portal",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            }
                        }

                        androidx.compose.material3.OutlinedButton(
                            onClick = { authViewModel.enterDemoMode(AccountType.MATCHMAKER) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("explore_as_matchmaker_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                        ) {
                            Text(
                                text = "Preview Demo Matchmaker Portal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BurgundyPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = MutedText,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "100% Confidential & Secure Matrimony Platform",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }

                // Generous bottom spacer so page scrolls freely above system gestures and buttons
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                initialEmail = emailInput,
                onDismiss = { showForgotPasswordDialog = false },
                onSendReset = { email, callback ->
                    authViewModel.sendPasswordResetEmail(email, callback)
                }
            )
        }

        if (showMmForgotPasswordDialog) {
            ForgotPasswordDialog(
                initialEmail = mmLoginEmail,
                onDismiss = { showMmForgotPasswordDialog = false },
                onSendReset = { email, callback ->
                    authViewModel.sendPasswordResetEmail(email, callback)
                }
            )
        }

        if (showMatchmakerRegModal) {
            RegisterMatchmakerDialog(
                onDismiss = { showMatchmakerRegModal = false },
                onSubmit = { profile ->
                    authViewModel.registerMatchmaker(profile) { success ->
                        showMatchmakerRegModal = false
                    }
                }
            )
        }
    }
}

@Composable
fun RegisterMatchmakerDialog(
    onDismiss: () -> Unit,
    onSubmit: (MatchmakerProfile) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+880 1") }
    var country by remember { mutableStateOf("Bangladesh") }
    var city by remember { mutableStateOf("Dhaka") }
    var languages by remember { mutableStateOf("Bengali, English") }
    var experienceYears by remember { mutableIntStateOf(3) }
    var bio by remember { mutableStateOf("") }
    var referenceInfo by remember { mutableStateOf("") }
    var termsConsent by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = BurgundyPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Register as Matchmaker",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyPrimary
                    )
                    Text(
                        text = "Professional Matchmaker Application",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Join BiyeAbroad as a verified matrimonial matchmaker. All applications are reviewed by Admin before verified status is granted.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone with Country Code") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Country") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = languages,
                        onValueChange = { languages = it },
                        label = { Text("Languages") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = experienceYears.toString(),
                        onValueChange = { experienceYears = it.toIntOrNull() ?: 1 },
                        label = { Text("Exp (Yrs)") },
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Professional Bio / Matchmaking Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = referenceInfo,
                    onValueChange = { referenceInfo = it },
                    label = { Text("Reference / Community Background (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = termsConsent,
                        onCheckedChange = { termsConsent = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BurgundyPrimary, checkedTrackColor = GoldLight)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I agree to BiyeAbroad Matchmaker Terms of Service, Confidentiality, and Two-Sided Consent Policy.",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }

                errorText?.let { err ->
                    Text(text = "⚠️ $err", color = Color(0xFFD32F2F), fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank() || email.isBlank() || phone.isBlank()) {
                        errorText = "Please fill in Name, Email, and Phone."
                        return@Button
                    }
                    if (!termsConsent) {
                        errorText = "Please consent to the terms of service."
                        return@Button
                    }
                    errorText = null
                    val newProfile = MatchmakerProfile(
                        matchmakerId = "ghotok_${java.util.UUID.randomUUID().toString().take(8)}",
                        fullName = fullName.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        country = country.trim(),
                        city = city.trim(),
                        languages = languages.split(",").map { it.trim() },
                        experienceYears = experienceYears,
                        bio = bio.trim(),
                        referenceInfo = referenceInfo.trim(),
                        verificationStatus = "pending",
                        currentPlanId = "plan_free_launch",
                        maxManagedProfiles = 10,
                        termsAccepted = true
                    )
                    onSubmit(newProfile)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Surface(
        color = PureWhite,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GoldLight.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = BurgundyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyDeep
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    authViewModel: AuthViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("1998-05-15") }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var calculatedAge by remember { mutableIntStateOf(26) }
    var gender by remember { mutableStateOf("male") } // Male first as requested
    var country by remember { mutableStateOf("United Kingdom") }
    var city by remember { mutableStateOf("London") }
    var countryOfOrigin by remember { mutableStateOf("Bangladesh (Sylhet)") }
    var nationality by remember { mutableStateOf("British") }
    var selectedLanguages by remember { mutableStateOf(setOf("Bengali", "English")) }
    var religion by remember { mutableStateOf("") }
    var religiousDenomination by remember { mutableStateOf("") }
    var openToDifferentReligions by remember { mutableStateOf(true) }
    var education by remember { mutableStateOf("MSc / Master's Degree") }
    var profession by remember { mutableStateOf("Software Engineer") }
    var maritalStatus by remember { mutableStateOf("Never Married") }
    var height by remember { mutableStateOf("5'7\"") }
    var bio by remember {
        mutableStateOf("Family-oriented, dedicated, and friendly professional seeking a compatible life partner for a meaningful lifelong marriage.")
    }
    var selectedInterests by remember {
        mutableStateOf(setOf("Halal Travel", "Bengali Cuisine", "Reading", "Fitness & Gym"))
    }
    var partnerPreferences by remember {
        mutableStateOf("Seeking an educated, kind-hearted, and family-oriented partner living in the UK, USA, Canada or Bangladesh.")
    }
    var familyInvolvementPreference by remember { mutableStateOf("Joint decision (Self + Family)") }
    var photoUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800") }
    var photoPublicId by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Re-calculate age dynamically whenever dateOfBirth changes
    LaunchedEffect(dateOfBirth) {
        calculatedAge = try {
            val parts = dateOfBirth.trim().split("-", "/", ".")
            if (parts.size == 3) {
                val year = if (parts[0].length == 4) parts[0].toInt() else parts[2].toInt()
                val month = if (parts[0].length == 4) parts[1].toInt() else parts[0].toInt()
                val day = if (parts[0].length == 4) parts[2].toInt() else parts[1].toInt()
                val bDate = java.time.LocalDate.of(year, month.coerceIn(1, 12), day.coerceIn(1, 28))
                val now = java.time.LocalDate.now()
                java.time.Period.between(bDate, now).years.coerceAtLeast(18)
            } else 26
        } catch (_: Exception) {
            26
        }
    }

    // Curated options - Only Male and Female as requested
    val genderOptions = listOf(
        Pair("male", "Male (Groom)"),
        Pair("female", "Female (Bride)")
    )
    val countryOptions = listOf("United Kingdom", "United States", "Canada", "Australia", "Bangladesh", "Germany", "Sweden", "Japan", "United Arab Emirates", "Other")
    val nationalityOptions = listOf("British", "American", "Canadian", "Australian", "Bangladeshi", "German", "Irish", "Swedish", "Other")
    val bdOriginOptions = listOf("Dhaka", "Sylhet", "Chittagong", "Comilla", "Rajshahi", "Khulna", "Barisal", "Mymensingh", "Rangpur", "Noakhali")
    val religionOptions = listOf("Islam", "Hinduism", "Christianity", "Buddhism", "Sikhism", "Spiritual / Other", "None / Prefer not to say")
    val denominationOptions = listOf("Sunni", "Shia", "Sufi", "Vaishnava", "Catholic", "Protestant", "Traditional", "Liberal / Moderate", "None")
    val maritalOptions = listOf("Never Married", "Divorced", "Widowed", "Annulled")
    val heightOptions = listOf("5'0\"", "5'2\"", "5'4\"", "5'6\"", "5'7\"", "5'8\"", "5'9\"", "5'10\"", "5'11\"", "6'0\"", "6'1\"+")
    val availableLanguages = listOf("Bengali", "English", "Sylheti", "Chittagonian", "Urdu", "Arabic", "Hindi", "French", "German")
    val availableInterests = listOf("Halal Travel", "Bengali Cuisine", "Hiking & Outdoors", "Reading", "Fitness & Gym", "Tech & AI", "Classical Music", "Photography", "Charity & Volunteering", "Coffee & Cafes", "Family Gatherings")
    val familyOptions = listOf(
        "Joint decision (Self + Family)",
        "Self-directed with family blessings",
        "Parents & Family actively guiding",
        "Independent / Self-managed"
    )

    // Preset avatars for instant photo selection
    val sampleAvatars = listOf(
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800", // Male 1
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=800", // Male 2
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=800", // Male 3
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=800", // Female 1
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=800", // Female 2
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800"  // Female 3
    )

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize().imePadding(),
        color = WarmIvory
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header with official brand identity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BiyeAbroadLogo(size = 48.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Create Matrimonial Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BurgundyDeep
                        )
                        Text(
                            text = "Complete Your Bio-data • BiyeAbroad",
                            fontSize = 12.sp,
                            color = DarkText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Basic Identity & Date of Birth
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. Personal Identity & Birth Date", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BurgundyDeep)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // First Name & Last Name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = { Text("First Name *") },
                                placeholder = { Text("e.g. Farhan") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.weight(1f).testTag("input_first_name")
                            )
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = { Text("Last Name *") },
                                placeholder = { Text("e.g. Chowdhury") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.weight(1f).testTag("input_last_name")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Date of Birth: Type any date directly OR pick from calendar
                        OutlinedTextField(
                            value = dateOfBirth,
                            onValueChange = { dateOfBirth = it },
                            label = { Text("Date of Birth (Type or Select) *") },
                            placeholder = { Text("e.g. 1998-05-15 or 15/05/1998") },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showDatePickerDialog = true },
                                    modifier = Modifier.testTag("btn_choose_dob")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Pick Date from Calendar",
                                        tint = BurgundyPrimary
                                    )
                                }
                            },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_dob")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Gender - Male & Female cards (Male first, Female second as requested)
                        GenderSelectionCards(
                            selectedGender = gender,
                            onGenderSelected = { gender = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Profile Photos & Cloudinary Upload Picker
                        ProfilePhotoPickerSection(
                            currentPhotoUrl = photoUrl,
                            onPhotoSelected = { selectedUrl ->
                                photoUrl = selectedUrl
                                if (!selectedUrl.contains("res.cloudinary.com")) {
                                    photoPublicId = ""
                                }
                            },
                            onPhotoUploaded = { secureUrl, publicId ->
                                photoUrl = secureUrl
                                photoPublicId = publicId
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Location, Roots & Heritage
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. Location & Origin", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BurgundyDeep)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Country of residence
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country of Residence *") },
                            placeholder = { Text("e.g. United Kingdom, USA, Canada, Australia") },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("input_country")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // City or general location
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City or General Location *") },
                            placeholder = { Text("e.g. London, New York, Toronto, Sydney, Dhaka") },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("input_city")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Full Address
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address (House, Area / Permanent & Present)") },
                            placeholder = { Text("e.g. House 14, Road 5, Dhanmondi / Kensington, London") },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("input_address")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Country of origin & Hometown in BD
                        OutlinedTextField(
                            value = countryOfOrigin,
                            onValueChange = { countryOfOrigin = it },
                            label = { Text("Country of Origin / District in BD *") },
                            placeholder = { Text("e.g. Bangladesh (Sylhet / Dhaka / Chittagong)") },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Nationality
                        OutlinedTextField(
                            value = nationality,
                            onValueChange = { nationality = it },
                            label = { Text("Nationality *") },
                            placeholder = { Text("e.g. British, American, Canadian, Bangladeshi") },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Languages (Multi-select)
                        Text("Languages Spoken & Written:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableLanguages.forEach { lang ->
                                val isSelected = selectedLanguages.contains(lang)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedLanguages = if (isSelected) {
                                            selectedLanguages - lang
                                        } else {
                                            selectedLanguages + lang
                                        }
                                    },
                                    label = { Text(lang, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BurgundyPrimary,
                                        selectedLabelColor = PureWhite,
                                        containerColor = WarmIvory,
                                        labelColor = DarkText
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Education & Profession
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("3. Education & Career", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BurgundyDeep)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = education,
                            onValueChange = { education = it },
                            label = { Text("Education Degree & Institution *") },
                            placeholder = { Text("e.g. MSc Computer Science, Imperial College / MBBS") },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("input_education")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = profession,
                            onValueChange = { profession = it },
                            label = { Text("Profession / Job Title *") },
                            placeholder = { Text("e.g. Software Engineer, Physician, Auditor, Architect") },
                            colors = biyeAbroadTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("input_profession")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Personal & Lifestyle Profile
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("5. Personal & Lifestyle Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BurgundyDeep)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = maritalStatus,
                                onValueChange = { maritalStatus = it },
                                label = { Text("Marital Status *") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                label = { Text("Height (Optional)") },
                                placeholder = { Text("5'7\"") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Short biography
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Short Biography & About Me *") },
                            placeholder = { Text("Share your personality, passions, and what brings you joy...") },
                            colors = biyeAbroadTextFieldColors(),
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("input_bio")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Partner Preferences & Family Involvement
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("6. Partner & Family Preferences", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BurgundyDeep)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Partner preferences
                        OutlinedTextField(
                            value = partnerPreferences,
                            onValueChange = { partnerPreferences = it },
                            label = { Text("Partner Preferences & Expectations *") },
                            placeholder = { Text("Age range, values, location flexibility, education...") },
                            colors = biyeAbroadTextFieldColors(),
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth().testTag("input_partner_preferences")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Family involvement preference
                        Text("Family Involvement Preference:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            familyOptions.forEach { option ->
                                val isSelected = familyInvolvementPreference == option
                                Surface(
                                    onClick = { familyInvolvementPreference = option },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) BurgundyPrimary else WarmIvory,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BurgundyPrimary else GoldBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(14.dp),
                                            shape = CircleShape,
                                            color = if (isSelected) GoldLight else PureWhite,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BurgundyDeep)
                                        ) {}
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = option,
                                            color = if (isSelected) PureWhite else DarkText,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7. Verification Status Banner
                Surface(
                    color = WarmIvory,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BurgundyPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Verification Status: Unverified",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep
                            )
                            Text(
                                text = "You can submit Passport or National ID anytime in your profile to obtain the verified BiyeAbroad Blue Shield Badge 🛡️.",
                                fontSize = 11.sp,
                                color = DarkText,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = validationError != null) {
                    Text(
                        text = validationError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        if (firstName.isBlank() && lastName.isBlank()) {
                            validationError = "Please enter your First Name and Last Name."
                            return@Button
                        }
                        val finalDisplayName = "$firstName $lastName".trim()
                        if (calculatedAge < 18) {
                            validationError = "Members must be at least 18 years old to join BiyeAbroad."
                            return@Button
                        }
                        validationError = null
                        val profile = UserProfile(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            displayName = finalDisplayName,
                            address = address.trim(),
                            age = calculatedAge,
                            dateOfBirth = dateOfBirth.trim(),
                            gender = gender,
                            country = country.trim(),
                            city = city.trim(),
                            countryOfOrigin = countryOfOrigin.trim(),
                            nationality = nationality.trim(),
                            languages = selectedLanguages.toList(),
                            religion = religion.trim(),
                            religiousDenomination = religiousDenomination.trim(),
                            openToDifferentReligions = openToDifferentReligions,
                            education = education.trim(),
                            profession = profession.trim(),
                            maritalStatus = maritalStatus.trim(),
                            height = height.trim(),
                            bio = bio.trim(),
                            interests = emptyList(),
                            partnerPreferences = partnerPreferences.trim(),
                            familyInvolvementPreference = familyInvolvementPreference,
                            photoUrl = photoUrl.trim(),
                            photoUrls = if (photoUrl.isNotBlank()) listOf(photoUrl.trim()) else emptyList(),
                            photoPublicId = photoPublicId,
                            verificationStatus = "unverified"
                        )
                        authViewModel.completeOnboarding(profile, onComplete)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_profile_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BurgundyPrimary,
                        contentColor = PureWhite
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = GoldLight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Publish Biodata", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Skip & Explore with decorated defaults
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        val finalDisplayName = if (firstName.isNotBlank() || lastName.isNotBlank()) "$firstName $lastName".trim() else "Member"
                        val defaultProf = UserProfile(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            displayName = finalDisplayName,
                            address = address.trim(),
                            age = calculatedAge,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            country = country,
                            city = city,
                            countryOfOrigin = countryOfOrigin,
                            nationality = nationality,
                            languages = selectedLanguages.toList(),
                            religion = religion,
                            religiousDenomination = religiousDenomination,
                            openToDifferentReligions = openToDifferentReligions,
                            education = education,
                            profession = profession,
                            maritalStatus = maritalStatus,
                            height = height,
                            bio = bio,
                            interests = emptyList(),
                            partnerPreferences = partnerPreferences,
                            familyInvolvementPreference = familyInvolvementPreference,
                            photoUrl = photoUrl,
                            photoUrls = if (photoUrl.isNotBlank()) listOf(photoUrl) else emptyList(),
                            photoPublicId = photoPublicId,
                            verificationStatus = "unverified"
                        )
                        authViewModel.completeOnboarding(defaultProf, onComplete)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BurgundyPrimary)
                ) {
                    Text("Complete Later (Skip & Explore)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(180.dp))
            }
        }

        if (showDatePickerDialog) {
            BiyeAbroadDatePickerDialog(
                initialDate = dateOfBirth,
                onDateSelected = { chosenDate ->
                    dateOfBirth = chosenDate
                    showDatePickerDialog = false
                },
                onDismiss = { showDatePickerDialog = false }
            )
        }
    }
}
