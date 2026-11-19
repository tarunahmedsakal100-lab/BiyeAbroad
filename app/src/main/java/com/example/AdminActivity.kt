package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.repository.BiyeAbroadRepository
import com.example.ui.screens.AdminAccessDeniedScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AccountType
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BiyeAbroadAdminApp()
            }
        }
    }
}

@Composable
fun BiyeAbroadAdminApp() {
    val context = LocalContext.current
    val repository = remember { BiyeAbroadRepository(context) }
    val authViewModel = remember { AuthViewModel(repository) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val isDemoMode by authViewModel.isDemoMode.collectAsState()
    val demoUid by authViewModel.demoUid.collectAsState()
    val accountType by authViewModel.accountType.collectAsState()

    val isDemoAdmin = isDemoMode && accountType == AccountType.ADMIN
    val isRealAdmin = currentUser != null && authViewModel.isAuthorizedAdminUser(currentUser)

    if (isDemoAdmin || isRealAdmin) {
        val activeUid = if (isDemoMode) (demoUid ?: AuthViewModel.TARUN_ADMIN_UID) else (currentUser?.uid ?: "")
        val mainViewModel = remember(activeUid) {
            MainViewModel(repository, activeUid)
        }

        AdminDashboardScreen(
            viewModel = mainViewModel,
            authViewModel = authViewModel,
            onExitAdmin = {
                if (isDemoMode) {
                    authViewModel.exitDemoMode()
                } else {
                    authViewModel.signOut(context)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
        )
    } else if (currentUser != null) {
        // Authenticated with a regular member account; block admin access
        AdminAccessDeniedScreen(
            userEmail = currentUser?.email ?: "Unknown Member Account",
            onSignOut = { authViewModel.signOut(context) },
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        )
    } else {
        // Show Admin Login
        AdminLoginScreen(
            authViewModel = authViewModel,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        )
    }
}
