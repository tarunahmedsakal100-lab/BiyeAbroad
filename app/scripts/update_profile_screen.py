import sys

def main():
    path = "app/src/main/java/com/example/ui/screens/ProfileAndSettingsScreens.kt"
    with open(path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    start_idx = None
    end_idx = None
    for i, line in enumerate(lines):
        if "@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)" in line and "fun EditProfileDialog" in lines[i+2]:
            start_idx = i
        if "fun MatchmakerConsentDialog(" in line:
            end_idx = i
            break

    if start_idx is None or end_idx is None:
        print(f"Indices not found! start={start_idx}, end={end_idx}")
        sys.exit(1)

    print(f"Replacing lines {start_idx+1} to {end_idx} (0-indexed: {start_idx} to {end_idx})")

    family_dialog = """@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FamilyManagementDialog(
    invitations: List<FamilyInvitation>,
    memberships: List<FamilyMembership>,
    onDismiss: () -> Unit,
    onSendInvite: (email: String, relation: String, permissions: List<String>, onDone: (Boolean, String?) -> Unit) -> Unit,
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

    var isSending by remember { mutableStateOf(false) }
    var inviteDispatched by remember { mutableStateOf(false) }
    var dispatchedEmail by remember { mutableStateOf("") }
    var dispatchedToken by remember { mutableStateOf("") }

    val buildInviteMessage: (String, String, String) -> String = { toEmail, rel, token ->
        val sb = StringBuilder()
        sb.append("Assalamu Alaikum / Greetings,\\n\\n")
        sb.append("$inviterName has invited you as their $rel on BiyeAbroad — The dignified Bangladeshi Matrimonial Platform.\\n\\n")
        sb.append("As an authorized family member, you can:\\n")
        sb.append("• Review prospective bride/groom matrimonial matches\\n")
        sb.append("• Participate in consensual family chat discussions\\n\\n")
        sb.append("Your Family Invitation Code: $token\\n\\n")
        sb.append("How to join:\\n")
        sb.append("1. Open the BiyeAbroad App on Android\\n")
        sb.append("2. Register or sign in using this email address: $toEmail\\n")
        sb.append("3. You will automatically be linked to $inviterName's family circle.\\n\\n")
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

                // Tab Switcher: Invite vs Active Members
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = WarmIvory,
                    contentColor = BurgundyPrimary
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Invite", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Members (${memberships.size + invitations.filter { it.status == \\"pending\\" }.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
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
                                Surface(
                                    color = PureWhite,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("INVITATION CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                                            Text(dispatchedToken, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BurgundyPrimary)
                                        }
                                        IconButton(onClick = { copyToClipboard(dispatchedEmail, relationship, dispatchedToken) }) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = BurgundyPrimary)
                                        }
                                    }
                                }

                                Button(
                                    onClick = { launchEmailClient(dispatchedEmail, relationship, dispatchedToken) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Email App to Send Message", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { shareInvite(dispatchedEmail, relationship, dispatchedToken) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { copyToClipboard(dispatchedEmail, relationship, dispatchedToken) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy All", fontSize = 11.sp)
                                    }
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
                                onSendInvite(target, relationship, perms) { success, token ->
                                    isSending = false
                                    if (success) {
                                        inviteDispatched = true
                                        dispatchedEmail = target
                                        dispatchedToken = token ?: "INV-${System.currentTimeMillis().toString().takeLast(6)}"
                                        launchEmailClient(target, relationship, dispatchedToken)
                                    }
                                }
                            },
                            enabled = email.isNotBlank() && email.contains("@") && !isSending,
                            modifier = Modifier.fillMaxWidth().testTag("send_family_invite_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (isSending) "Registering Invitation..." else "Send Family Invitation",
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    // TAB 1: Active Family Memberships & Pending Invitations
                    val pending = invitations.filter { it.status == \\"pending\\" }
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
                    val activeMembers = memberships.filter { it.status == \\"active\\" }
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

"""

    new_lines = lines[:start_idx] + [family_dialog] + lines[end_idx:]
    with open(path, "w", encoding="utf-8") as f:
        f.writelines(new_lines)
    print("Replacement complete!")

if __name__ == "__main__":
    main()
