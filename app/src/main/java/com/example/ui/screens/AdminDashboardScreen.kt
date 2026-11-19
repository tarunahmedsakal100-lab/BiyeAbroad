package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoProfiles
import com.example.data.model.MatchmakerAuditLog
import com.example.data.model.MatchmakerProfile
import com.example.data.model.Report
import com.example.data.model.UserProfile
import com.example.data.model.VerificationRequest
import com.example.ui.components.BiyeAbroadLogo
import com.example.ui.components.BiyeAbroadAdminLogo
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

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel? = null
) {
    val context = LocalContext.current
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf("Overview", "Users", "Matchmakers", "Verifications", "Reports", "Monetization")

    val users by viewModel.adminAllUsers.collectAsState()
    val reports by viewModel.adminReports.collectAsState()
    val verifications by viewModel.adminVerifications.collectAsState()

    // Blended users count including demo sample for overview
    val totalUserCount = (users.size + DemoProfiles.sampleProfiles.size)
    val pendingVerifications = verifications.filter { it.status == "pending" }
    val openReports = reports.filter { it.status == "open" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
    ) {
        // Admin Header
        Surface(
            color = BurgundyDeep,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BiyeAbroadAdminLogo(size = 42.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BiyeAbroad Admin",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = GoldAccent,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "OFFICIAL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BurgundyDeep,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Platform Control & Trust Center",
                            fontSize = 11.sp,
                            color = PureWhite.copy(alpha = 0.8f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onExitAdmin,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(13.dp), tint = GoldLight)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview App", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                    }

                    if (authViewModel != null) {
                        Button(
                            onClick = { authViewModel.signOut(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(13.dp), tint = PureWhite)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sign Out", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        }
                    }
                }
            }
        }

        // Section Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = PureWhite,
            contentColor = BurgundyPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = BurgundyPrimary
                )
            }
        ) {
            sections.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedSection == idx,
                    onClick = { selectedSection = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSection == idx) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedSection) {
            0 -> AdminOverviewTab(
                totalUsers = totalUserCount,
                pendingVerifications = pendingVerifications.size,
                openReports = openReports.size,
                onNavigate = { selectedSection = it }
            )
            1 -> AdminUserManagementTab(
                registeredUsers = users,
                onToggleSuspension = { uid, suspended -> viewModel.setSuspension(uid, suspended) },
                onToggleVerification = { uid, status -> viewModel.adminSetUserVerification(uid, status) },
                onDeleteUser = { uid -> viewModel.adminDeleteUser(uid) }
            )
            2 -> AdminMatchmakersTab(viewModel = viewModel)
            3 -> AdminVerificationQueueTab(
                verifications = verifications,
                onReview = { reqId, uid, approved, notes ->
                    viewModel.reviewVerification(reqId, uid, approved, notes)
                }
            )
            4 -> AdminReportsTab(
                reports = reports,
                allUsers = users
            )
            5 -> AdminMonetizationTab()
        }
    }
}

@Composable
fun AdminOverviewTab(
    totalUsers: Int,
    pendingVerifications: Int,
    openReports: Int,
    onNavigate: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Platform Metrics", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminMetricCard(
                title = "Total Users",
                value = "$totalUsers",
                color = BurgundyPrimary,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(1) }
            )
            AdminMetricCard(
                title = "Pending Verify",
                value = "$pendingVerifications",
                color = GoldAccent,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(2) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminMetricCard(
                title = "Open Complaints",
                value = "$openReports",
                color = if (openReports > 0) Color(0xFFBA1A1A) else SuccessGreen,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(3) }
            )
            AdminMetricCard(
                title = "Premium Subs",
                value = "1,840+",
                color = BurgundyDeep,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(4) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Quick Administration Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                AdminActionRow(
                    icon = Icons.Default.VerifiedUser,
                    title = "Review Pending Verifications",
                    desc = "$pendingVerifications identity submissions awaiting manual check",
                    onClick = { onNavigate(2) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = WarmIvory)
                AdminActionRow(
                    icon = Icons.Default.Flag,
                    title = "Moderation & Safety Center",
                    desc = "$openReports member reports flagged by the community",
                    onClick = { onNavigate(3) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = WarmIvory)
                AdminActionRow(
                    icon = Icons.Default.Diamond,
                    title = "Subscription & Monetization Rules",
                    desc = "Manage membership tiers and sponsorship placements",
                    onClick = { onNavigate(4) }
                )
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BurgundyPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
            Text(text = desc, fontSize = 11.sp, color = MutedText)
        }
    }
}

@Composable
fun AdminUserManagementTab(
    registeredUsers: List<UserProfile>,
    onToggleSuspension: (userId: String, isSuspended: Boolean) -> Unit,
    onToggleVerification: (userId: String, newStatus: String) -> Unit,
    onDeleteUser: (userId: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") } // "all", "active", "suspended", "verified", "pending"
    var inspectUser by remember { mutableStateOf<UserProfile?>(null) }
    var userToDelete by remember { mutableStateOf<UserProfile?>(null) }

    val combinedUsers = (registeredUsers + DemoProfiles.sampleProfiles)
    val filtered = combinedUsers.filter { user ->
        val matchesQuery = user.displayName.contains(searchQuery, ignoreCase = true) ||
            user.email.contains(searchQuery, ignoreCase = true) ||
            user.country.contains(searchQuery, ignoreCase = true) ||
            user.city.contains(searchQuery, ignoreCase = true) ||
            user.profession.contains(searchQuery, ignoreCase = true) ||
            user.userId.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "active" -> !user.isSuspended
            "suspended" -> user.isSuspended
            "verified" -> user.verificationStatus == "verified"
            "pending" -> user.verificationStatus == "pending"
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, email, city, country, profession...") },
            colors = biyeAbroadTextFieldColors(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BurgundyPrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = MutedText)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_search_input"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        val chipScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(chipScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf(
                "all" to "All Users (${combinedUsers.size})",
                "active" to "Active (${combinedUsers.count { !it.isSuspended }})",
                "suspended" to "Suspended (${combinedUsers.count { it.isSuspended }})",
                "verified" to "Verified (${combinedUsers.count { it.verificationStatus == "verified" }})",
                "pending" to "Pending ID (${combinedUsers.count { it.verificationStatus == "pending" }})"
            )

            filterOptions.forEach { (key, label) ->
                val selected = selectedFilter == key
                Surface(
                    onClick = { selectedFilter = key },
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) BurgundyPrimary else PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) BurgundyPrimary else GoldBorder),
                    modifier = Modifier.clickable { selectedFilter = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) PureWhite else DarkText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Showing ${filtered.size} managed user profiles",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MutedText
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No user accounts found matching criteria.", color = MutedText, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_user_card_${user.userId}"),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (user.isSuspended) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else GoldBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // User Avatar Circle
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (user.gender == "female") BurgundyDeep else Color(0xFF0F766E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.displayName.take(2).uppercase().ifBlank { "MB" },
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = DarkText
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        VerificationBadge(status = user.verificationStatus)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        TierBadge(tier = user.membershipTier)
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "${user.email} • ${user.city}, ${user.country}",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )

                                    Text(
                                        text = "${if (user.gender == "female") "Bride" else "Groom"} • ${user.age} yrs • ${user.profession.ifBlank { "Professional" }}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BurgundyPrimary
                                    )
                                }

                                // Status Pill
                                Surface(
                                    color = if (user.isSuspended) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (user.isSuspended) "SUSPENDED" else "ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (user.isSuspended) Color(0xFF991B1B) else Color(0xFF166534),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = WarmIvory)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Control Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Inspect Biodata
                                OutlinedButton(
                                    onClick = { inspectUser = user },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyPrimary),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(13.dp), tint = BurgundyPrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Biodata", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BurgundyPrimary)
                                }

                                // Toggle Verification
                                OutlinedButton(
                                    onClick = {
                                        val newStatus = if (user.verificationStatus == "verified") "standard" else "verified"
                                        onToggleVerification(user.userId, newStatus)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(13.dp), tint = BurgundyDeep)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (user.verificationStatus == "verified") "Unverify" else "Verify",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BurgundyDeep
                                    )
                                }

                                // Toggle Suspension
                                Button(
                                    onClick = { onToggleSuspension(user.userId, !user.isSuspended) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (user.isSuspended) SuccessGreen else MaterialTheme.colorScheme.error
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (user.isSuspended) "Restore" else "Suspend",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                }

                                // Delete Account
                                IconButton(
                                    onClick = { userToDelete = user },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete user",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Full Biodata Inspection
    inspectUser?.let { user ->
        AlertDialog(
            onDismissRequest = { inspectUser = null },
            containerColor = PureWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BurgundyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "User Control: ${user.displayName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = WarmIvory,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("UID: ${user.userId}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BurgundyPrimary)
                            Text("Email: ${user.email}", fontSize = 11.sp, color = DarkText)
                            Text("Account Role: ${user.role} | Status: ${if (user.isSuspended) "SUSPENDED" else "ACTIVE"}", fontSize = 11.sp, color = DarkText)
                            Text("ID Verification: ${user.verificationStatus.uppercase()} | Tier: ${user.membershipTier.uppercase()}", fontSize = 11.sp, color = DarkText)
                        }
                    }

                    Text("Matrimonial Biodata Details", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BurgundyPrimary)
                    Text("• Gender: ${if (user.gender == "female") "Bride (Female)" else "Groom (Male)"}", fontSize = 12.sp, color = DarkText)
                    Text("• Date of Birth: ${user.dateOfBirth.ifBlank { "Not specified" }} (${user.resolvedAge()} years old)", fontSize = 12.sp, color = DarkText)
                    Text("• Marital Status: ${user.maritalStatus} • Height: ${user.height}", fontSize = 12.sp, color = DarkText)
                    Text("• Location: ${user.city}, ${user.country}", fontSize = 12.sp, color = DarkText)
                    if (user.address.isNotBlank()) {
                        Text("• Address: ${user.address}", fontSize = 12.sp, color = DarkText)
                    }
                    Text("• Origin in Bangladesh: ${user.countryOfOrigin}", fontSize = 12.sp, color = DarkText)
                    Text("• Education: ${user.education}", fontSize = 12.sp, color = DarkText)
                    Text("• Profession: ${user.profession}", fontSize = 12.sp, color = DarkText)
                    Text("• Family Involvement: ${user.familyInvolvementPreference}", fontSize = 12.sp, color = DarkText)
                    Text("• Languages: ${user.languages.joinToString(", ")}", fontSize = 12.sp, color = DarkText)

                    if (user.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("About / Biography:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BurgundyDeep)
                        Text(user.bio, fontSize = 11.sp, color = DarkText)
                    }

                    if (user.partnerPreferences.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Partner Preferences:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BurgundyDeep)
                        Text(user.partnerPreferences, fontSize = 11.sp, color = DarkText)
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onToggleSuspension(user.userId, !user.isSuspended)
                            inspectUser = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isSuspended) SuccessGreen else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(if (user.isSuspended) "Restore Account" else "Suspend Account", fontSize = 11.sp, color = PureWhite)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectUser = null }) {
                    Text("Close", color = BurgundyPrimary)
                }
            }
        )
    }

    // Modal: Confirm Delete User
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = PureWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Delete User Account?", color = BurgundyDeep, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to permanently delete '${user.displayName}' (${user.email}) from the BiyeAbroad database? All their profile data, photos, and records will be deleted immediately.",
                    fontSize = 13.sp,
                    color = DarkText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(user.userId)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Permanently Delete", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel", color = BurgundyPrimary)
                }
            }
        )
    }
}

@Composable
fun AdminVerificationQueueTab(
    verifications: List<VerificationRequest>,
    onReview: (requestId: String, userId: String, approved: Boolean, notes: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Pending Verification Requests (${verifications.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
        Spacer(modifier = Modifier.height(10.dp))

        if (verifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No pending verification requests at this time.", color = MutedText, fontSize = 13.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(verifications) { req ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Doc: ${req.documentType.uppercase()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BurgundyPrimary)
                                Surface(
                                    color = if (req.status == "approved") SuccessGreen.copy(alpha = 0.2f) else GoldLight.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = req.status.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BurgundyDeep,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Number: ${req.documentNumber}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                            Text(text = "User ID: ${req.userId}", fontSize = 11.sp, color = MutedText)

                            if (req.status == "pending") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onReview(req.requestId, req.userId, false, "Document rejected by admin") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Reject", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { onReview(req.requestId, req.userId, true, "Verified by Admin") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                    ) {
                                        Text("Approve Badge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportsTab(
    reports: List<Report>,
    allUsers: List<UserProfile>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Moderation & Incident Reports (${reports.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
        Spacer(modifier = Modifier.height(10.dp))

        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No user reports reported. Community is safe and peaceful.", color = MutedText, fontSize = 13.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(reports) { rep ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = rep.reason, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                                Text(text = rep.status.uppercase(), fontSize = 10.sp, color = MutedText)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Reported User: ${rep.reportedUserId}", fontSize = 12.sp, color = DarkText)
                            Text(text = "Reporter: ${rep.reporterId}", fontSize = 11.sp, color = MutedText)
                            if (rep.details.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Details: ${rep.details}", fontSize = 12.sp, color = MutedText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMonetizationTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Subscription & Monetization Tiers", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TierSummaryRow(tier = "Free Members", count = "10,500+", rev = "$0", badgeColor = Color.LightGray)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WarmIvory)
                TierSummaryRow(tier = "Silver Tier ($19/mo)", count = "1,120", rev = "$21,280", badgeColor = Color(0xFFC0C0C0))
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WarmIvory)
                TierSummaryRow(tier = "Gold Tier ($39/3mo)", count = "560", rev = "$21,840", badgeColor = GoldAccent)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WarmIvory)
                TierSummaryRow(tier = "Platinum VIP ($79/6mo)", count = "160", rev = "$12,640", badgeColor = Color(0xFF2E3138))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Advertisement & Sponsorship Rules", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Only certified Bangladeshi cultural events, wedding photography, and halal catering sponsors are eligible for spotlight sponsorship banners on BiyeAbroad.",
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun TierSummaryRow(tier: String, count: String, rev: String, badgeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = badgeColor) {}
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = tier, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DarkText)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = count, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BurgundyDeep)
            Text(text = "Rev: $rev", fontSize = 10.sp, color = MutedText)
        }
    }
}

@Composable
fun AdminMatchmakersTab(
    viewModel: MainViewModel
) {
    val liveMatchmakers by viewModel.adminAllMatchmakers.collectAsState()
    val auditLogs by viewModel.adminMatchmakerAuditLogs.collectAsState()

    // Blended list: if Firestore has none yet, show the demo official matchmaker
    val demoMatchmaker = MatchmakerProfile(
        matchmakerId = "ghotok_tarun_pro_101",
        fullName = "Ghotok Tarun Ahmed (Matchmaker)",
        email = "ghotok.tarun@biyeabroad.com",
        phone = "+880 1711-002233",
        country = "Bangladesh",
        city = "Sylhet & London",
        languages = listOf("Bengali", "English", "Sylheti"),
        experienceYears = 8,
        bio = "Trusted senior matchmaker connecting noble Bangladeshi families across the UK, USA, Canada, and Bangladesh.",
        referenceInfo = "Community Leader & Senior Matchmaker, Sylhet Welfare Association",
        verificationStatus = "verified",
        currentPlanId = "plan_free_launch",
        maxManagedProfiles = 10,
        managedProfileCount = 4,
        termsAccepted = true
    )

    val allMatchmakers = if (liveMatchmakers.isEmpty()) {
        listOf(demoMatchmaker)
    } else {
        liveMatchmakers
    }

    var editingQuotaMm by remember { mutableStateOf<MatchmakerProfile?>(null) }
    var quotaInput by remember { mutableStateOf("10") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Free Launch Policy Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = BurgundyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Free Launch Policy & Plan Control",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SuccessGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Active: $0 / Free",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All approved matchmakers are launched on the Free Plan with up to 10 managed biodatas by default. Admins can grant higher quotas per matchmaker without requiring payments.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Registered Matchmakers (${allMatchmakers.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BurgundyDeep
            )
        }

        items(allMatchmakers) { mm ->
            Card(
                shape = RoundedCornerShape(12.dp),
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
                        Column {
                            Text(
                                text = mm.fullName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )
                            Text(
                                text = "${mm.city}, ${mm.country} • ${mm.experienceYears} Yrs Exp",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                            Text(
                                text = "📞 ${mm.phone} • ✉️ ${mm.email}",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }

                        val (statusText, statusBg, statusFg) = when (mm.verificationStatus.lowercase()) {
                            "verified" -> Triple("Verified Ghotok", SuccessGreen.copy(alpha = 0.2f), SuccessGreen)
                            "suspended" -> Triple("Suspended", Color(0xFFFFEBEE), Color(0xFFD32F2F))
                            "rejected" -> Triple("Rejected", Color(0xFFFFEBEE), Color(0xFFD32F2F))
                            else -> Triple("Pending Review", GoldLight.copy(alpha = 0.5f), BurgundyDeep)
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

                    Surface(
                        color = WarmIvory,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Profile Quota: ${mm.maxManagedProfiles} Allowed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BurgundyDeep
                            )

                            TextButton(
                                onClick = {
                                    editingQuotaMm = mm
                                    quotaInput = mm.maxManagedProfiles.toString()
                                },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Edit Quota", fontSize = 11.sp, color = BurgundyPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (mm.verificationStatus != "verified") {
                            Button(
                                onClick = { viewModel.adminVerifyMatchmaker(mm.matchmakerId, "verified") },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verify & Approve", fontSize = 11.sp)
                            }
                        }

                        if (mm.verificationStatus != "suspended") {
                            OutlinedButton(
                                onClick = { viewModel.adminSetMatchmakerSuspension(mm.matchmakerId, true) },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Suspend", fontSize = 11.sp, color = Color(0xFFD32F2F))
                            }
                        } else {
                            Button(
                                onClick = { viewModel.adminSetMatchmakerSuspension(mm.matchmakerId, false) },
                                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Restore Access", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Audit Logs Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Matchmaker Audit Trail & Security Logs",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BurgundyDeep
            )
        }

        if (auditLogs.isEmpty()) {
            item {
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Audit log is monitoring all matchmaker additions, consent responses, and revocations.",
                        fontSize = 11.sp,
                        color = MutedText,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(auditLogs.take(10)) { log ->
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.action,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyPrimary
                            )
                            Text(
                                text = log.matchmakerId.take(12),
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                        Text(
                            text = log.details,
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }

    // Modal to edit quota
    editingQuotaMm?.let { mm ->
        AlertDialog(
            onDismissRequest = { editingQuotaMm = null },
            title = {
                Text(
                    text = "Update Quota for ${mm.fullName}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter the maximum number of managed profiles this matchmaker can maintain under the Free Launch tier:",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = quotaInput,
                        onValueChange = { quotaInput = it },
                        label = { Text("Profile Quota Limit") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLim = quotaInput.toIntOrNull() ?: 10
                        viewModel.adminUpdatePlanLimit(mm.matchmakerId, newLim)
                        editingQuotaMm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                ) {
                    Text("Save Quota")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingQuotaMm = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
