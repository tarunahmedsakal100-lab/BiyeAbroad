package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarmIvory

@Composable
fun PrivacySafetyDialog(
    currentProfile: UserProfile? = null,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit = {}
) {
    var photoVisibility by remember { mutableStateOf("all") } // "all", "interest_only", "blurred"
    var searchListed by remember { mutableStateOf(true) }
    var familyChatApproval by remember { mutableStateOf(true) }
    var contactDetailsProtected by remember { mutableStateOf(true) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(WarmIvory, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Privacy & Safety",
                        tint = BurgundyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Privacy & Safety Settings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep
                    )
                    Text(
                        text = "Confidentiality & Family Safeguards",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Notice banner
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BurgundyPrimary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your phone number and private residential address are never shown publicly on your biodata. They are only shared upon mutual, consensual acceptance.",
                            fontSize = 11.sp,
                            color = DarkText,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Photo Privacy
                Text(
                    text = "1. Biodata Photo Visibility",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BurgundyPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                val photoOptions = listOf(
                    Triple("all", "Visible to All Verified Members", "Active verified seekers can view your photo"),
                    Triple("interest_only", "Visible Only upon Accepted Interest", "Hidden until you accept a connection request"),
                    Triple("blurred", "Blurred Photo Preview", "Watermarked and blurred until mutual consent")
                )

                photoOptions.forEach { (key, title, subtitle) ->
                    val isSelected = photoVisibility == key
                    Surface(
                        onClick = { photoVisibility = key },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFFFF1F2) else PureWhite,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) BurgundyPrimary else GoldBorder.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { photoVisibility = key },
                                colors = RadioButtonDefaults.colors(selectedColor = BurgundyPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BurgundyDeep
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 10.sp,
                                    color = MutedText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Search & Discovery Visibility
                Text(
                    text = "2. Search & Discovery Listing",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BurgundyPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = WarmIvory,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "List my Biodata in Search",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "Toggle off to temporarily hide your biodata while reviewing proposals.",
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                        Switch(
                            checked = searchListed,
                            onCheckedChange = { searchListed = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = BurgundyPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Family Chat & Protection
                Text(
                    text = "3. Family Oversight & Protection",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BurgundyPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = WarmIvory,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Family Chat Two-Sided Consent",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = "Requires both families to consent before family chat rooms open.",
                                    fontSize = 10.sp,
                                    color = MutedText
                                )
                            }
                            Switch(
                                checked = familyChatApproval,
                                onCheckedChange = { familyChatApproval = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PureWhite,
                                    checkedTrackColor = BurgundyPrimary
                                )
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = GoldBorder.copy(alpha = 0.4f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mask Direct Phone Numbers",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = "Phone calls only permitted after mutual matrimonial acceptance.",
                                    fontSize = 10.sp,
                                    color = MutedText
                                )
                            }
                            Switch(
                                checked = contactDetailsProtected,
                                onCheckedChange = { contactDetailsProtected = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PureWhite,
                                    checkedTrackColor = BurgundyPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Anti-Harassment Guarantee
                Surface(
                    color = BurgundyDeep.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BurgundyDeep,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zero-Tolerance Safety Policy: Harassment, deceptive information, or commercial solicitation results in immediate permanent ban and document invalidation.",
                            fontSize = 10.sp,
                            color = DarkText,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentProfile != null) {
                        onSave(currentProfile)
                    }
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgundyPrimary,
                    contentColor = PureWhite
                ),
                modifier = Modifier.testTag("save_privacy_settings_button")
            ) {
                Text(
                    text = "Save Privacy Settings",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp, color = MutedText)
            }
        }
    )
}
