package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandHeader
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AccountType
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun UserAppRoot(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by mainViewModel.selectedTab.collectAsState()
    val isAdmin by authViewModel.isAdmin.collectAsState()
    val isAdminMode by mainViewModel.isAdminMode.collectAsState()
    val selectedProfile by mainViewModel.selectedProfile.collectAsState()
    val showVerificationDialog by mainViewModel.showVerificationDialog.collectAsState()
    val showReportDialog by mainViewModel.showReportDialog.collectAsState()
    val showFamilyInviteDialog by mainViewModel.showFamilyInviteDialog.collectAsState()
    val showPremiumDialog by mainViewModel.showPremiumDialog.collectAsState()
    val userFeedback by mainViewModel.userFeedback.collectAsState()

    val accountType by authViewModel.accountType.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userFeedback) {
        userFeedback?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            mainViewModel.clearFeedback()
        }
    }

    if (accountType == AccountType.MATCHMAKER) {
        MatchmakerDashboardScreen(
            mainViewModel = mainViewModel,
            authViewModel = authViewModel,
            modifier = modifier,
            onExit = { authViewModel.enterDemoMode(AccountType.USER) }
        )
    } else {
        Scaffold(
            topBar = {
                BrandHeader(
                    compact = true,
                    isAdmin = false,
                    isAdminMode = false,
                    onAdminClick = null
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = PureWhite,
                    contentColor = BurgundyPrimary,
                    tonalElevation = 6.dp
                ) {
                    val tabs = listOf(
                        MainTab.HOME to Icons.Default.Home,
                        MainTab.DISCOVER to Icons.Default.Explore,
                        MainTab.INTERESTS to Icons.Default.Favorite,
                        MainTab.MESSAGES to Icons.Default.Chat,
                        MainTab.PROFILE to Icons.Default.Person
                    )

                    tabs.forEach { (tab, icon) ->
                        val selected = selectedTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { mainViewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BurgundyPrimary,
                                selectedTextColor = BurgundyPrimary,
                                indicatorColor = GoldLight.copy(alpha = 0.4f),
                                unselectedIconColor = MutedText,
                                unselectedTextColor = MutedText
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
            ) {
                when (selectedTab) {
                    MainTab.HOME -> HomeScreen(viewModel = mainViewModel)
                    MainTab.DISCOVER -> DiscoverScreen(viewModel = mainViewModel)
                    MainTab.INTERESTS -> InterestsScreen(viewModel = mainViewModel)
                    MainTab.MESSAGES -> MessagesScreen(viewModel = mainViewModel)
                    MainTab.PROFILE -> ProfileScreen(mainViewModel = mainViewModel, authViewModel = authViewModel)
                }
            }
        }
    }

    val familyInvitations by mainViewModel.familyInvitations.collectAsState()
    val incomingFamilyInvitations by mainViewModel.incomingFamilyInvitations.collectAsState()
    val familyMemberships by mainViewModel.familyMemberships.collectAsState()
    val myProfile by mainViewModel.myProfile.collectAsState()

    // Modal Dialogs
    selectedProfile?.let { profile ->
        ProfileDetailDialog(
            profile = profile,
            onDismiss = { mainViewModel.closeProfile() },
            onSendInterest = { mainViewModel.sendInterest(profile.userId) },
            onRequestFamilyChat = { mainViewModel.sendFamilyConsentRequest(profile.userId, profile.displayName) },
            onReport = { mainViewModel.showReportDialog(profile) }
        )
    }

    if (showVerificationDialog) {
        VerificationSubmissionDialog(
            onDismiss = { mainViewModel.showVerificationDialog(false) },
            onSubmit = { docType, docNum ->
                mainViewModel.submitVerification(docType, docNum)
            }
        )
    }

    showReportDialog?.let { reportedUser ->
        ReportUserDialog(
            profile = reportedUser,
            onDismiss = { mainViewModel.showReportDialog(null) },
            onSubmit = { reason, details ->
                mainViewModel.submitReport(reportedUser.userId, reason, details)
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

    if (showPremiumDialog) {
        PremiumMembershipDialog(
            onDismiss = { mainViewModel.showPremiumDialog(false) }
        )
    }
}
