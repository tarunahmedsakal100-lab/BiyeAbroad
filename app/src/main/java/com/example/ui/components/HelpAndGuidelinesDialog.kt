package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmIvory

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun HelpAndGuidelinesDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var expandedFaqIndex by remember { mutableIntStateOf(0) }

    val supportEmail = "tarunahmedsakal100@gmail.com"

    val faqs = listOf(
        FaqItem(
            question = "How do I manually type my birth year and edit biodata?",
            answer = "Open Profile > Edit Biodata. You can type your Birth Year directly (e.g. 1996), Birth Month, and Birth Day in the numeric fields without relying on date pickers. You can also edit your age manually and scroll up and down freely to update your address, education, profession, and partner preferences."
        ),
        FaqItem(
            question = "How do I invite family members and send them email?",
            answer = "Go to Profile > Family & Guardian Participation, type their email address and select relationship (Father, Mother, Brother, etc.). Tap 'Send Family Invitation'. You can then tap 'Open Email App' to send a formal email directly via Gmail/Mail, or 'Copy Invitation' to send via WhatsApp. When they sign in with that email, they will automatically join your family circle."
        ),
        FaqItem(
            question = "How does ID Verification work?",
            answer = "Tap 'Get Verified' in your profile to upload a government-issued National ID (NID), Passport, or Driving License. Authorized BiyeAbroad directors review the credential within 24-48 hours. Once approved, your profile receives the official verified Green Badge."
        ),
        FaqItem(
            question = "How do Matchmakers (Ghotoks) work with seekers?",
            answer = "A professional matchmaker can represent candidate biodatas only with explicit consent. If a matchmaker requests to represent you, an approval request appears in Profile > Matchmaker Management. You can review, approve, or revoke their access at any time."
        ),
        FaqItem(
            question = "Is my contact information and phone number safe?",
            answer = "Yes, 100%. Your phone number, private house address, and identity documents are strictly confidential and protected by zero-trust security rules. They are never shown on your public biodata and can only be exchanged after mutual, consensual agreement."
        )
    )

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
                        imageVector = Icons.Default.Help,
                        contentDescription = "Help",
                        tint = BurgundyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Help & Community Guidelines",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep
                    )
                    Text(
                        text = "Support, FAQs & Platform Etiquette",
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
                // Dignity banner
                Surface(
                    color = WarmIvory,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Matrimonial Code of Conduct",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDeep
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "BiyeAbroad is dedicated to honorable, culturally respectful matrimony for the Bangladeshi community worldwide. Casual dating, vulgarity, financial scams, or harassment are strictly prohibited.",
                            fontSize = 11.sp,
                            color = DarkText,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FAQs Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QuestionAnswer,
                        contentDescription = null,
                        tint = BurgundyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Frequently Asked Questions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BurgundyPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // FAQ Accordions
                faqs.forEachIndexed { index, faq ->
                    val isExpanded = expandedFaqIndex == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isExpanded) Color(0xFFFFF9F9) else PureWhite),
                        border = BorderStroke(1.dp, if (isExpanded) BurgundyPrimary else GoldBorder.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedFaqIndex = if (isExpanded) -1 else index
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.question,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyDeep,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = BurgundyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    HorizontalDivider(color = GoldBorder.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = faq.answer,
                                        fontSize = 11.sp,
                                        color = DarkText,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Support Contact Section
                Surface(
                    color = BurgundyDeep.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Direct Support & Assistance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "If you have questions, feedback, or need help with your biodata, our administration team is available 24/7.",
                            fontSize = 11.sp,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:$supportEmail")
                                        putExtra(Intent.EXTRA_SUBJECT, "BiyeAbroad User Support Request")
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Hello BiyeAbroad Support Team,\n\nI need assistance with:\n"
                                        )
                                    }
                                    try {
                                        context.startActivity(Intent.createChooser(intent, "Email BiyeAbroad Support"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No email client installed", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("email_support_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurgundyPrimary,
                                    contentColor = PureWhite
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Email Support", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Support Email", supportEmail)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Support email copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(40.dp)
                                    .testTag("copy_support_email_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, BurgundyPrimary)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = BurgundyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Email", fontSize = 11.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgundyPrimary,
                    contentColor = PureWhite
                )
            ) {
                Text("Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
            }
        }
    )
}
