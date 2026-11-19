package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManagedProfile
import com.example.data.model.MatchmakerProfile
import com.example.data.model.MatchSuggestion
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Email
import com.example.ui.theme.biyeAbroadTextFieldColors
import com.example.data.model.UserProfile
import com.example.ui.components.BiyeAbroadLogo
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
import com.example.ui.viewmodel.AccountType
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class MatchmakerTab(val title: String, val titleBn: String) {
    OVERVIEW("Overview", "Overview"),
    PROFILES("My Profiles", "My Profiles"),
    ADD_PROFILE("Add Profile", "Add Profile"),
    SUGGESTIONS("Suggestions", "Suggestions"),
    PLAN_PROFILE("Plan & Profile", "Plan & Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakerDashboardScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onExit: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf(MatchmakerTab.OVERVIEW) }
    val matchmakerProfile by mainViewModel.myMatchmakerProfile.collectAsState()
    val authMatchmaker by authViewModel.matchmakerProfile.collectAsState()
    val currentMm = matchmakerProfile ?: authMatchmaker

    val managedProfiles by mainViewModel.myManagedProfiles.collectAsState()
    val approvedProfiles by mainViewModel.myApprovedManagedProfiles.collectAsState()
    val matchSuggestions by mainViewModel.myMatchSuggestions.collectAsState()
    val candidatePool by mainViewModel.discoveryProfiles.collectAsState()

    var showSuggestModalForProfile by remember { mutableStateOf<ManagedProfile?>(null) }
    var generatedTokenDialog by remember { mutableStateOf<Triple<String, String, String>?>(null) } // Triple(token, ownerEmail, candidateName)

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BurgundyPrimary,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BiyeAbroadLogo(size = 38.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "BiyeAbroad Ghotok",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldAccent.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent)
                                ) {
                                    Text(
                                        text = "Dashboard",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GoldLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = currentMm?.fullName ?: "Professional Matchmaker",
                                fontSize = 11.sp,
                                color = PureWhite.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Verification Status Tag
                        val vStatus = currentMm?.verificationStatus ?: "pending"
                        val (vLabel, vColor) = when (vStatus) {
                            "verified" -> "Verified" to SuccessGreen
                            "suspended" -> "Suspended" to Color(0xFFD32F2F)
                            else -> "Pending Review" to GoldAccent
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = vColor.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, vColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (vStatus == "verified") {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Matchmaker",
                                        tint = vColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = vLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = vColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (onExit != null) onExit() else authViewModel.enterDemoMode(AccountType.USER)
                            },
                            modifier = Modifier.testTag("switch_role_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Exit / Switch Mode",
                                tint = PureWhite
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = PureWhite,
                contentColor = BurgundyPrimary,
                tonalElevation = 6.dp
            ) {
                MatchmakerTab.values().forEach { tab ->
                    val selected = selectedTab == tab
                    val icon = when (tab) {
                        MatchmakerTab.OVERVIEW -> Icons.Default.Star
                        MatchmakerTab.PROFILES -> Icons.Default.People
                        MatchmakerTab.ADD_PROFILE -> Icons.Default.Add
                        MatchmakerTab.SUGGESTIONS -> Icons.Default.Favorite
                        MatchmakerTab.PLAN_PROFILE -> Icons.Default.Person
                    }

                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = icon, contentDescription = tab.title) },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BurgundyPrimary,
                            selectedTextColor = BurgundyPrimary,
                            indicatorColor = GoldLight.copy(alpha = 0.4f),
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText
                        ),
                        modifier = Modifier.testTag("tab_matchmaker_${tab.name.lowercase()}")
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(WarmIvory)
        ) {
            when (selectedTab) {
                MatchmakerTab.OVERVIEW -> MatchmakerOverviewView(
                    profile = currentMm,
                    managedProfiles = managedProfiles,
                    matchSuggestions = matchSuggestions,
                    onNavigateTab = { selectedTab = it }
                )
                MatchmakerTab.PROFILES -> ManagedProfilesListView(
                    profiles = managedProfiles,
                    onAddClick = { selectedTab = MatchmakerTab.ADD_PROFILE },
                    onSuggestClick = { showSuggestModalForProfile = it },
                    onSendConsentClick = { prof ->
                        val tok = prof.inviteToken.ifBlank { "CON-${prof.managedProfileId.take(8).uppercase()}" }
                        generatedTokenDialog = Triple(tok, prof.ownerEmail, prof.fullName)
                    }
                )
                MatchmakerTab.ADD_PROFILE -> AddManagedProfileForm(
                    currentMm = currentMm,
                    managedCount = managedProfiles.size,
                    maxQuota = currentMm?.maxManagedProfiles ?: 10,
                    onSave = { newProfile ->
                        mainViewModel.saveManagedProfile(
                            profile = newProfile,
                            onSuccess = { tokenId ->
                                generatedTokenDialog = Triple(tokenId, newProfile.ownerEmail, newProfile.fullName)
                                selectedTab = MatchmakerTab.PROFILES
                            }
                        )
                    }
                )
                MatchmakerTab.SUGGESTIONS -> {
                    val combinedApproved = remember(managedProfiles, approvedProfiles) {
                        (managedProfiles.filter { it.consentStatus == "approved" } + approvedProfiles)
                            .distinctBy { it.managedProfileId }
                    }
                    MatchSuggestionsView(
                        suggestions = matchSuggestions,
                        approvedProfiles = combinedApproved,
                        candidatePool = candidatePool,
                        onSubmitSuggestion = { pA, pB, notes ->
                            mainViewModel.submitMatchSuggestion(
                                profileAId = pA.managedProfileId,
                                profileAName = pA.fullName,
                                profileAOwnerId = pA.matchmakerId,
                                profileBId = pB.userId,
                                profileBName = pB.displayName,
                                profileBOwnerId = pB.userId,
                                notes = notes
                            )
                        }
                    )
                }
                MatchmakerTab.PLAN_PROFILE -> MatchmakerPlanAndProfileView(
                    matchmaker = currentMm,
                    managedCount = managedProfiles.size
                )
            }
        }
    }

    val context = LocalContext.current

    // Modal when a new profile is saved or Resend Consent is tapped, showing the consent link / code / email option
    generatedTokenDialog?.let { (token, ownerEmail, candidateName) ->
        val claimLink = "https://biyeabroad.web.app/consent?token=$token"
        val mmName = currentMm?.fullName ?: "BiyeAbroad Matchmaker"
        val shareMessage = "Assalamu Alaikum,\n$mmName has created a BiyeAbroad matrimonial biodata for $candidateName.\n\nPlease review and approve representation by clicking the link or entering the consent code inside your BiyeAbroad app (Profile -> Matchmaker & Consent Management):\n\nConsent Link: $claimLink\nVerification Code: $token\n\nTogether Forever — BiyeAbroad Matrimony"

        AlertDialog(
            onDismissRequest = { generatedTokenDialog = null },
            shape = RoundedCornerShape(16.dp),
            containerColor = PureWhite,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Biodata Saved & Consent Link Ready",
                        fontWeight = FontWeight.Bold,
                        color = BurgundyPrimary,
                        fontSize = 15.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "The profile is saved under 'Pending Consent' and sent in-app to ${ownerEmail.ifBlank { "the candidate" }}. You can also copy, share, or email the consent link below:",
                        fontSize = 12.sp,
                        color = DarkText
                    )
                    Surface(
                        color = WarmIvory,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Consent Verification Code:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = token,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = claimLink,
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("Consent Link", shareMessage))
                                Toast.makeText(context, "Consent link & code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = BurgundyPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Link", fontSize = 11.sp, color = BurgundyPrimary)
                        }

                        Button(
                            onClick = {
                                try {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "BiyeAbroad: Profile Consent Invitation for $candidateName")
                                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Consent Invitation"))
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Could not open share menu", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = PureWhite)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 11.sp, color = PureWhite)
                        }
                    }

                    if (ownerEmail.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${ownerEmail.trim()}")
                                        putExtra(Intent.EXTRA_SUBJECT, "BiyeAbroad: Consent Request for $candidateName")
                                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                                    }
                                    context.startActivity(Intent.createChooser(emailIntent, "Send Consent Request Email"))
                                } catch (_: Exception) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    clipboard?.setPrimaryClip(ClipData.newPlainText("Consent Email", shareMessage))
                                    Toast.makeText(context, "Email app not found. Consent link copied!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = BurgundyDeep)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send Consent Email to $ownerEmail", fontSize = 11.sp, color = BurgundyDeep, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Text(
                        text = "The profile remains private until approved. If the candidate signs in with $ownerEmail, it appears automatically in their 'Matchmaker & Consent Management' section.",
                        fontSize = 10.sp,
                        color = MutedText,
                        lineHeight = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { generatedTokenDialog = null }
                ) {
                    Text("Close", color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal to suggest a match from managed profile to discovery candidates
    showSuggestModalForProfile?.let { managedProf ->
        SuggestMatchDialog(
            managedProfile = managedProf,
            candidatePool = candidatePool,
            onDismiss = { showSuggestModalForProfile = null },
            onSend = { targetUser, notes ->
                mainViewModel.submitMatchSuggestion(
                    profileAId = managedProf.managedProfileId,
                    profileAName = managedProf.fullName,
                    profileAOwnerId = managedProf.matchmakerId,
                    profileBId = targetUser.userId,
                    profileBName = targetUser.displayName,
                    profileBOwnerId = targetUser.userId,
                    notes = notes
                )
                showSuggestModalForProfile = null
            }
        )
    }
}

// ==========================================
// TAB 1: OVERVIEW
// ==========================================

@Composable
fun MatchmakerOverviewView(
    profile: MatchmakerProfile?,
    managedProfiles: List<ManagedProfile>,
    matchSuggestions: List<MatchSuggestion>,
    onNavigateTab: (MatchmakerTab) -> Unit
) {
    val totalCount = managedProfiles.size
    val approvedCount = managedProfiles.count { it.consentStatus == "approved" }
    val pendingConsentCount = managedProfiles.count { it.consentStatus == "pending_consent" }
    val pendingReviewCount = managedProfiles.count { it.consentStatus == "pending_review" }
    val activeSuggestionsCount = matchSuggestions.count { it.status == "pending" }
    val maxQuota = profile?.maxManagedProfiles ?: 10

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card with Launch Plan Badge
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome, Ghotok Sahab",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )
                            Text(
                                text = "Official Matchmaker Partner Workspace",
                                fontSize = 12.sp,
                                color = MutedText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GoldLight.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Plan",
                                    tint = BurgundyDeep,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Free Launch Plan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyDeep
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plan Quota Meter
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Managed Profiles Quota",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "$totalCount / $maxQuota Used",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val progress = if (maxQuota > 0) (totalCount.toFloat() / maxQuota.toFloat()).coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BurgundyPrimary,
                            trackColor = GoldLight.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigateTab(MatchmakerTab.ADD_PROFILE) },
                            colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Profile", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { onNavigateTab(MatchmakerTab.SUGGESTIONS) },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Matchmaking", fontSize = 13.sp, color = BurgundyPrimary)
                        }
                    }
                }
            }
        }

        // Summary Metric Grid
        item {
            Text(
                text = "Dashboard Summary",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BurgundyPrimary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Total Managed",
                        count = totalCount.toString(),
                        subtitle = "Candidates in care",
                        icon = Icons.Default.People,
                        color = BurgundyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Approved & Live",
                        count = approvedCount.toString(),
                        subtitle = "Owner consented",
                        icon = Icons.Default.CheckCircle,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Pending Consent",
                        count = pendingConsentCount.toString(),
                        subtitle = "Awaiting owner response",
                        icon = Icons.Default.Schedule,
                        color = GoldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Active Matches",
                        count = activeSuggestionsCount.toString(),
                        subtitle = "Suggestions submitted",
                        icon = Icons.Default.Favorite,
                        color = BurgundyDeep,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Policy & Consent Transparency Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WarmIvory),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Consent Policy",
                            tint = BurgundyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consent & Security Guarantee",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• All candidates remain in 'Pending Consent' until the owner verifies via email/SMS link.\n• Matchmakers cannot access private member-to-member chat conversations.\n• Candidate owners may revoke matchmaking representation at any time from their profile settings.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    count: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MutedText
            )
        }
    }
}

// ==========================================
// TAB 2: MANAGED PROFILES LIST
// ==========================================

@Composable
fun ManagedProfilesListView(
    profiles: List<ManagedProfile>,
    onAddClick: () -> Unit,
    onSuggestClick: (ManagedProfile) -> Unit,
    onSendConsentClick: (ManagedProfile) -> Unit
) {
    var filterType by remember { mutableStateOf("all") } // "all", "bride", "groom", "approved", "pending"

    val filtered = profiles.filter { p ->
        when (filterType) {
            "bride" -> p.profileType.equals("bride", ignoreCase = true)
            "groom" -> p.profileType.equals("groom", ignoreCase = true)
            "approved" -> p.consentStatus == "approved"
            "pending" -> p.consentStatus == "pending_consent" || p.consentStatus == "pending_review"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Managed Profiles (${profiles.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyPrimary
                    )
                    Text(
                        text = "Bride & Groom Biodatas under your care",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", fontSize = 12.sp)
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val chips = listOf(
                    "all" to "All",
                    "bride" to "Brides",
                    "groom" to "Grooms",
                    "approved" to "Approved",
                    "pending" to "Pending"
                )
                chips.forEach { (key, label) ->
                    val isSelected = filterType == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { filterType = key },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BurgundyPrimary,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Managed Profiles Found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Add New' to register a bride or groom biodata and invite the owner for consent.",
                            fontSize = 12.sp,
                            color = MutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filtered) { profile ->
                ManagedProfileCard(
                    profile = profile,
                    onSuggestClick = { onSuggestClick(profile) },
                    onSendConsentClick = { onSendConsentClick(profile) }
                )
            }
        }
    }
}

@Composable
fun ManagedProfileCard(
    profile: ManagedProfile,
    onSuggestClick: () -> Unit,
    onSendConsentClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (profile.profileType == "bride") BurgundyPrimary.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (profile.profileType == "bride") "👰" else "🤵",
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = profile.fullName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyPrimary
                        )
                        Text(
                            text = "${if (profile.profileType == "bride") "Bride" else "Groom"} • ${profile.age} yrs • ${profile.city}, ${profile.country}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                // Status Badge
                val (statusText, statusBg, statusFg) = when (profile.consentStatus) {
                    "approved" -> Triple("Approved & Live", SuccessGreen.copy(alpha = 0.2f), SuccessGreen)
                    "suspended" -> Triple("Suspended", Color(0xFFFFEBEE), Color(0xFFD32F2F))
                    else -> Triple("Pending Consent", GoldLight.copy(alpha = 0.5f), BurgundyDeep)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, statusFg)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WarmIvory, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "💼 ${profile.profession} | 🎓 ${profile.education}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "🏠 Owner: ${profile.ownerName} (${profile.ownerRelationship}) • 📞 ${profile.ownerPhone}",
                    fontSize = 11.sp,
                    color = MutedText
                )
                if (profile.consentStatus == "pending_consent") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔑 Token: ${profile.inviteToken.take(14)}...",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BurgundyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Permissions: ${profile.permissions.size} granted",
                    fontSize = 11.sp,
                    color = MutedText
                )

                if (profile.consentStatus == "approved") {
                    Button(
                        onClick = onSuggestClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Suggest Match", fontSize = 11.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onSendConsentClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = BurgundyDeep, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Send / Share Consent", fontSize = 11.sp, color = BurgundyDeep)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: ADD MANAGED PROFILE FORM
// ==========================================

@Composable
fun AddManagedProfileForm(
    currentMm: MatchmakerProfile?,
    managedCount: Int,
    maxQuota: Int,
    onSave: (ManagedProfile) -> Unit
) {
    var profileType by remember { mutableStateOf("bride") } // "bride" or "groom"
    var fullName by remember { mutableStateOf("") }
    var dobString by remember { mutableStateOf("1998-06-15") } // YYYY-MM-DD
    var calculatedAge by remember { mutableIntStateOf(26) }
    var isUnderage by remember { mutableStateOf(false) }

    var country by remember { mutableStateOf("United Kingdom") }
    var city by remember { mutableStateOf("London") }
    var nationality by remember { mutableStateOf("British Bangladeshi") }
    var education by remember { mutableStateOf("BSc in Computer Science") }
    var profession by remember { mutableStateOf("Software Engineer") }
    var height by remember { mutableStateOf("5'5\"") }
    var maritalStatus by remember { mutableStateOf("Never Married") }
    var religion by remember { mutableStateOf("Islam (Sunni)") }
    var bio by remember { mutableStateOf("Practicing, educated, family-oriented individual living abroad.") }

    // Partner Preferences
    var prefAgeMin by remember { mutableIntStateOf(24) }
    var prefAgeMax by remember { mutableIntStateOf(32) }
    var prefCountry by remember { mutableStateOf("United Kingdom, Bangladesh, USA") }
    var prefProfession by remember { mutableStateOf("Engineer, Doctor, Professional") }

    // Profile Owner Details
    var ownerName by remember { mutableStateOf("") }
    var ownerRelation by remember { mutableStateOf("Father") }
    var ownerEmail by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("+44 7700 900123") }

    // Error text
    var formError by remember { mutableStateOf<String?>(null) }

    fun recomputeAge(dob: String) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val d = sdf.parse(dob.trim())
            if (d != null) {
                val dobCal = Calendar.getInstance().apply { time = d }
                val today = Calendar.getInstance()
                var age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                calculatedAge = age
                isUnderage = age < 18
            }
        } catch (e: Exception) {
            // Keep current age if parse fails
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quota check banner
        if (managedCount >= maxQuota) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You have reached your Free Launch Plan quota ($managedCount / $maxQuota profiles). Contact Admin to request higher quota.",
                        fontSize = 12.sp,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }

        Text(
            text = "Add New Candidate Biodata",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = BurgundyPrimary
        )

        // Bride vs Groom Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val isBride = profileType == "bride"
            Surface(
                onClick = {
                    profileType = "bride"
                    if (height == "5'10\"") height = "5'4\""
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = if (isBride) BurgundyPrimary else PureWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isBride) BurgundyPrimary else GoldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👰", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bride Profile",
                        fontWeight = FontWeight.Bold,
                        color = if (isBride) PureWhite else Color.DarkGray,
                        fontSize = 13.sp
                    )
                }
            }

            val isGroom = profileType == "groom"
            Surface(
                onClick = {
                    profileType = "groom"
                    if (height == "5'4\"") height = "5'10\""
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = if (isGroom) BurgundyPrimary else PureWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isGroom) BurgundyPrimary else GoldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🤵", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Groom Profile",
                        fontWeight = FontWeight.Bold,
                        color = if (isGroom) PureWhite else Color.DarkGray,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Section 1: Candidate Biodata
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. Candidate Information",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyPrimary
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Date of Birth & Calculated Age
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = dobString,
                        onValueChange = {
                            dobString = it
                            recomputeAge(it)
                        },
                        label = { Text("DOB (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Surface(
                        modifier = Modifier
                            .weight(0.9f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isUnderage) Color(0xFFFFEBEE) else WarmIvory,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnderage) Color(0xFFD32F2F) else GoldBorder)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Age: $calculatedAge yrs",
                                fontWeight = FontWeight.Bold,
                                color = if (isUnderage) Color(0xFFD32F2F) else BurgundyPrimary,
                                fontSize = 13.sp
                            )
                            if (isUnderage) {
                                Text(
                                    text = "Must be 18+",
                                    fontSize = 10.sp,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = education,
                        onValueChange = { education = it },
                        label = { Text("Education") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = profession,
                        onValueChange = { profession = it },
                        label = { Text("Profession") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = maritalStatus,
                        onValueChange = { maritalStatus = it },
                        label = { Text("Marital Status") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("About Candidate (Brief Bio)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )
            }
        }

        // Section 2: Partner Preferences
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "2. Partner Expectations",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = prefAgeMin.toString(),
                        onValueChange = { prefAgeMin = it.toIntOrNull() ?: 20 },
                        label = { Text("Min Age") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = prefAgeMax.toString(),
                        onValueChange = { prefAgeMax = it.toIntOrNull() ?: 35 },
                        label = { Text("Max Age") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                OutlinedTextField(
                    value = prefCountry,
                    onValueChange = { prefCountry = it },
                    label = { Text("Preferred Countries") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = prefProfession,
                    onValueChange = { prefProfession = it },
                    label = { Text("Preferred Profession") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Section 3: Profile Owner & Consent Details (MANDATORY FOR TWO-SIDED CONSENT)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "3. Profile Owner & Consent Invitation",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyPrimary
                    )
                }

                Text(
                    text = "A secure verification invitation link will be issued to this guardian/owner. Matchmakers cannot publish or suggest matches until the owner approves.",
                    fontSize = 11.sp,
                    color = MutedText
                )

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Guardian / Candidate Name") },
                    placeholder = { Text("e.g. " + fullName.ifBlank { "Candidate or Guardian Name" }) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = biyeAbroadTextFieldColors()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ownerRelation,
                        onValueChange = { ownerRelation = it },
                        label = { Text("Relationship") },
                        placeholder = { Text("Father / Self") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = biyeAbroadTextFieldColors()
                    )
                    OutlinedTextField(
                        value = ownerPhone,
                        onValueChange = { ownerPhone = it },
                        label = { Text("Phone with Country Code") },
                        placeholder = { Text("+880 1...") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = biyeAbroadTextFieldColors()
                    )
                }

                OutlinedTextField(
                    value = ownerEmail,
                    onValueChange = { ownerEmail = it },
                    label = { Text("Candidate / Owner Email Address") },
                    placeholder = { Text("candidate@gmail.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = biyeAbroadTextFieldColors()
                )
            }
        }

        formError?.let { err ->
            Text(
                text = "⚠️ $err",
                color = Color(0xFFD32F2F),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Save & Send Consent Request Button
        Button(
            onClick = {
                if (fullName.isBlank()) {
                    formError = "Please enter the candidate's full name."
                    return@Button
                }
                if (isUnderage || calculatedAge < 18) {
                    formError = "Candidate must be at least 18 years of age."
                    return@Button
                }
                val resolvedOwnerName = if (ownerName.isNotBlank()) ownerName.trim() else fullName.trim()
                if (resolvedOwnerName.isBlank()) {
                    formError = "Please enter the candidate or guardian name."
                    return@Button
                }
                if (ownerEmail.isBlank() && ownerPhone.isBlank()) {
                    formError = "Please enter either Email or Phone for consent verification."
                    return@Button
                }

                formError = null
                val newManagedId = UUID.randomUUID().toString()
                val profile = ManagedProfile(
                    managedProfileId = newManagedId,
                    matchmakerId = currentMm?.matchmakerId ?: "matchmaker_demo",
                    matchmakerName = currentMm?.fullName ?: "Verified Ghotok",
                    profileType = profileType,
                    fullName = fullName.trim(),
                    dateOfBirth = dobString,
                    age = calculatedAge,
                    gender = if (profileType == "bride") "female" else "male",
                    country = country.trim(),
                    city = city.trim(),
                    nationality = nationality.trim(),
                    education = education.trim(),
                    profession = profession.trim(),
                    maritalStatus = maritalStatus.trim(),
                    height = height.trim(),
                    religion = religion.trim(),
                    bio = bio.trim(),
                    partnerPreferences = "Age $prefAgeMin-$prefAgeMax, Countries: $prefCountry, Profession: $prefProfession",
                    consentStatus = "pending_consent",
                    ownerName = resolvedOwnerName,
                    ownerRelationship = ownerRelation.trim().ifBlank { "Self / Guardian" },
                    ownerEmail = ownerEmail.trim(),
                    ownerPhone = ownerPhone.trim()
                )

                onSave(profile)
            },
            enabled = managedCount < maxQuota,
            colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Biodata & Send Consent Request", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ==========================================
// TAB 4: MATCH SUGGESTIONS
// ==========================================

@Composable
fun MatchSuggestionsView(
    suggestions: List<MatchSuggestion>,
    approvedProfiles: List<ManagedProfile>,
    candidatePool: List<UserProfile>,
    onSubmitSuggestion: (ManagedProfile, UserProfile, String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Match Proposals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyPrimary
                    )
                    Text(
                        text = "Propose introductions between approved candidates",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    enabled = approvedProfiles.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Match", fontSize = 12.sp)
                }
            }
        }

        if (suggestions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Match Suggestions Yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connect two approved candidates by proposing a match with your matchmaking assessment.",
                            fontSize = 12.sp,
                            color = MutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(suggestions) { sug ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${sug.profileAName} ❤️ ${sug.profileBName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )

                            val (statusText, statusBg, statusFg) = when (sug.status) {
                                "approved" -> Triple("Accepted", SuccessGreen.copy(alpha = 0.2f), SuccessGreen)
                                "declined" -> Triple("Declined", Color(0xFFFFEBEE), Color(0xFFD32F2F))
                                else -> Triple("Pending Review", GoldLight.copy(alpha = 0.5f), BurgundyDeep)
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusBg
                            ) {
                                Text(
                                    text = statusText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusFg,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (sug.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ghotok Note: \"${sug.notes}\"",
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateMatchSuggestionDialog(
            approvedProfiles = approvedProfiles,
            candidatePool = candidatePool,
            onDismiss = { showCreateDialog = false },
            onSubmit = { pA, pB, notes ->
                onSubmitSuggestion(pA, pB, notes)
                showCreateDialog = false
            }
        )
    }
}

// ==========================================
// TAB 5: PLAN & PROFILE
// ==========================================

@Composable
fun MatchmakerPlanAndProfileView(
    matchmaker: MatchmakerProfile?,
    managedCount: Int
) {
    val maxQuota = matchmaker?.maxManagedProfiles ?: 10

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Plan Info
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BiyeAbroad Launch Plan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )
                            Text(
                                text = "Free Community Matchmaker Tier",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = "FREE / $0",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = GoldBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "• Managed Profiles Allowed: Up to $maxQuota profiles (Configurable by Admin)\n• Biodata Creation & Guardian Consent Tokens: Included\n• Direct Match Suggestions to Candidates: Included\n• Verified Ghotok Badge on Approved Profiles: Included\n• No credit card or fee required during early launch phase.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Future Tier Card (Coming Soon)
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmIvory),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Future Premium / Unlimited Tier",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BurgundyDeep
                                ) {
                                    Text(
                                        text = "Coming Soon",
                                        fontSize = 10.sp,
                                        color = GoldLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Higher quota tiers, priority candidate matching, and enterprise agency tools will be available in upcoming platform updates.",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                        }
                    }
                }
            }
        }

        // Matchmaker Profile Details Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Matchmaker Professional Profile",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyPrimary
                    )

                    ProfileInfoRow(label = "Full Name", value = matchmaker?.fullName ?: "N/A")
                    ProfileInfoRow(label = "Email", value = matchmaker?.email ?: "N/A")
                    ProfileInfoRow(label = "Phone", value = matchmaker?.phone ?: "N/A")
                    ProfileInfoRow(label = "Location", value = "${matchmaker?.city}, ${matchmaker?.country}")
                    ProfileInfoRow(label = "Experience", value = "${matchmaker?.experienceYears} Years")
                    ProfileInfoRow(label = "Languages", value = matchmaker?.languages?.joinToString(", ") ?: "Bengali, English")
                    ProfileInfoRow(label = "Bio", value = matchmaker?.bio ?: "N/A")
                    ProfileInfoRow(label = "Reference", value = matchmaker?.referenceInfo ?: "N/A")
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 12.sp, color = BurgundyDeep, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = GoldBorder.copy(alpha = 0.2f))
    }
}

// ==========================================
// DIALOGS: SUGGEST MATCH & NEW SUGGESTION
// ==========================================

@Composable
fun SuggestMatchDialog(
    managedProfile: ManagedProfile,
    candidatePool: List<UserProfile>,
    onDismiss: () -> Unit,
    onSend: (UserProfile, String) -> Unit
) {
    var selectedCandidate by remember { mutableStateOf<UserProfile?>(null) }
    var matchmakerNotes by remember { mutableStateOf("Both families share similar values and educational backgrounds. Highly recommended.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Propose Match for ${managedProfile.fullName}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BurgundyPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select a compatible candidate from BiyeAbroad to send this introduction:",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                candidatePool.take(5).forEach { cand ->
                    val isSel = selectedCandidate?.userId == cand.userId
                    Surface(
                        onClick = { selectedCandidate = cand },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) BurgundyPrimary.copy(alpha = 0.1f) else WarmIvory,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) BurgundyPrimary else GoldBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${cand.displayName} (${cand.age} yrs, ${cand.profession})",
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = BurgundyPrimary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = matchmakerNotes,
                    onValueChange = { matchmakerNotes = it },
                    label = { Text("Matchmaker Recommendation Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedCandidate?.let { onSend(it, matchmakerNotes) }
                },
                enabled = selectedCandidate != null,
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
            ) {
                Text("Submit Suggestion")
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
fun CreateMatchSuggestionDialog(
    approvedProfiles: List<ManagedProfile>,
    candidatePool: List<UserProfile>,
    onDismiss: () -> Unit,
    onSubmit: (ManagedProfile, UserProfile, String) -> Unit
) {
    var selectedProfA by remember { mutableStateOf<ManagedProfile?>(approvedProfiles.firstOrNull()) }
    var selectedProfB by remember { mutableStateOf<UserProfile?>(candidatePool.firstOrNull()) }
    var notes by remember { mutableStateOf("Families have compatible lifestyles and educational aspirations.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Match Suggestion",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BurgundyPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select Candidate 1 (From your managed biodatas):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BurgundyPrimary)
                approvedProfiles.forEach { p ->
                    val isSel = selectedProfA?.managedProfileId == p.managedProfileId
                    Surface(
                        onClick = { selectedProfA = p },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) BurgundyPrimary.copy(alpha = 0.1f) else WarmIvory,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) BurgundyPrimary else GoldBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${p.fullName} (${p.age} yrs • ${p.profession})",
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(8.dp),
                            color = BurgundyDeep
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Select Candidate 2 (From BiyeAbroad members):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BurgundyPrimary)
                candidatePool.take(4).forEach { p ->
                    val isSel = selectedProfB?.userId == p.userId
                    Surface(
                        onClick = { selectedProfB = p },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) BurgundyPrimary.copy(alpha = 0.1f) else WarmIvory,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) BurgundyPrimary else GoldBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${p.displayName} (${p.age} yrs • ${p.profession})",
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(8.dp),
                            color = BurgundyDeep
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Recommendation Note") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pA = selectedProfA
                    val pB = selectedProfB
                    if (pA != null && pB != null) {
                        onSubmit(pA, pB, notes)
                    }
                },
                enabled = selectedProfA != null && selectedProfB != null,
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
