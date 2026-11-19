package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SuccessGreen

@Composable
fun BiyeAbroadLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_biyeabroad),
            contentDescription = "Official BiyeAbroad Logo",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun BiyeAbroadAdminLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_biyeabroad_admin),
            contentDescription = "Official BiyeAbroad Admin Logo",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onAdminClick: (() -> Unit)? = null,
    isAdmin: Boolean = false,
    isAdminMode: Boolean = false
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BurgundyPrimary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BiyeAbroadLogo(size = if (compact) 40.dp else 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "BiyeAbroad",
                        fontSize = if (compact) 18.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Together Forever",
                        fontSize = 11.sp,
                        color = PureWhite.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (isAdmin && onAdminClick != null) {
                Surface(
                    onClick = onAdminClick,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAdminMode) GoldAccent else BurgundyDeep,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Admin Portal",
                            tint = if (isAdminMode) BurgundyDeep else GoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAdminMode) "Member View" else "Admin Portal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAdminMode) BurgundyDeep else GoldLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, bg, fg) = when (status.lowercase()) {
        "verified" -> Triple("Verified", SuccessGreen, PureWhite)
        "pending" -> Triple("Under Review", GoldAccent, BurgundyDeep)
        else -> Triple("Unverified", Color(0xFF888888), PureWhite)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (status.equals("verified", ignoreCase = true)) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified Badge",
                    tint = fg,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = fg
            )
        }
    }
}

@Composable
fun TierBadge(
    tier: String,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (tier.lowercase()) {
        "platinum" -> Pair(Color(0xFF2E3138), GoldLight)
        "gold" -> Pair(GoldAccent, BurgundyDeep)
        "silver" -> Pair(Color(0xFFC0C0C0), BurgundyDeep)
        else -> Pair(Color(0xFFE8DFC8), BurgundyPrimary)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
    ) {
        Text(
            text = tier.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = fg,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
