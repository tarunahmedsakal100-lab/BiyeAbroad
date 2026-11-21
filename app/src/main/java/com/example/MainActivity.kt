package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.BiyeAbroadRepository
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.UserAppRoot
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.MutedText
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WarmIvory
import com.example.ui.viewmodel.AccountType
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BiyeAbroadApp()
            }
        }
    }
}

@Composable
fun BiyeAbroadApp() {
    val context = LocalContext.current
    val repository = remember { BiyeAbroadRepository(context) }
    val authViewModel = remember { AuthViewModel(repository) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val isDemoMode by authViewModel.isDemoMode.collectAsState()
    val demoUid by authViewModel.demoUid.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val isProfileLoading by authViewModel.isProfileLoading.collectAsState()

    var showSplash by remember { mutableStateOf(true) }

    val accountType by authViewModel.accountType.collectAsState()
    val matchmakerProfile by authViewModel.matchmakerProfile.collectAsState()

    val activeUid = if (isDemoMode) demoUid else currentUser?.uid

    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
    } else if (activeUid == null) {
        WelcomeScreen(
            authViewModel = authViewModel,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
        )
    } else if (accountType == AccountType.USER && userProfile == null && isProfileLoading && !isDemoMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmIvory)
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = BurgundyPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Loading your BiyeAbroad profile...",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            }
        }
    } else if (accountType == AccountType.USER && userProfile == null && !isDemoMode) {
        OnboardingScreen(
            authViewModel = authViewModel,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),
            onComplete = {
                // Profile loaded automatically via AuthStateListener
            }
        )
    } else {
        val mainViewModel = remember(activeUid) {
            MainViewModel(repository, activeUid)
        }
        UserAppRoot(
            mainViewModel = mainViewModel,
            authViewModel = authViewModel,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        )
    }
}
