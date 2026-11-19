package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.UserProfile
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
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.biyeAbroadTextFieldColors
import com.example.ui.viewmodel.FilterCriteria
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DiscoverScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.discoveryProfiles.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showFilterDialog by viewModel.showFilterDialog.collectAsState()
    var showQuickFilters by remember { mutableStateOf(true) }

    val quickCountries = listOf("all", "United Kingdom", "United States", "Canada", "Australia", "Bangladesh", "Germany")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
    ) {
        // Direct Search & Quick Easy Filters Bar
        Surface(
            color = PureWhite,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                // 1. Direct Search Bar (Search without needing to use filters)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by name, profession, city, origin...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = BurgundyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = biyeAbroadTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("discover_direct_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Header & Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Matches (${profiles.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDeep
                        )
                        val isFiltered = searchQuery.isNotBlank() || filters.gender != "all" || filters.country != "all" || filters.minAge > 18 || filters.maxAge < 60 || filters.profession.isNotBlank()
                        if (isFiltered) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                onClick = {
                                    viewModel.setSearchQuery("")
                                    viewModel.resetFilters()
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = WarmIvory,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyPrimary)
                            ) {
                                Text(
                                    text = "Reset All ✕",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = { showQuickFilters = !showQuickFilters },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (showQuickFilters) "Hide Filters" else "Show Filters",
                                fontSize = 11.sp,
                                color = BurgundyPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.showFilterDialog(true) },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BurgundyPrimary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filters",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Filters", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (showQuickFilters) {
                    Spacer(modifier = Modifier.height(8.dp))

                // 1. Gender Quick Filter
                val genderOptions = listOf(
                    "all" to "All",
                    "male" to "Male (Groom)",
                    "female" to "Female (Bride)"
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(genderOptions) { (key, label) ->
                        val selected = filters.gender == key
                        Surface(
                            onClick = {
                                viewModel.setFilter(filters.copy(gender = key))
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) BurgundyPrimary else WarmIvory,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) BurgundyPrimary else GoldBorder)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) PureWhite else DarkText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2. Country Quick Filter Chips
                val quickCountryOptions = listOf(
                    "all" to "All Countries",
                    "United Kingdom" to "UK 🇬🇧",
                    "United States" to "USA 🇺🇸",
                    "Canada" to "Canada 🇨🇦",
                    "Bangladesh" to "Bangladesh 🇧🇩",
                    "Australia" to "Australia 🇦🇺",
                    "Germany" to "Germany 🇩🇪"
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickCountryOptions) { (key, label) ->
                        val selected = (key == "all" && filters.country == "all") || (filters.country.equals(key, ignoreCase = true))
                        Surface(
                            onClick = {
                                viewModel.setFilter(filters.copy(country = key))
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) BurgundyPrimary else WarmIvory,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) BurgundyPrimary else GoldBorder)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) PureWhite else DarkText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 3. Age Range Quick Filter Chips
                val ageBrackets = listOf(
                    Triple("All Ages", 18, 60),
                    Triple("20–25 yrs", 20, 25),
                    Triple("26–30 yrs", 26, 30),
                    Triple("31–35 yrs", 31, 35),
                    Triple("36–45 yrs", 36, 45)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ageBrackets) { (label, minA, maxA) ->
                        val selected = filters.minAge == minA && filters.maxAge == maxA
                        Surface(
                            onClick = {
                                viewModel.setFilter(filters.copy(minAge = minA, maxAge = maxA))
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) BurgundyPrimary else WarmIvory,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) BurgundyPrimary else GoldBorder)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) PureWhite else DarkText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }

        if (profiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No profiles found matching your search or filters",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BurgundyDeep,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.resetFilters()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                    ) {
                        Text("Reset Search & Filters")
                    }
                }
            }
        } else {
            val listState = rememberLazyListState()
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(profiles) { profile ->
                        ProfileCard(
                            profile = profile,
                            onViewDetail = { viewModel.openProfile(profile) },
                            onSendInterest = { viewModel.sendInterest(profile.userId) },
                            onReport = { viewModel.showReportDialog(profile) }
                        )
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterBottomSheet(
            currentFilters = filters,
            onDismiss = { viewModel.showFilterDialog(false) },
            onApply = { newFilters ->
                viewModel.setFilter(newFilters)
                viewModel.showFilterDialog(false)
            }
        )
    }
}

@Composable
fun ProfileCard(
    profile: UserProfile,
    onViewDetail: () -> Unit,
    onSendInterest: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onViewDetail)
            .testTag("profile_card_${profile.userId}"),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Image or Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(BurgundyPrimary, BurgundyDeep)
                        )
                    )
            ) {
                if (profile.photoUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(profile.photoUrl),
                        contentDescription = profile.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GoldLight.copy(alpha = 0.8f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "BiyeAbroad Confidential",
                                color = PureWhite.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Overlay Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerificationBadge(status = profile.verificationStatus)
                    TierBadge(tier = profile.membershipTier)
                }

                // Gradient scrim at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Name and Age on bottom of photo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${profile.displayName}, ${profile.age}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }

            // Info Details
            Column(modifier = Modifier.padding(14.dp)) {
                // Profession & Education
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = BurgundyPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = profile.profession,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${profile.city}, ${profile.country} • Origin: ${profile.countryOfOrigin}",
                        fontSize = 12.sp,
                        color = MutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${profile.education} • ${profile.profession}",
                        fontSize = 12.sp,
                        color = MutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = profile.bio,
                    fontSize = 12.sp,
                    color = DarkText.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onViewDetail,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BurgundyPrimary)
                    ) {
                        Text("View Biodata", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onSendInterest,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("send_interest_btn_${profile.userId}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BurgundyPrimary,
                            contentColor = PureWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Send Interest",
                            tint = GoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Interest", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSendInterest: () -> Unit,
    onRequestFamilyChat: (() -> Unit)? = null,
    onReport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onRequestFamilyChat != null) {
                    OutlinedButton(
                        onClick = {
                            onRequestFamilyChat()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BurgundyPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Family Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = {
                        onSendInterest()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Express Interest", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = BurgundyPrimary)
            }
        },
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BurgundyDeep)
                ) {
                    if (profile.photoUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(profile.photoUrl),
                            contentDescription = profile.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight, modifier = Modifier.size(60.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${profile.displayName}, ${profile.resolvedAge()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BurgundyDeep
                        )
                        val genderLabel = if (profile.gender == "female") "Female (Bride)" else "Male (Groom)"
                        Text(
                            text = "$genderLabel • ${profile.maritalStatus} • ${profile.height}",
                            fontSize = 12.sp,
                            color = DarkText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    VerificationBadge(status = profile.verificationStatus)
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = GoldBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                if (profile.dateOfBirth.isNotBlank()) {
                    DetailItem(label = "Date of Birth", value = profile.dateOfBirth)
                }
                DetailItem(label = "Country of Residence", value = "${profile.city}, ${profile.country}")
                if (profile.address.isNotBlank()) {
                    DetailItem(label = "Address", value = profile.address)
                }
                DetailItem(label = "Origin in Bangladesh", value = profile.countryOfOrigin)
                DetailItem(label = "Nationality", value = profile.nationality)
                DetailItem(label = "Education", value = profile.education)
                DetailItem(label = "Profession", value = profile.profession)
                DetailItem(label = "Family Involvement", value = profile.familyInvolvementPreference)

                // Languages
                if (profile.languages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Languages Spoken:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        profile.languages.forEach { lang ->
                            Surface(
                                color = WarmIvory,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                            ) {
                                Text(lang, fontSize = 11.sp, color = BurgundyDeep, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                    }
                }



                Spacer(modifier = Modifier.height(12.dp))
                Text("About & Biodata Biography", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BurgundyPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile.bio,
                    fontSize = 12.sp,
                    color = DarkText,
                    lineHeight = 17.sp
                )

                // Partner Preferences
                if (profile.partnerPreferences.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Partner Preferences & Expectations", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BurgundyPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = WarmIvory,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = profile.partnerPreferences,
                            fontSize = 12.sp,
                            color = DarkText,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }
                }

                // Additional Photo Gallery if available
                if (profile.photoUrls.size > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Profile Photos Gallery (${profile.photoUrls.size} photos)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        profile.photoUrls.forEach { pUrl ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, GoldBorder, RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(pUrl),
                                    contentDescription = "Gallery photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        onDismiss()
                        onReport()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Report", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report Profile", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                }
            }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = MutedText, modifier = Modifier.weight(1f))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilters: FilterCriteria,
    onDismiss: () -> Unit,
    onApply: (FilterCriteria) -> Unit
) {
    var gender by remember { mutableStateOf(currentFilters.gender) }
    var country by remember { mutableStateOf(currentFilters.country) }
    var minAge by remember { mutableStateOf(currentFilters.minAge.toFloat()) }
    var maxAge by remember { mutableStateOf(currentFilters.maxAge.toFloat()) }
    var profession by remember { mutableStateOf(currentFilters.profession) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = WarmIvory
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Search & Matrimony Filters", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gender - Groom / Male first, Bride / Female second
            Text("Seeking Gender", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BurgundyPrimary)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                listOf("all" to "Any", "male" to "Groom / Male", "female" to "Bride / Female").forEach { (key, label) ->
                    val selected = gender == key
                    Surface(
                        onClick = { gender = key },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) BurgundyPrimary else PureWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) BurgundyPrimary else GoldBorder),
                        modifier = Modifier.weight(1f).padding(3.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) PureWhite else DarkText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Age Range
            Text("Age Range: ${minAge.toInt()} - ${maxAge.toInt()} years", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BurgundyPrimary)
            RangeSlider(
                value = minAge..maxAge,
                onValueChange = { range ->
                    minAge = range.start
                    maxAge = range.endInclusive
                },
                valueRange = 18f..65f,
                steps = 46,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Country
            Text("Country of Residence", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BurgundyPrimary)
            OutlinedTextField(
                value = if (country == "all") "" else country,
                onValueChange = { country = if (it.isBlank()) "all" else it },
                placeholder = { Text("e.g. United Kingdom, USA, Australia") },
                colors = biyeAbroadTextFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Profession
            Text("Profession Keyword", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BurgundyPrimary)
            OutlinedTextField(
                value = profession,
                onValueChange = { profession = it },
                placeholder = { Text("e.g. Doctor, Engineer, Accountant") },
                colors = biyeAbroadTextFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onApply(FilterCriteria())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }
                Button(
                    onClick = {
                        onApply(
                            FilterCriteria(
                                gender = gender,
                                country = country,
                                minAge = minAge.toInt(),
                                maxAge = maxAge.toInt(),
                                profession = profession.trim()
                            )
                        )
                    },
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
