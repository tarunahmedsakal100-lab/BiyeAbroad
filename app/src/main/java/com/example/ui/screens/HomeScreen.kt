package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.UserProfile
import com.example.ui.components.BiyeAbroadLogo
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
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val myProfile by viewModel.myProfile.collectAsState()
    val profiles by viewModel.discoveryProfiles.collectAsState()
    val scrollState = rememberScrollState()

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
        // Hero Greeting Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(BurgundyDeep, BurgundyPrimary)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome,",
                            fontSize = 13.sp,
                            color = GoldLight,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = myProfile?.displayName ?: "Dear Member",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Welcome to your matrimonial journey",
                            fontSize = 12.sp,
                            color = PureWhite.copy(alpha = 0.8f)
                        )
                    }
                    BiyeAbroadLogo(size = 54.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Global Stats Pill Row
                Surface(
                    color = BurgundyDeep.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(count = "12,400+", label = "Verified Members")
                        StatDivider()
                        StatItem(count = "28+", label = "Countries")
                        StatDivider()
                        StatItem(count = "4,200+", label = "Marriages")
                    }
                }
            }
        }

        // Verification & Security CTA Card if unverified
        if (myProfile?.verificationStatus != "verified") {
            Box(modifier = Modifier.padding(16.dp)) {
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldLight.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = BurgundyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Get Verified Badge",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep
                            )
                            Text(
                                text = "Verified profiles receive 3x more interest responses.",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                        }
                        Button(
                            onClick = { viewModel.showVerificationDialog(true) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                        ) {
                            Text("Verify", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Daily Recommended Matches
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommended for You",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyDeep
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyPrimary,
                    modifier = Modifier.clickable { viewModel.selectTab(MainTab.DISCOVER) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(profiles.take(6)) { profile ->
                    HomeProfileThumbnail(
                        profile = profile,
                        onClick = { viewModel.openProfile(profile) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Family Collaboration Banner
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Surface(
                color = BurgundyPrimary,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = GoldLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Family Collaboration",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Invite your parents or guardian to review profiles and recommend matches with your full consent.",
                            fontSize = 12.sp,
                            color = PureWhite.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { viewModel.showFamilyInviteDialog(true) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldLight)
                        ) {
                            Text("Invite Family Member", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Premium Membership Banner
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Surface(
                color = PureWhite,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BurgundyDeep),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Upgrade to Premium",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDeep
                        )
                        Text(
                            text = "Unlimited interests, direct messaging & priority matchmaking spotlight.",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                    Button(
                        onClick = { viewModel.showPremiumDialog(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Plans", color = BurgundyDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(140.dp))
    }
}
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldLight)
        Text(text = label, fontSize = 10.sp, color = PureWhite.copy(alpha = 0.8f))
    }
}

@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .height(24.dp)
            .width(1.dp)
            .background(GoldBorder.copy(alpha = 0.3f))
    )
}

@Composable
fun HomeProfileThumbnail(
    profile: UserProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
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
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight, modifier = Modifier.size(40.dp))
                    }
                }
                VerificationBadge(
                    status = profile.verificationStatus,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "${profile.displayName}, ${profile.age}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = profile.profession,
                    fontSize = 11.sp,
                    color = BurgundyPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${profile.city}, ${profile.country}",
                    fontSize = 10.sp,
                    color = MutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
