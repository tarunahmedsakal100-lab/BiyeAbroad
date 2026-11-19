package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.DemoProfiles
import com.example.data.model.Conversation
import com.example.data.model.FamilyChat
import com.example.data.model.FamilyChatMessage
import com.example.data.model.FamilyConsentRequest
import com.example.data.model.Interest
import com.example.data.model.Message
import com.example.data.model.UserProfile
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
import com.example.ui.viewmodel.MainViewModel

@Composable
fun InterestsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val receivedInterests by viewModel.receivedInterests.collectAsState()
    val sentInterests by viewModel.sentInterests.collectAsState()
    val allProfiles by viewModel.discoveryProfiles.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = PureWhite,
            contentColor = BurgundyPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = BurgundyPrimary
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = "Received (${receivedInterests.size})",
                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = "Sent (${sentInterests.size})",
                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }

        if (selectedTabIndex == 0) {
            if (receivedInterests.isEmpty()) {
                EmptyInterestsView(title = "No received interests yet", subtitle = "When a member expresses interest in your matrimonial biodata, they will appear here.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(receivedInterests) { interest ->
                        val sender = allProfiles.find { it.userId == interest.senderId }
                            ?: DemoProfiles.sampleProfiles.first()
                        ReceivedInterestCard(
                            interest = interest,
                            sender = sender,
                            onAccept = { viewModel.respondToInterest(interest, "accepted") },
                            onDecline = { viewModel.respondToInterest(interest, "declined") },
                            onView = { viewModel.openProfile(sender) }
                        )
                    }
                }
            }
        } else {
            if (sentInterests.isEmpty()) {
                EmptyInterestsView(title = "No sent interests", subtitle = "Browse the Discover tab and express interest to members you feel compatible with.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sentInterests) { interest ->
                        val receiver = allProfiles.find { it.userId == interest.receiverId }
                            ?: DemoProfiles.sampleProfiles.first()
                        SentInterestCard(
                            interest = interest,
                            receiver = receiver,
                            onView = { viewModel.openProfile(receiver) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceivedInterestCard(
    interest: Interest,
    sender: UserProfile,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onView: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(BurgundyDeep)
                        .clickable(onClick = onView)
                ) {
                    if (sender.photoUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(sender.photoUrl),
                            contentDescription = sender.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight, modifier = Modifier.size(30.dp).align(Alignment.Center))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${sender.displayName}, ${sender.age}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BurgundyDeep
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        VerificationBadge(status = sender.verificationStatus)
                    }
                    Text(
                        text = "${sender.profession} • ${sender.city}, ${sender.country}",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (interest.status == "pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF888888))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Decline", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept Interest", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Surface(
                    color = if (interest.status == "accepted") SuccessGreen.copy(alpha = 0.15f) else Color(0xFFEEEEEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (interest.status == "accepted") "You accepted this interest. Conversation is open!" else "Interest declined",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (interest.status == "accepted") SuccessGreen else DarkText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SentInterestCard(
    interest: Interest,
    receiver: UserProfile,
    onView: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(BurgundyDeep)
                    .clickable(onClick = onView)
            ) {
                if (receiver.photoUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(receiver.photoUrl),
                        contentDescription = receiver.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight, modifier = Modifier.size(26.dp).align(Alignment.Center))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${receiver.displayName}, ${receiver.age}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkText
                )
                Text(
                    text = "${receiver.profession} • ${receiver.country}",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
            Surface(
                color = when (interest.status) {
                    "accepted" -> SuccessGreen.copy(alpha = 0.15f)
                    "declined" -> Color(0xFFFFEEEE)
                    else -> GoldLight.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = interest.status.replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (interest.status) {
                        "accepted" -> SuccessGreen
                        "declined" -> Color(0xFFBA1A1A)
                        else -> BurgundyDeep
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyInterestsView(title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = GoldBorder, modifier = Modifier.size(52.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BurgundyDeep)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = subtitle, fontSize = 12.sp, color = MutedText, textAlign = TextAlign.Center, lineHeight = 16.sp)
        }
    }
}

@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    val familyChats by viewModel.familyChats.collectAsState()
    val pendingConsentRequests by viewModel.receivedFamilyConsentRequests.collectAsState()
    val allProfiles by viewModel.discoveryProfiles.collectAsState()
    val activeConversation by viewModel.activeConversation.collectAsState()
    val activeFamilyChat by viewModel.activeFamilyChat.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    if (activeConversation != null) {
        val otherUserId = activeConversation!!.members.firstOrNull { it != viewModel.currentUserId } ?: ""
        val otherProfile = allProfiles.find { it.userId == otherUserId }
            ?: DemoProfiles.sampleProfiles.first()

        ChatScreen(
            conversation = activeConversation!!,
            partnerProfile = otherProfile,
            viewModel = viewModel,
            onBack = { viewModel.closeConversation() }
        )
    } else if (activeFamilyChat != null) {
        FamilyChatScreen(
            familyChat = activeFamilyChat!!,
            viewModel = viewModel,
            onBack = { viewModel.closeFamilyChat() }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(WarmIvory)
        ) {
            Surface(
                color = PureWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Messages & Family Discussion",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = PureWhite,
                        contentColor = BurgundyPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = BurgundyPrimary,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Private Chats (${conversations.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(17.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Family Rooms (${familyChats.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // TAB 0: PRIVATE 1-ON-1 MATCHMAKING CONVERSATIONS
                if (conversations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(52.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No active conversations", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BurgundyDeep)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "When you and another member mutually accept interest, your secure private matchmaking chat unlocks here.",
                                fontSize = 12.sp,
                                color = MutedText,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 12.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(conversations) { conv ->
                            val otherId = conv.members.firstOrNull { it != viewModel.currentUserId } ?: ""
                            val partner = allProfiles.find { it.userId == otherId }
                                ?: DemoProfiles.sampleProfiles.first()

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openConversation(conv) }
                                    .testTag("conversation_item_${conv.conversationId}"),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(BurgundyDeep)
                                    ) {
                                        if (partner.photoUrl.isNotBlank()) {
                                            Image(
                                                painter = rememberAsyncImagePainter(partner.photoUrl),
                                                contentDescription = partner.displayName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight, modifier = Modifier.size(26.dp).align(Alignment.Center))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = partner.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = DarkText
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            VerificationBadge(status = partner.verificationStatus)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = conv.lastMessage.ifBlank { "Say Hello..." },
                                            fontSize = 12.sp,
                                            color = MutedText,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // TAB 1: BIYEABROAD FAMILY DISCUSSION ROOMS & TWO-SIDED CONSENT
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Educational Banner
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = BurgundyDeep, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Family Discussion Rooms",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BurgundyDeep
                                    )
                                    Text(
                                        text = "Involves parents & guardians with independent accounts. Created only when both prospective families grant mutual two-sided consent.",
                                        fontSize = 11.sp,
                                        color = DarkText,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    // Pending Two-Sided Consent Requests Received
                    val pendingRequests = pendingConsentRequests.filter { it.status == "pending" }
                    if (pendingRequests.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pending Family Room Requests (${pendingRequests.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(pendingRequests) { req ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Groups, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${req.requesterName}'s Family",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = DarkText
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "has requested to open a Family Discussion Room with you and your family. Both families must consent before messages unlock.",
                                        fontSize = 12.sp,
                                        color = MutedText
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.respondToFamilyConsent(req.requestId, false) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                                        ) {
                                            Text("Decline", fontSize = 12.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.respondToFamilyConsent(req.requestId, true) },
                                            modifier = Modifier.weight(1.5f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Consent & Open Room", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Active Family Chat Rooms List
                    item {
                        Text(
                            text = "Active Family Rooms",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDeep,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    if (familyChats.isEmpty()) {
                        item {
                            Surface(
                                color = PureWhite,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Groups, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(44.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No Active Family Rooms",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BurgundyDeep
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Invite your parents/guardians from the Profile tab, and request Family Chat with any mutual match.",
                                        fontSize = 12.sp,
                                        color = MutedText,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { viewModel.showFamilyInviteDialog(true) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
                                    ) {
                                        Text("Invite Family Member", fontSize = 12.sp, color = PureWhite)
                                    }
                                }
                            }
                        }
                    } else {
                        items(familyChats) { chat ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openFamilyChat(chat) }
                                    .testTag("family_chat_item_${chat.chatId}"),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(BurgundyDeep),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Groups, contentDescription = null, tint = GoldLight, modifier = Modifier.size(24.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = chat.title.ifBlank { "Family Discussion Room" },
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = DarkText
                                                )
                                                Text(
                                                    text = "${chat.participantUids.size} Family Participants",
                                                    fontSize = 11.sp,
                                                    color = BurgundyPrimary
                                                )
                                            }
                                        }

                                        Surface(
                                            color = SuccessGreen.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "Consented",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = GoldBorder.copy(alpha = 0.4f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (chat.lastSenderRelation.isNotBlank()) {
                                            Surface(
                                                color = BurgundyPrimary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = chat.lastSenderRelation,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BurgundyPrimary,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = chat.lastMessage.ifBlank { "Family room active." },
                                            fontSize = 12.sp,
                                            color = MutedText,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
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
fun FamilyChatScreen(
    familyChat: FamilyChat,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var selectedRelation by remember { mutableStateOf("Self") }

    val messagesFlow = remember(familyChat.chatId) {
        viewModel.observeFamilyChatMessages(familyChat.chatId)
    }
    val messages by messagesFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
    ) {
        // Family Chat Header
        Surface(
            color = BurgundyPrimary,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PureWhite)
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BurgundyDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = GoldLight, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = familyChat.title.ifBlank { "Family Discussion Room" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PureWhite
                    )
                    Text(
                        text = "${familyChat.participantUids.size} Verified Family Members • Two-Sided Consent",
                        fontSize = 11.sp,
                        color = GoldLight
                    )
                }
                Icon(Icons.Default.Lock, contentDescription = "Secure Family Room", tint = GoldLight, modifier = Modifier.size(20.dp).padding(end = 6.dp))
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == viewModel.currentUserId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        // Sender and Relation badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = msg.senderName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = when (msg.senderRelationship.lowercase()) {
                                    "father" -> Color(0xFFFEF3C7)
                                    "mother" -> Color(0xFFFFE4E6)
                                    "guardian" -> Color(0xFFE0F2FE)
                                    "self" -> BurgundyPrimary.copy(alpha = 0.15f)
                                    else -> Color(0xFFF3E8FF)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = msg.senderRelationship,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BurgundyDeep,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            color = if (isMe) BurgundyPrimary else PureWhite,
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (isMe) 14.dp else 2.dp,
                                bottomEnd = if (isMe) 2.dp else 14.dp
                            ),
                            border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = msg.text,
                                color = if (isMe) PureWhite else DarkText,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }
        }

        // Input Bar with Relationship Tag Selection
        Surface(
            color = PureWhite,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Relationship Tag Chips: "Speaking as: Self, Father, Mother, Guardian"
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Speaking as:", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.SemiBold)
                    listOf("Self", "Father", "Mother", "Guardian").forEach { role ->
                        val isSelected = selectedRelation == role
                        Surface(
                            color = if (isSelected) BurgundyPrimary else WarmIvory,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BurgundyPrimary else GoldBorder),
                            modifier = Modifier.clickable { selectedRelation = role }
                        ) {
                            Text(
                                text = role,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PureWhite else DarkText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Family message as $selectedRelation...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("family_chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = androidx.compose.ui.graphics.Color.Black,
                            unfocusedTextColor = androidx.compose.ui.graphics.Color.Black,
                            focusedBorderColor = BurgundyPrimary,
                            unfocusedBorderColor = GoldBorder
                        ),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendFamilyChatMessage(familyChat.chatId, messageText, selectedRelation)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BurgundyPrimary)
                            .testTag("family_chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Family Message",
                            tint = GoldLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    conversation: Conversation,
    partnerProfile: UserProfile,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    // Observe messages for this conversation
    val messagesFlow = remember(conversation.conversationId) {
        viewModel.observeMessages(conversation.conversationId)
    }
    val messages by messagesFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
    ) {
        // Chat Header
        Surface(
            color = BurgundyPrimary,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PureWhite)
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BurgundyDeep)
                ) {
                    if (partnerProfile.photoUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(partnerProfile.photoUrl),
                            contentDescription = partnerProfile.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight, modifier = Modifier.size(22.dp).align(Alignment.Center))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = partnerProfile.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PureWhite
                    )
                    Text(
                        text = "${partnerProfile.profession} • ${partnerProfile.country}",
                        fontSize = 11.sp,
                        color = GoldLight
                    )
                }
                IconButton(onClick = { viewModel.showReportDialog(partnerProfile) }) {
                    Icon(Icons.Default.Flag, contentDescription = "Report", tint = GoldLight.copy(alpha = 0.8f))
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == viewModel.currentUserId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isMe) BurgundyPrimary else PureWhite,
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        ),
                        border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = msg.text,
                            color = if (isMe) PureWhite else DarkText,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }

        // Input Bar
        Surface(
            color = PureWhite,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Write a respectful message...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = androidx.compose.ui.graphics.Color.Black,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.Black,
                        focusedBorderColor = BurgundyPrimary,
                        unfocusedBorderColor = GoldBorder
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(conversation.conversationId, messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BurgundyPrimary)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = GoldLight
                    )
                }
            }
        }
    }
}
