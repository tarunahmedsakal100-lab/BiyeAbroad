package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserProfile
import com.google.firebase.auth.auth
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.biyeAbroadTextFieldColors
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit,
    onPersistUploadedPhoto: (suspend (secureUrl: String, publicId: String) -> Result<Unit>)? = null,
    isUploadAuthenticated: () -> Boolean = { com.google.firebase.Firebase.auth.currentUser != null }
) {
    var firstName by remember {
        mutableStateOf(
            profile.firstName.ifBlank {
                profile.displayName.split(" ").firstOrNull() ?: ""
            }
        )
    }
    var lastName by remember {
        mutableStateOf(
            profile.lastName.ifBlank {
                profile.displayName.split(" ").drop(1).joinToString(" ")
            }
        )
    }
    var address by remember { mutableStateOf(profile.address) }

    // Parse existing date into year, month, day components for direct manual typing
    val initialParts = profile.dateOfBirth.trim().split("-", "/", ".")
    var birthYearInput by remember {
        mutableStateOf(
            if (initialParts.size == 3 && initialParts[0].length == 4) initialParts[0]
            else if (initialParts.size == 3 && initialParts[2].length == 4) initialParts[2]
            else "1998"
        )
    }
    var birthMonthInput by remember {
        mutableStateOf(
            if (initialParts.size == 3 && initialParts[0].length == 4) initialParts[1]
            else if (initialParts.size == 3) initialParts[1]
            else "05"
        )
    }
    var birthDayInput by remember {
        mutableStateOf(
            if (initialParts.size == 3 && initialParts[0].length == 4) initialParts[2]
            else if (initialParts.size == 3) initialParts[0]
            else "15"
        )
    }

    var manualAgeInput by remember { mutableStateOf(profile.resolvedAge().toString()) }
    var calculatedAge by remember { mutableIntStateOf(profile.resolvedAge()) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    var gender by remember { mutableStateOf(profile.gender) }
    var country by remember { mutableStateOf(profile.country) }
    var city by remember { mutableStateOf(profile.city) }
    var countryOfOrigin by remember { mutableStateOf(profile.countryOfOrigin) }
    var nationality by remember { mutableStateOf(profile.nationality) }
    var selectedLanguages by remember { mutableStateOf(profile.languages.toSet().ifEmpty { setOf("Bengali", "English") }) }
    var religion by remember { mutableStateOf(profile.religion) }
    var religiousDenomination by remember { mutableStateOf(profile.religiousDenomination) }
    var openToDifferentReligions by remember { mutableStateOf(profile.openToDifferentReligions) }
    var education by remember { mutableStateOf(profile.education) }
    var profession by remember { mutableStateOf(profile.profession) }
    var maritalStatus by remember { mutableStateOf(profile.maritalStatus) }
    var height by remember { mutableStateOf(profile.height) }
    var bio by remember { mutableStateOf(profile.bio) }
    var selectedInterests by remember { mutableStateOf(profile.interests.toSet().ifEmpty { setOf("Halal Travel", "Bengali Cuisine") }) }
    var partnerPreferences by remember { mutableStateOf(profile.partnerPreferences) }
    var familyInvolvementPreference by remember { mutableStateOf(profile.familyInvolvementPreference) }
    var photoUrl by remember { mutableStateOf(profile.photoUrl) }
    var photoUrls by remember { mutableStateOf(profile.photoUrls) }
    var photoPublicId by remember { mutableStateOf(profile.resolvedCloudinaryPublicId()) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val dialogScrollState = rememberScrollState()

    // Recalculate age whenever birth year input changes
    LaunchedEffect(birthYearInput, birthMonthInput, birthDayInput) {
        val y = birthYearInput.toIntOrNull()
        val m = birthMonthInput.toIntOrNull()?.coerceIn(1, 12) ?: 1
        val d = birthDayInput.toIntOrNull()?.coerceIn(1, 28) ?: 1
        if (y != null && y in 1940..2010) {
            val now = LocalDate.now()
            val bDate = LocalDate.of(y, m, d)
            val diff = java.time.Period.between(bDate, now).years
            calculatedAge = diff.coerceAtLeast(18)
            manualAgeInput = calculatedAge.toString()
        }
    }

    val availableLanguages = listOf("Bengali", "English", "Sylheti", "Chittagonian", "Urdu", "Arabic", "Hindi", "French", "German")
    val availableInterests = listOf("Halal Travel", "Bengali Cuisine", "Hiking & Outdoors", "Reading", "Fitness & Gym", "Tech & AI", "Classical Music", "Photography", "Charity & Volunteering", "Coffee & Cafes")
    val maritalOptions = listOf("Never Married", "Divorced", "Widowed", "Awaiting Divorce")
    val familyOptions = listOf(
        "Joint decision (Self + Family)",
        "Self-directed with family blessings",
        "Parents & Family actively guiding",
        "Independent / Self-managed"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            color = WarmIvory
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Header
                Surface(
                    color = BurgundyDeep,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = GoldLight
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Edit Matrimonial Biodata",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Text(
                                    text = "Direct Year & Details Input",
                                    fontSize = 11.sp,
                                    color = PureWhite.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (firstName.isBlank() && lastName.isBlank()) {
                                    validationError = "Please enter your First Name and Last Name."
                                    return@Button
                                }
                                val fullName = "$firstName $lastName".trim()
                                val y = birthYearInput.toIntOrNull() ?: 1998
                                val m = (birthMonthInput.toIntOrNull() ?: 5).coerceIn(1, 12).toString().padStart(2, '0')
                                val d = (birthDayInput.toIntOrNull() ?: 15).coerceIn(1, 31).toString().padStart(2, '0')
                                val finalDob = "$y-$m-$d"
                                val finalAge = manualAgeInput.toIntOrNull()?.coerceIn(18, 100) ?: calculatedAge

                                val updated = profile.copy(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    displayName = fullName,
                                    address = address.trim(),
                                    dateOfBirth = finalDob,
                                    age = finalAge,
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
                                    interests = selectedInterests.toList(),
                                    partnerPreferences = partnerPreferences.trim(),
                                    familyInvolvementPreference = familyInvolvementPreference,
                                    photoUrl = photoUrl.trim(),
                                    photoUrls = if (photoUrls.isNotEmpty()) photoUrls else if (photoUrl.isNotBlank()) listOf(photoUrl.trim()) else emptyList(),
                                    photoPublicId = photoPublicId,
                                    cloudinaryPublicId = photoPublicId
                                )
                                onSave(updated)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BurgundyPrimary,
                                contentColor = PureWhite
                            ),
                            modifier = Modifier.testTag("save_biodata_top_button")
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }

                // Scrollable Content (Screen upore niche korar system)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(dialogScrollState)
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (validationError != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = validationError ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // 1. MANUAL YEAR & BIRTH DATE SECTION (Prominent for easy manual typing)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.2.dp, GoldBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = BurgundyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "1. Birth Year & Age (Manual Direct Typing)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BurgundyPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Type your birth year directly (e.g. 1996) or change your age directly below. No date picker required.",
                                fontSize = 11.sp,
                                color = MutedText
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Row with Manual Year, Month, Day
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = birthYearInput,
                                    onValueChange = {
                                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                            birthYearInput = it
                                        }
                                    },
                                    label = { Text("Year (e.g. 1996)") },
                                    placeholder = { Text("1996") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = biyeAbroadTextFieldColors(),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("birth_year_manual_input")
                                )

                                OutlinedTextField(
                                    value = birthMonthInput,
                                    onValueChange = {
                                        if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                                            birthMonthInput = it
                                        }
                                    },
                                    label = { Text("Month (1-12)") },
                                    placeholder = { Text("05") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = biyeAbroadTextFieldColors(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("birth_month_manual_input")
                                )

                                OutlinedTextField(
                                    value = birthDayInput,
                                    onValueChange = {
                                        if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                                            birthDayInput = it
                                        }
                                    },
                                    label = { Text("Day (1-31)") },
                                    placeholder = { Text("15") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = biyeAbroadTextFieldColors(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("birth_day_manual_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Direct Manual Age input + Verified calculation badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = manualAgeInput,
                                    onValueChange = {
                                        if (it.length <= 3 && it.all { char -> char.isDigit() }) {
                                            manualAgeInput = it
                                            val typedAge = it.toIntOrNull()
                                            if (typedAge != null && typedAge in 18..99) {
                                                calculatedAge = typedAge
                                            }
                                        }
                                    },
                                    label = { Text("Age (Years)") },
                                    placeholder = { Text("28") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = biyeAbroadTextFieldColors(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("manual_age_input")
                                )

                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFF81C784)),
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Verified Age",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20)
                                        )
                                        Text(
                                            text = "$calculatedAge Years (Matrimonial Valid)",
                                            fontSize = 11.sp,
                                            color = DarkText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. PERSONAL IDENTITY & NAMES
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "2. Personal Identity & Photo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BurgundyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = firstName,
                                    onValueChange = { firstName = it },
                                    label = { Text("First Name *") },
                                    placeholder = { Text("Farhan") },
                                    colors = biyeAbroadTextFieldColors(),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    label = { Text("Last Name *") },
                                    placeholder = { Text("Chowdhury") },
                                    colors = biyeAbroadTextFieldColors(),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            GenderSelectionCards(
                                selectedGender = gender,
                                onGenderSelected = { gender = it }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ProfilePhotoPickerSection(
                                currentPhotoUrl = photoUrl,
                                onPhotoSelected = { selectedUrl ->
                                    photoUrl = selectedUrl
                                    photoUrls = if (selectedUrl.isNotBlank()) listOf(selectedUrl) else emptyList()
                                    if (!selectedUrl.contains("res.cloudinary.com")) {
                                        photoPublicId = ""
                                    }
                                },
                                onPhotoUploaded = { secureUrl, publicId ->
                                    photoUrl = secureUrl
                                    photoUrls = listOf(secureUrl)
                                    photoPublicId = publicId
                                },
                                onPersistUploadedPhoto = onPersistUploadedPhoto,
                                isUploadAuthenticated = isUploadAuthenticated
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. LOCATION & RESIDENCE
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "3. Location & Bangladeshi Heritage",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BurgundyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = country,
                                onValueChange = { country = it },
                                label = { Text("Country of Residence") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City or Area") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Detailed Address (House, Area / Present & Permanent)") },
                                placeholder = { Text("House 14, Road 5, Dhanmondi / Kensington, London") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = countryOfOrigin,
                                onValueChange = { countryOfOrigin = it },
                                label = { Text("Origin in Bangladesh (District / Upazila)") },
                                placeholder = { Text("Sylhet / Dhaka / Chittagong / Comilla") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = nationality,
                                onValueChange = { nationality = it },
                                label = { Text("Nationality") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. EDUCATION & PROFESSION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "4. Education & Career Background",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BurgundyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = education,
                                onValueChange = { education = it },
                                label = { Text("Education (Degree & Institution)") },
                                placeholder = { Text("MSc in Software Engineering, King's College") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = profession,
                                onValueChange = { profession = it },
                                label = { Text("Profession & Current Job") },
                                placeholder = { Text("Senior Software Consultant") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. MARITAL STATUS & RELIGION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "5. Background & Religion",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BurgundyPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Marital Status:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                maritalOptions.forEach { statusOption ->
                                    val isSelected = maritalStatus == statusOption
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { maritalStatus = statusOption },
                                        label = { Text(statusOption, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFFFF1F2),
                                            selectedLabelColor = BurgundyPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                label = { Text("Height (e.g. 5'10\" or 175 cm)") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = religion,
                                onValueChange = { religion = it },
                                label = { Text("Religion (e.g. Islam / Hinduism / Christianity)") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = religiousDenomination,
                                onValueChange = { religiousDenomination = it },
                                label = { Text("Religious Practice / Denomination (Optional)") },
                                colors = biyeAbroadTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 6. LANGUAGES & INTERESTS
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "6. Languages & Interests",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BurgundyPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Languages Spoken:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                availableLanguages.forEach { lang ->
                                    val isSelected = selectedLanguages.contains(lang)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedLanguages = if (isSelected) selectedLanguages - lang else selectedLanguages + lang
                                        },
                                        label = { Text(lang, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFFFF1F2),
                                            selectedLabelColor = BurgundyPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Interests & Hobbies:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                availableInterests.forEach { interest ->
                                    val isSelected = selectedInterests.contains(interest)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedInterests = if (isSelected) selectedInterests - interest else selectedInterests + interest
                                        },
                                        label = { Text(interest, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFFFF1F2),
                                            selectedLabelColor = BurgundyPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7. BIO & EXPECTATIONS
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "7. Personal Bio & Partner Expectations",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BurgundyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("About Me (Personal Biodata Summary)") },
                                colors = biyeAbroadTextFieldColors(),
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = partnerPreferences,
                                onValueChange = { partnerPreferences = it },
                                label = { Text("Partner Expectations & Qualifications") },
                                colors = biyeAbroadTextFieldColors(),
                                minLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Family Involvement Preference:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                familyOptions.forEach { option ->
                                    val isSelected = familyInvolvementPreference == option
                                    Surface(
                                        onClick = { familyInvolvementPreference = option },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color(0xFFFFF1F2) else PureWhite,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) BurgundyPrimary else GoldBorder
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = option,
                                            color = if (isSelected) BurgundyPrimary else DarkText,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // BOTTOM SAVE BUTTONS
                    Button(
                        onClick = {
                            if (firstName.isBlank() && lastName.isBlank()) {
                                validationError = "Please enter your First Name and Last Name."
                                return@Button
                            }
                            val fullName = "$firstName $lastName".trim()
                            val y = birthYearInput.toIntOrNull() ?: 1998
                            val m = (birthMonthInput.toIntOrNull() ?: 5).coerceIn(1, 12).toString().padStart(2, '0')
                            val d = (birthDayInput.toIntOrNull() ?: 15).coerceIn(1, 31).toString().padStart(2, '0')
                            val finalDob = "$y-$m-$d"
                            val finalAge = manualAgeInput.toIntOrNull()?.coerceIn(18, 100) ?: calculatedAge

                            val updated = profile.copy(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                displayName = fullName,
                                address = address.trim(),
                                dateOfBirth = finalDob,
                                age = finalAge,
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
                                interests = selectedInterests.toList(),
                                partnerPreferences = partnerPreferences.trim(),
                                familyInvolvementPreference = familyInvolvementPreference,
                                photoUrl = photoUrl.trim(),
                                photoUrls = if (photoUrls.isNotEmpty()) photoUrls else if (photoUrl.isNotBlank()) listOf(photoUrl.trim()) else emptyList(),
                                photoPublicId = photoPublicId,
                                cloudinaryPublicId = photoPublicId
                            )
                            onSave(updated)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_biodata_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BurgundyPrimary,
                            contentColor = PureWhite
                        )
                    ) {
                        Text(
                            text = "Save Biodata Changes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BurgundyPrimary)
                    ) {
                        Text(
                            text = "Cancel & Discard",
                            fontSize = 13.sp,
                            color = BurgundyPrimary
                        )
                    }

                    // Generous bottom spacer so page scrolls freely above system gestures and buttons
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (showDatePickerDialog) {
        val y = birthYearInput.toIntOrNull() ?: 1998
        val m = (birthMonthInput.toIntOrNull() ?: 5).toString().padStart(2, '0')
        val d = (birthDayInput.toIntOrNull() ?: 15).toString().padStart(2, '0')
        BiyeAbroadDatePickerDialog(
            initialDate = "$y-$m-$d",
            onDateSelected = { chosenDate ->
                val p = chosenDate.split("-", "/", ".")
                if (p.size == 3) {
                    birthYearInput = if (p[0].length == 4) p[0] else p[2]
                    birthMonthInput = if (p[0].length == 4) p[1] else p[0]
                    birthDayInput = if (p[0].length == 4) p[2] else p[1]
                }
                showDatePickerDialog = false
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }
}
