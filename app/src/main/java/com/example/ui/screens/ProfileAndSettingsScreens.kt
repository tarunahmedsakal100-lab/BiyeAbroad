package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.data.model.FamilyInvitation
import com.example.data.model.FamilyMembership
import com.example.data.model.ProfileManagementRequest
import com.example.data.model.UserProfile
import com.example.ui.components.BiyeAbroadDatePickerDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.GenderSelectionCards
import com.example.ui.components.HelpAndGuidelinesDialog
import com.example.ui.components.PrivacySafetyDialog
import android.graphics.Bitmap
import com.example.ui.components.ProfilePhotoPickerSection
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.data.remote.CloudinaryUploader
import com.example.data.remote.PreparedPhotoUpload
import com.example.ui.components.CloudinaryPhotoUploadDialog
import com.example.ui.components.TierBadge
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.biyeAbroadTextFieldColors
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PhotoUploadUiState

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val myProfile by mainViewModel.myProfile.collectAsState()
    val showFamilyInviteDialog by mainViewModel.showFamilyInviteDialog.collectAsState()
    val authProfile by authViewModel.userProfile.collectAsState()
    val incomingFamilyInvitations by mainViewModel.incomingFamilyInvitations.collectAsState()
    val familyInvitations by mainViewModel.familyInvitations.collectAsState()
    val familyMemberships by mainViewModel.familyMemberships.collectAsState()
    val ownerConsentRequests by mainViewModel.myOwnerConsentRequests.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showMatchmakerConsentDialog by remember { mutableStateOf(false) }
    var showPrivacySafetyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()
    val photoUploadState by mainViewModel.photoUploadState.collectAsState()
    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var pendingPhotoUpload by remember { mutableStateOf<PreparedPhotoUpload?>(null) }
    var photoSelectionFeedback by remember { mutableStateOf<String?>(null) }

    val handlePreparedPhoto: (Result<PreparedPhotoUpload>) -> Unit = { res ->
        res.onSuccess { prepared ->
            photoSelectionFeedback = null
            mainViewModel.resetPhotoUploadState()
            pendingPhotoUpload = prepared
        }.onFailure { err ->
            val msg = err.message ?: "Invalid photo. Allowed formats: JPG, JPEG, PNG, WEBP up to 5 MB."
            photoSelectionFeedback = msg
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    val handleCancelledPhotoSelection: () -> Unit = {
        photoSelectionFeedback = "Photo selection cancelled."
        Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
    }

    // Android Photo Picker (Zero broad storage permissions required)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                handlePreparedPhoto(CloudinaryUploader.preparePhotoFromUri(context, uri))
            }
        } else {
            handleCancelledPhotoSelection()
        }
    }

    // ACTION_OPEN_DOCUMENT Browser (Device folders, Pictures, Downloads, Drive & Gallery)
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                handlePreparedPhoto(CloudinaryUploader.preparePhotoFromUri(context, uri))
            }
        } else {
            handleCancelledPhotoSelection()
        }
    }

    // Generic Gallery / Content Picker fallback (Zero broad storage permissions required)
    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                handlePreparedPhoto(CloudinaryUploader.preparePhotoFromUri(context, uri))
            }
        } else {
            handleCancelledPhotoSelection()
        }
    }

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scope.launch {
                handlePreparedPhoto(CloudinaryUploader.preparePhotoFromBitmap(bitmap))
            }
        } else {
            handleCancelledPhotoSelection()
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (_: Exception) {
                val msg = "No camera app available. Please select a photo from Device Folders."
                photoSelectionFeedback = msg
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        } else {
            val msg = "Camera permission denied. You can choose any photo from Device Folders without extra storage permissions."
            photoSelectionFeedback = msg
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    pendingPhotoUpload?.let { prepared ->
        val fallbackProf = myProfile ?: authProfile ?: UserProfile(
            userId = mainViewModel.currentUserId,
            email = authViewModel.currentUser.value?.email ?: "",
            displayName = authViewModel.currentUser.value?.displayName ?: "Member"
        )
        val isUploading = photoUploadState is PhotoUploadUiState.Uploading
        val uploadProgress = (photoUploadState as? PhotoUploadUiState.Uploading)?.progress ?: 0f
        val errorMsg = (photoUploadState as? PhotoUploadUiState.Error)?.message
        val successMsg = (photoUploadState as? PhotoUploadUiState.Success)?.let { state ->
            if (state.syncedToCloud) {
                "Profile photo uploaded & saved to your BiyeAbroad profile!"
            } else {
                "Profile photo uploaded successfully!"
            }
        }

        CloudinaryPhotoUploadDialog(
            prepared = prepared,
            isUploading = isUploading,
            uploadProgress = uploadProgress,
            errorMessage = errorMsg,
            successMessage = successMsg,
            onConfirmUpload = {
                mainViewModel.uploadAndSaveProfilePhoto(
                    prepared = prepared,
                    fallbackProfile = fallbackProf
                ) { ok, message ->
                    if (ok) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onChooseAnother = {
                mainViewModel.resetPhotoUploadState()
                pendingPhotoUpload = null
                showPhotoSourceDialog = true
            },
            onDismiss = {
                if (!isUploading) {
                    mainViewModel.resetPhotoUploadState()
                    pendingPhotoUpload = null
                }
            }
        )
    }

    if (showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceDialog = false },
            containerColor = PureWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = BurgundyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!myProfile?.photoUrl.isNullOrBlank()) "Replace Profile Photo" else "Select Profile Photo",
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Choose a JPG, JPEG, PNG, or WEBP photo (max 5 MB). If Gallery shows empty folders, tap Browse Photos & Downloads:",
                        fontSize = 12.sp,
                        color = DarkText
                    )

                    Button(
                        onClick = {
                            showPhotoSourceDialog = false
                            try {
                                openDocumentLauncher.launch(
                                    arrayOf("image/*", "image/jpeg", "image/jpg", "image/png", "image/webp")
                                )
                            } catch (_: Exception) {
                                try {
                                    getContentLauncher.launch("image/*")
                                } catch (_: Exception) {
                                    photoSelectionFeedback = "No file or gallery picker found on this device."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_profile_browse_files")
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp), tint = PureWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browse Photos & Downloads (All Folders)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showPhotoSourceDialog = false
                            try {
                                if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    getContentLauncher.launch("image/*")
                                }
                            } catch (_: Exception) {
                                try {
                                    getContentLauncher.launch("image/*")
                                } catch (_: Exception) {
                                    photoSelectionFeedback = "Unable to open Photo Picker on this device."
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_profile_pick_gallery")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = BurgundyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Android Photo Picker (Gallery)", fontSize = 12.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showPhotoSourceDialog = false
                            try {
                                getContentLauncher.launch("image/*")
                            } catch (_: Exception) {
                                photoSelectionFeedback = "No external gallery app found on this device."
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_profile_pick_any_gallery")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = BurgundyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose via Any Gallery App", fontSize = 12.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showPhotoSourceDialog = false
                            val hasCam = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            if (hasCam) {
                                try {
                                    cameraLauncher.launch(null)
                                } catch (_: Exception) {
                                    photoSelectionFeedback = "Unable to start camera on this device."
                                }
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_profile_take_camera")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = BurgundyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo with Camera", fontSize = 12.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoSourceDialog = false }) {
                    Text("Cancel", color = BurgundyPrimary)
                }
            }
        )
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
                .padding(16.dp)
        ) {
        // User Profile Card - Complete Decorated Biodata
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Clickable Avatar with Camera Badge Overlay
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(BurgundyDeep)
                        .border(2.5.dp, GoldAccent, CircleShape)
                        .clickable { showPhotoSourceDialog = true }
                ) {
                    if (!myProfile?.photoUrl.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(myProfile!!.photoUrl)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(48.dp).align(Alignment.Center)
                        )
                    }
                    // Camera icon indicator
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .background(BurgundyPrimary, CircleShape)
                            .border(1.5.dp, PureWhite, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = PureWhite,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = myProfile?.resolvedName() ?: "Member",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BurgundyDeep
                )
                Text(
                    text = "${myProfile?.profession} • ${myProfile?.city}, ${myProfile?.country}",
                    fontSize = 13.sp,
                    color = DarkText,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VerificationBadge(status = myProfile?.verificationStatus ?: "unverified")
                    TierBadge(tier = myProfile?.membershipTier ?: "free")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Edit Biodata & Change Photo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Edit Profile Button (Light background as requested)
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = PureWhite,
                            contentColor = BurgundyPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, BurgundyPrimary),
                        modifier = Modifier.weight(1f).testTag("edit_profile_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Biodata", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BurgundyPrimary)
                    }

                    // Direct Upload / Replace Photo Button
                    Button(
                        onClick = { showPhotoSourceDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BurgundyPrimary,
                            contentColor = PureWhite
                        ),
                        modifier = Modifier.weight(1f).testTag("change_photo_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!myProfile?.photoUrl.isNullOrBlank()) "Replace Photo" else "Upload Photo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                if (!photoSelectionFeedback.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = photoSelectionFeedback!!,
                        fontSize = 11.sp,
                        color = BurgundyPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().testTag("profile_photo_feedback")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = GoldBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Detailed Decorated Biodata Breakdown
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Biodata Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BurgundyPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    val displayFull = "${myProfile?.firstName.orEmpty().ifBlank { myProfile?.displayName ?: "" }} ${myProfile?.lastName.orEmpty()}".trim()
                    ProfileFieldRow(label = "First & Last Name", value = displayFull.ifBlank { "Member" })
                    ProfileFieldRow(label = "Date of Birth", value = myProfile?.dateOfBirth.takeIf { !it.isNullOrBlank() } ?: "1998-05-15")
                    ProfileFieldRow(label = "Gender", value = if (myProfile?.gender == "female") "Female (Bride)" else "Male (Groom)")
                    ProfileFieldRow(label = "Marital Status & Height", value = "${myProfile?.maritalStatus ?: "Never Married"} • ${myProfile?.height ?: "5'7\""}")
                    ProfileFieldRow(label = "Country of Residence", value = "${myProfile?.city}, ${myProfile?.country}")
                    if (!myProfile?.address.isNullOrBlank()) {
                        ProfileFieldRow(label = "Address", value = myProfile?.address ?: "")
                    }
                    ProfileFieldRow(label = "Origin in Bangladesh", value = myProfile?.countryOfOrigin ?: "Bangladesh")
                    ProfileFieldRow(label = "Nationality", value = myProfile?.nationality ?: "Bangladeshi")
                    ProfileFieldRow(label = "Education", value = myProfile?.education ?: "Graduate")
                    ProfileFieldRow(label = "Profession", value = myProfile?.profession ?: "Professional")
                    ProfileFieldRow(label = "Family Involvement", value = myProfile?.familyInvolvementPreference ?: "Joint decision (Self + Family)")

                    // Spoken Languages Chips
                    if (!myProfile?.languages.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Languages Spoken:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            myProfile!!.languages.forEach { lang ->
                                Surface(
                                    color = WarmIvory,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                                ) {
                                    Text(lang, fontSize = 11.sp, color = BurgundyDeep, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }



                    // Short Biography
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("About Me:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(myProfile?.bio ?: "", fontSize = 12.sp, color = DarkText, lineHeight = 16.sp)

                    // Partner Preferences
                    if (!myProfile?.partnerPreferences.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Partner Preferences:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BurgundyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            color = WarmIvory,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = myProfile?.partnerPreferences ?: "",
                                fontSize = 12.sp,
                                color = DarkText,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))



        // Action Menu Options
        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsRow(
                    icon = Icons.Default.VerifiedUser,
                    title = "Identity Verification",
                    subtitle = "Submit Passport / National ID for verified badge",
                    onClick = { mainViewModel.showVerificationDialog(true) }
                )
                HorizontalDivider(color = WarmIvory)
                SettingsRow(
                    icon = Icons.Default.Diamond,
                    title = "Premium Membership",
                    subtitle = "Boost profile, unlimited interests & spotlight",
                    onClick = { mainViewModel.showPremiumDialog(true) }
                )
                HorizontalDivider(color = WarmIvory)
                SettingsRow(
                    icon = Icons.Default.FamilyRestroom,
                    title = "Family & Guardian Participation",
                    subtitle = "Invite parents or trusted family to collaborate",
                    onClick = { mainViewModel.showFamilyInviteDialog(true) }
                )
                HorizontalDivider(color = WarmIvory)
                SettingsRow(
                    icon = Icons.Default.Person,
                    title = "Matchmaker & Consent Management",
                    subtitle = "Review Ghotok requests, approve visibility & revoke access",
                    onClick = { showMatchmakerConsentDialog = true }
                )
                HorizontalDivider(color = WarmIvory)
                SettingsRow(
                    icon = Icons.Default.Security,
                    title = "Privacy & Safety Settings",
                    subtitle = "Confidentiality, photo visibility, and security rules",
                    onClick = { showPrivacySafetyDialog = true }
                )
                HorizontalDivider(color = WarmIvory)
                SettingsRow(
                    icon = Icons.Default.Help,
                    title = "Help & Community Guidelines",
                    subtitle = "Respectful matrimony guidelines & support",
                    onClick = { showHelpDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Actions
        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Log Out",
                    subtitle = "Sign out from this device",
                    onClick = { authViewModel.signOut(context) }
                )
                HorizontalDivider(color = WarmIvory)
                SettingsRow(
                    icon = Icons.Default.Delete,
                    title = "Delete Account & Biodata",
                    subtitle = "Permanently remove your profile and data",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { showDeleteDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(140.dp))
    }
}

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = PureWhite,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Delete BiyeAbroad Account?", color = BurgundyDeep, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This action is permanent and will delete your matrimonial profile, biodata, and all message history from our cloud servers in compliance with data privacy policies.",
                    fontSize = 13.sp,
                    color = MutedText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        authViewModel.signOut(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete My Account", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = BurgundyPrimary)
                }
            }
        )
    }

    if (showEditProfileDialog && myProfile != null) {
        EditProfileDialog(
            profile = myProfile!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                mainViewModel.updateProfile(updated)
                showEditProfileDialog = false
            },
            onPersistUploadedPhoto = { secureUrl, publicId ->
                mainViewModel.saveUploadedPhotoToProfile(
                    secureUrl = secureUrl,
                    publicId = publicId,
                    fallbackProfile = myProfile ?: authProfile
                )
            }
        )
    }

    if (showFamilyInviteDialog) {
        FamilyManagementDialog(
            invitations = familyInvitations,
            incomingInvitations = incomingFamilyInvitations,
            memberships = familyMemberships,
            onDismiss = { mainViewModel.showFamilyInviteDialog(false) },
            onSendInvite = { email, relation, permissions, onComplete ->
                mainViewModel.sendFamilyInvitation(email, relation, permissions, onComplete)
            },
            onAcceptInvitation = { code, onComplete ->
                mainViewModel.acceptFamilyInvitation(code, myProfile?.resolvedName() ?: "Family Member", onComplete)
            },
            onRevokeMembership = { membershipId ->
                mainViewModel.revokeFamilyMembership(membershipId)
            },
            onRevokeInvitation = { invitationId ->
                mainViewModel.revokeFamilyInvitation(invitationId)
            },
            inviterName = myProfile?.resolvedName() ?: "BiyeAbroad Member"
        )
    }

    if (showMatchmakerConsentDialog) {
        MatchmakerConsentDialog(
            requests = ownerConsentRequests,
            onDismiss = { showMatchmakerConsentDialog = false },
            onRespond = { reqId, approved ->
                mainViewModel.respondToProfileOwnerConsent(reqId, approved)
            },
            onClaimByToken = { token, onComplete ->
                mainViewModel.claimMatchmakerByToken(token, onComplete)
            },
            onRevoke = { managedProfileId, matchmakerId ->
                mainViewModel.revokeMatchmakerAccess(managedProfileId, matchmakerId)
            }
        )
    }

    if (showPrivacySafetyDialog) {
        PrivacySafetyDialog(
            currentProfile = myProfile,
            onDismiss = { showPrivacySafetyDialog = false },
            onSave = { updated ->
                mainViewModel.updateProfile(updated)
                showPrivacySafetyDialog = false
            }
        )
    }

    if (showHelpDialog) {
        HelpAndGuidelinesDialog(onDismiss = { showHelpDialog = false })
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color = BurgundyPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = subtitle, fontSize = 11.sp, color = MutedText)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = GoldBorder,
            modifier = Modifier.size(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationSubmissionDialog(
    onDismiss: () -> Unit,
    onSubmit: (documentType: String, documentNumber: String) -> Unit
) {
    var docType by remember { mutableStateOf("Passport") }
    var docNumber by remember { mutableStateOf("") }
    val docOptions = listOf("Passport", "National ID (NID)", "Driving License")
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BurgundyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Identity Verification", fontWeight = FontWeight.Bold, color = BurgundyDeep)
            }
        },
        text = {
            Column {
                Text(
                    "To maintain trust and security across the worldwide Bangladeshi community, verification documents are reviewed strictly by authorized BiyeAbroad administrators.",
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = docType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Document Type") },
                        colors = biyeAbroadTextFieldColors(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        docOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    docType = opt
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text("Document / Passport Number") },
                    placeholder = { Text("e.g. A01234567") },
                    colors = biyeAbroadTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("input_verification_doc")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (docNumber.isNotBlank()) {
                        onSubmit(docType, docNumber.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
            ) {
                Text("Submit for Review", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BurgundyPrimary)
            }
        }
    )
}

@Composable
fun ReportUserDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, details: String) -> Unit
) {
    var reason by remember { mutableStateOf("Inappropriate Communication") }
    var details by remember { mutableStateOf("") }
    val reasons = listOf("Inappropriate Communication", "Suspicious or Fake Profile", "Harassment", "Commercial Solicitation", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = { Text("Report ${profile.displayName}", fontWeight = FontWeight.Bold, color = BurgundyDeep) },
        text = {
            Column {
                Text("Select reason for report:", fontSize = 12.sp, color = MutedText)
                Spacer(modifier = Modifier.height(6.dp))

                reasons.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { reason = r }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(16.dp),
                            shape = CircleShape,
                            color = if (reason == r) BurgundyPrimary else PureWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyPrimary)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = r, fontSize = 12.sp, color = DarkText)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Additional Details (Optional)") },
                    colors = biyeAbroadTextFieldColors(),
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(reason, details.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Submit Report", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BurgundyPrimary)
            }
        }
    )
}

@Composable
fun FamilyInviteDialog(
    onDismiss: () -> Unit,
    onSubmit: (guardianEmail: String, relation: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("Parent / Guardian") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = BurgundyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Family Collaboration", fontWeight = FontWeight.Bold, color = BurgundyDeep)
            }
        },
        text = {
            Column {
                Text(
                    "Invite your parents or family member to review profiles with you. Family members can view shortlisted biodatas with your explicit permission.",
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Family Member's Email Address") },
                    placeholder = { Text("parent@example.com") },
                    colors = biyeAbroadTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text("Relationship (e.g. Father, Mother, Sister)") },
                    colors = biyeAbroadTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        onSubmit(email.trim(), relation.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
            ) {
                Text("Send Invitation", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = BurgundyPrimary)
            }
        }
    )
}

@Composable
fun PremiumMembershipDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Diamond, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("BiyeAbroad Premium", fontWeight = FontWeight.Bold, color = BurgundyDeep)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Elevate your marriage search with verified matchmaking privileges.", fontSize = 12.sp, color = MutedText)
                Spacer(modifier = Modifier.height(12.dp))

                PlanCard(name = "Free Member", price = "Free", features = listOf("Create biodata", "Browse global profiles", "Receive interests"))
                Spacer(modifier = Modifier.height(8.dp))
                PlanCard(name = "Silver Plan", price = "$19 / mo", features = listOf("Send 30 interests/mo", "Direct chat with matches", "View visitor count"))
                Spacer(modifier = Modifier.height(8.dp))
                PlanCard(name = "Gold Plan", price = "$39 / 3 mos", highlighted = true, features = listOf("Unlimited interests", "Priority search spotlight", "Fast-track ID verification", "Family collaboration seat"))
                Spacer(modifier = Modifier.height(8.dp))
                PlanCard(name = "Platinum VIP", price = "$79 / 6 mos", features = listOf("All Gold benefits", "Personal Match Advisor assistance", "Top profile badge", "Confidential phone exchange"))
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
            ) {
                Text("Select Plan", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later", color = BurgundyPrimary)
            }
        }
    )
}

@Composable
fun PlanCard(
    name: String,
    price: String,
    features: List<String>,
    highlighted: Boolean = false
) {
    Surface(
        color = if (highlighted) BurgundyPrimary else PureWhite,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (highlighted) GoldAccent else GoldBorder.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (highlighted) GoldLight else BurgundyDeep
                )
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (highlighted) PureWhite else GoldAccent
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            features.forEach { feat ->
                Text(
                    text = "✓ $feat",
                    fontSize = 11.sp,
                    color = if (highlighted) PureWhite.copy(alpha = 0.9f) else DarkText.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ProfileFieldRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(text = label, fontSize = 12.sp, color = MutedText, modifier = Modifier.weight(1f))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FamilyManagementDialog(
    invitations: List<FamilyInvitation>,
    incomingInvitations: List<FamilyInvitation> = emptyList(),
    memberships: List<FamilyMembership>,
    onDismiss: () -> Unit,
    onSendInvite: (email: String, relation: String, permissions: List<String>, onResult: (Boolean, String?) -> Unit) -> Unit,
    onAcceptInvitation: (code: String, onResult: (Boolean, String?) -> Unit) -> Unit = { _, cb -> cb(false, null) },
    onRevokeMembership: (membershipId: String) -> Unit,
    onRevokeInvitation: (invitationId: String) -> Unit,
    inviterName: String = "BiyeAbroad Member"
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Guardian") }
    val relationOptions = listOf("Father", "Mother", "Guardian", "Brother", "Sister", "Uncle", "Aunt")
    var canChat by remember { mutableStateOf(true) }
    var canViewMatches by remember { mutableStateOf(true) }
    var activeTab by remember { mutableIntStateOf(0) }

    var joinCode by remember { mutableStateOf("") }
    var isJoining by remember { mutableStateOf(false) }

    var isSending by remember { mutableStateOf(false) }
    var inviteDispatched by remember { mutableStateOf(false) }
    var dispatchedEmail by remember { mutableStateOf("") }
    var dispatchedToken by remember { mutableStateOf("") }

    val buildInviteMessage: (String, String, String) -> String = { toEmail, rel, token ->
        val sb = StringBuilder()
        sb.append("Assalamu Alaikum / Greetings,\n\n")
        sb.append("$inviterName has invited you as their $rel on BiyeAbroad — The dignified Bangladeshi Matrimonial Platform.\n\n")
        sb.append("As an authorized family member, you can:\n")
        sb.append("• Review prospective bride/groom matrimonial matches\n")
        sb.append("• Participate in consensual family chat discussions\n\n")
        sb.append("Your Family Invitation Code: $token\n\n")
        sb.append("How to join:\n")
        sb.append("1. Open the BiyeAbroad App on Android\n")
        sb.append("2. Go to Profile > Family Circle > 'Join' tab\n")
        sb.append("3. Enter your invitation code: $token\n")
        sb.append("4. You will instantly be linked to $inviterName's family circle.\n\n")
        sb.append("Together Forever — BiyeAbroad Matrimony")
        sb.toString()
    }

    val launchEmailClient: (String, String, String) -> Unit = { toEmail, rel, token ->
        val subject = "BiyeAbroad: Family Matrimonial Invitation from $inviterName"
        val body = buildInviteMessage(toEmail, rel, token)
        try {
            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$toEmail")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(Intent.createChooser(mailIntent, "Send Family Invitation via Email"))
        } catch (e: Exception) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.setPrimaryClip(ClipData.newPlainText("Family Invitation", body))
            Toast.makeText(context, "Email client unavailable. Invitation copied to clipboard!", Toast.LENGTH_LONG).show()
        }
    }

    val shareInvite: (String, String, String) -> Unit = { toEmail, rel, token ->
        val subject = "BiyeAbroad: Family Matrimonial Invitation from $inviterName"
        val body = buildInviteMessage(toEmail, rel, token)
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Matrimonial Invitation"))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open share menu", Toast.LENGTH_SHORT).show()
        }
    }

    val copyToClipboard: (String, String, String) -> Unit = { toEmail, rel, token ->
        val body = buildInviteMessage(toEmail, rel, token)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("Family Invitation", body))
        Toast.makeText(context, "Invitation message & code copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = BurgundyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Family Circle & Permissions", fontWeight = FontWeight.Bold, color = BurgundyDeep, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Informational Notice
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                ) {
                    Text(
                        text = "Invited family members sign in with their own independent accounts. They cannot alter your profile, grant themselves administrative roles, or elevate permissions.",
                        fontSize = 11.sp,
                        color = DarkText,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 15.sp
                    )
                }

                // Tab Switcher: Invite vs Join vs Active Members
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = WarmIvory,
                    contentColor = BurgundyPrimary
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Invite", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Join", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = {
                            val pendingCount = invitations.count { it.status == "pending" }
                            Text("Members (${memberships.size + pendingCount})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    )
                }

                if (activeTab == 0) {
                    if (inviteDispatched) {
                        // Invitation Success & Sharing Banner
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Invitation Registered Successfully!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = SuccessGreen
                                    )
                                }
                                Text(
                                    "Invitation code generated for $dispatchedEmail as $relationship.",
                                    fontSize = 11.sp,
                                    color = DarkText
                                )
                                val inviteLink = "https://biyeabroad.web.app/family/join?code=$dispatchedToken"
                                val inviteShareText = "Assalamu Alaikum,\n$inviterName has invited you as their $relationship on BiyeAbroad Bangladeshi Matrimony.\n\nJoin Family Circle Link:\n$inviteLink\n\nFamily Invitation Code: $dispatchedToken\n\nOpen BiyeAbroad app > Profile > Family Circle > 'Join' tab and enter the code."

                                Surface(
                                    color = PureWhite,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("INVITATION CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                                        Text(dispatchedToken, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BurgundyPrimary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("JOINING LINK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                                        Text(inviteLink, fontSize = 10.sp, color = DarkText)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            clipboard?.setPrimaryClip(ClipData.newPlainText("Family Invitation Link", inviteShareText))
                                            Toast.makeText(context, "Invitation link & code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { shareInvite(dispatchedEmail, relationship, dispatchedToken) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = BurgundyPrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", fontSize = 11.sp, color = BurgundyPrimary)
                                    }
                                }

                                TextButton(
                                    onClick = { launchEmailClient(dispatchedEmail, relationship, dispatchedToken) },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = MutedText)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Open in Email App (Optional)", fontSize = 11.sp, color = MutedText)
                                }

                                TextButton(
                                    onClick = {
                                        inviteDispatched = false
                                        email = ""
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Invite Another Family Member", fontSize = 11.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // TAB 0: Send Family Invitation Form
                        Text("Invitee Email Address:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("e.g. parent@gmail.com", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("family_invite_email_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = biyeAbroadTextFieldColors(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Relationship to Seeker:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            relationOptions.forEach { rel ->
                                val isSelected = relationship == rel
                                Surface(
                                    color = if (isSelected) BurgundyPrimary else WarmIvory,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BurgundyPrimary else GoldBorder),
                                    modifier = Modifier.clickable { relationship = rel }
                                ) {
                                    Text(
                                        text = rel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PureWhite else DarkText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Authorization & Access Permissions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("View Matrimonial Matches", fontSize = 12.sp, color = DarkText)
                            Switch(
                                checked = canViewMatches,
                                onCheckedChange = { canViewMatches = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PureWhite, checkedTrackColor = BurgundyPrimary)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Participate in Family Chat Rooms", fontSize = 12.sp, color = DarkText)
                            Switch(
                                checked = canChat,
                                onCheckedChange = { canChat = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = PureWhite, checkedTrackColor = BurgundyPrimary)
                            )
                        }

                        Button(
                            onClick = {
                                isSending = true
                                val perms = mutableListOf<String>()
                                if (canViewMatches) perms.add("view_matches")
                                if (canChat) perms.add("family_chat")
                                val target = email.trim()
                                onSendInvite(target, relationship, perms) { success, tokenOrErr ->
                                    isSending = false
                                    if (success && !tokenOrErr.isNullOrBlank()) {
                                        inviteDispatched = true
                                        dispatchedEmail = target
                                        dispatchedToken = tokenOrErr
                                    } else {
                                        Toast.makeText(
                                            context,
                                            tokenOrErr ?: "Failed to create invitation link.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            enabled = email.isNotBlank() && email.contains("@") && !isSending,
                            modifier = Modifier.fillMaxWidth().testTag("send_family_invite_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (isSending) "Creating Join Link..." else "Create & Send Join Link",
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else if (activeTab == 1) {
                    // TAB 1: Incoming Invitations + Join Family Circle by Code
                    if (incomingInvitations.isNotEmpty()) {
                        Text("Invitations Sent to You (${incomingInvitations.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        incomingInvitations.forEach { inc ->
                            Surface(
                                color = Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "From ${inc.inviterName}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = BurgundyDeep
                                        )
                                        Text(
                                            text = "Role: ${inc.relationship} • Code: ${inc.token}",
                                            fontSize = 10.sp,
                                            color = DarkText
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            isJoining = true
                                            onAcceptInvitation(inc.token) { ok, err ->
                                                isJoining = false
                                                if (ok) {
                                                    Toast.makeText(context, "Joined family circle!", Toast.LENGTH_SHORT).show()
                                                    activeTab = 2
                                                } else {
                                                    Toast.makeText(context, err ?: "Could not accept invitation.", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Accept", fontSize = 11.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text("Enter Family Invitation Code:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Text("Paste or type the invitation code (e.g. INV-XXXXXXXX) or joining link shared with you.", fontSize = 11.sp, color = MutedText)

                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { raw ->
                            val clean = if (raw.contains("code=")) raw.substringAfter("code=").substringBefore("&") else raw
                            joinCode = clean.trim().uppercase()
                        },
                        placeholder = { Text("e.g. INV-A1B2C3D4", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("family_join_code_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = biyeAbroadTextFieldColors(),
                        singleLine = true,
                        trailingIcon = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            IconButton(onClick = {
                                val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()?.trim().orEmpty()
                                if (clip.isNotBlank()) {
                                    val token = when {
                                        clip.contains("INV-") -> "INV-" + clip.substringAfter("INV-").take(8)
                                        clip.contains("code=") -> clip.substringAfter("code=").substringBefore("&")
                                        else -> clip
                                    }
                                    joinCode = token.trim().uppercase()
                                }
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Paste Code", tint = BurgundyPrimary)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (joinCode.isNotBlank()) {
                                isJoining = true
                                onAcceptInvitation(joinCode.trim()) { ok, err ->
                                    isJoining = false
                                    if (ok) {
                                        joinCode = ""
                                        Toast.makeText(context, "Successfully joined family circle!", Toast.LENGTH_SHORT).show()
                                        activeTab = 2
                                    } else {
                                        Toast.makeText(context, err ?: "Invalid invitation code.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        enabled = joinCode.isNotBlank() && !isJoining,
                        modifier = Modifier.fillMaxWidth().testTag("join_family_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            if (isJoining) "Joining..." else "Join Family Circle (পরিবারে যুক্ত হোন)",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        color = WarmIvory,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("💡 How Family Circle Works:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
                            Text("• Both parties must consent to family access.\n• As a family member, you can review prospective matches and participate in family discussions.\n• Your account remains completely independent.", fontSize = 10.sp, color = DarkText, lineHeight = 14.sp)
                        }
                    }
                } else {
                    // TAB 2: Active Family Memberships & Pending Invitations
                    val pending = invitations.filter { it.status == "pending" }
                    if (pending.isNotEmpty()) {
                        Text("Pending Invitations", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
                        pending.forEach { inv ->
                            Surface(
                                color = WarmIvory,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(inv.inviteeEmail, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText)
                                        Text("${inv.relationship} • Code: ${inv.token}", fontSize = 10.sp, color = BurgundyPrimary, fontWeight = FontWeight.SemiBold)
                                        Text("Pending Acceptance", fontSize = 10.sp, color = MutedText)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = { launchEmailClient(inv.inviteeEmail, inv.relationship, inv.token) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Email, contentDescription = "Resend Email", tint = BurgundyPrimary, modifier = Modifier.size(16.dp))
                                        }
                                        OutlinedButton(
                                            onClick = { onRevokeInvitation(inv.invitationId) },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Cancel", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Active Family Members", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
                    val activeMembers = memberships.filter { it.status == "active" }
                    if (activeMembers.isEmpty()) {
                        Text("No active family members yet. Sent invites appear above.", fontSize = 11.sp, color = MutedText)
                    } else {
                        activeMembers.forEach { mem ->
                            Surface(
                                color = PureWhite,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(mem.familyMemberName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(mem.relationship, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                        Text(mem.familyMemberEmail.ifBlank { "Independent Account" }, fontSize = 10.sp, color = MutedText)
                                    }
                                    OutlinedButton(
                                        onClick = { onRevokeMembership(mem.membershipId) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Revoke", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = BurgundyPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun MatchmakerConsentDialog(
    requests: List<ProfileManagementRequest>,
    onDismiss: () -> Unit,
    onRespond: (String, Boolean) -> Unit,
    onClaimByToken: (String, (Boolean, String?) -> Unit) -> Unit = { _, cb -> cb(false, null) },
    onRevoke: (String, String) -> Unit
) {
    val context = LocalContext.current
    val pendingRequests = requests.filter { it.status == "pending" }
    val activeLinked = requests.filter { it.status == "accepted" || it.status == "approved" }
    var claimCodeInput by remember { mutableStateOf("") }
    var isClaiming by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = BurgundyPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Matchmaker (Ghotok) Representation",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyPrimary
                    )
                    Text(
                        text = "Candidate Representation Consent",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = WarmIvory,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Under BiyeAbroad privacy rules, a Matchmaker (Ghotok) cannot publish your biodata or suggest matches without your two-sided consent. Matchmakers never gain access to your private member chats or passwords, and you may revoke their representation at any time.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Direct Verification Code Entry (when Ghotok sends a consent link/code)
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Have a Matchmaker Consent Code?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDeep
                        )
                        OutlinedTextField(
                            value = claimCodeInput,
                            onValueChange = { raw ->
                                val parsed = if (raw.contains("token=")) raw.substringAfter("token=").substringBefore("&") else raw
                                claimCodeInput = parsed.trim().uppercase()
                            },
                            placeholder = { Text("e.g. CON-XXXXXXXX", fontSize = 12.sp) },
                            colors = biyeAbroadTextFieldColors(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("matchmaker_claim_code_input")
                        )
                        Button(
                            onClick = {
                                if (claimCodeInput.isNotBlank()) {
                                    isClaiming = true
                                    onClaimByToken(claimCodeInput.trim()) { ok, msg ->
                                        isClaiming = false
                                        if (ok) {
                                            claimCodeInput = ""
                                            Toast.makeText(context, "Consent approved!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, msg ?: "Code not found.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            enabled = claimCodeInput.isNotBlank() && !isClaiming,
                            colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (isClaiming) "Verifying..." else "Verify & Approve Consent Code",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }

                Text(
                    text = "Pending Consent Requests (${pendingRequests.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyDeep
                )

                if (pendingRequests.isEmpty()) {
                    Text(
                        text = "No pending matchmaker requests for your email right now. If your Ghotok gave you a code, enter it above.",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                } else {
                    pendingRequests.forEach { req ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Matchmaker: ${req.matchmakerName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Requested Permissions: ${req.grantedPermissions.joinToString(", ")}",
                                    fontSize = 10.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onRespond(req.requestId, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(34.dp)
                                    ) {
                                        Text("Accept & Approve", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { onRespond(req.requestId, false) },
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                                        modifier = Modifier.weight(0.7f).height(34.dp)
                                    ) {
                                        Text("Decline", fontSize = 11.sp, color = Color(0xFFD32F2F))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Active Authorized Matchmakers (${activeLinked.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyDeep
                )

                if (activeLinked.isEmpty()) {
                    Text(
                        text = "You are currently not represented by any external Matchmaker.",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                } else {
                    activeLinked.forEach { active ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = active.matchmakerName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BurgundyPrimary
                                    )
                                    Surface(
                                        color = SuccessGreen.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Authorized",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Permissions: ${active.grantedPermissions.joinToString(", ")}",
                                    fontSize = 10.sp,
                                    color = MutedText
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { onRevoke(active.managedProfileId, active.matchmakerId) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text("Revoke Matchmaker Representation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = BurgundyPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}


