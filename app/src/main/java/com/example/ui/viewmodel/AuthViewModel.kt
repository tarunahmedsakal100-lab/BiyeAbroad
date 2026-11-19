package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.model.MatchmakerProfile
import com.example.data.model.UserProfile
import com.example.data.repository.BiyeAbroadRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class AccountType {
    USER,
    MATCHMAKER,
    ADMIN
}

class AuthViewModel(private val repository: BiyeAbroadRepository) : ViewModel() {

    private val auth = Firebase.auth

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _accountType = MutableStateFlow(AccountType.USER)
    val accountType: StateFlow<AccountType> = _accountType.asStateFlow()

    private val _matchmakerProfile = MutableStateFlow<MatchmakerProfile?>(null)
    val matchmakerProfile: StateFlow<MatchmakerProfile?> = _matchmakerProfile.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _demoUid = MutableStateFlow<String?>(null)
    val demoUid: StateFlow<String?> = _demoUid.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user
            if (user != null) {
                checkAdminRole(user)
                loadProfile(user.uid)
            } else {
                _userProfile.value = null
                _isAdmin.value = false
            }
        }
    }

    private fun checkAdminRole(user: FirebaseUser) {
        val email = user.email?.trim()?.lowercase() ?: ""
        val uid = user.uid
        if (email == "tarunahmedsakal100@gmail.com" || uid == TARUN_ADMIN_UID || uid == "ZjO7C62fDZfRSXKYllQSoRFXFYg1" || uid == "xWDJ4WM2qiZXYLCAO924hAJFDuz1") {
            _isAdmin.value = true
            _accountType.value = AccountType.ADMIN
        }
    }

    private fun loadProfile(uid: String) {
        viewModelScope.launch {
            try {
                // Check if user is a registered Matchmaker
                repository.observeMatchmakerProfile(uid).collect { mm ->
                    if (mm != null && mm.matchmakerId.isNotBlank()) {
                        _matchmakerProfile.value = mm
                        _accountType.value = AccountType.MATCHMAKER
                    }
                }
            } catch (e: Exception) {
                Log.w("AuthViewModel", "Matchmaker observe: ${e.message}")
            }
        }

        viewModelScope.launch {
            try {
                repository.observeUserProfile(uid).collect { profile ->
                    if (profile != null) {
                        _userProfile.value = profile
                        if (profile.role == "admin") {
                            _isAdmin.value = true
                            _accountType.value = AccountType.ADMIN
                        } else if (profile.role == "matchmaker") {
                            _accountType.value = AccountType.MATCHMAKER
                        } else {
                            if (_matchmakerProfile.value == null) {
                                _accountType.value = AccountType.USER
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("AuthViewModel", "Profile observe: ${e.message}")
            }
        }
    }

    fun attemptAutoSignIn(context: Context) {
        if (auth.currentUser != null) return

        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(context)
        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(authCredential).await()
                }
            } catch (e: Exception) {
                // Silent auto-sign-in failed; keep login UI visible
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            _errorMessage.value = "Google Sign-In configuration missing"
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()
        val credentialManager = CredentialManager.create(context)

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(context as Activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(authCredential).await()
                } else {
                    _errorMessage.value = "Unexpected credential returned"
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("Auth", "Google Sign-In cancelled: ${e.message}", e)
                if (e.message?.contains("Account reauth failed", ignoreCase = true) == true) {
                    _errorMessage.value = "Google Play Services required re-authentication. Please tap 'Sign in with Google' again."
                } else {
                    _errorMessage.value = "Google Sign-In prompt was dismissed. You can also sign in with Email & Password or explore via the 1-Tap Access buttons below."
                }
            } catch (e: Exception) {
                Log.e("Auth", "Google Sign-In failed", e)
                _errorMessage.value = e.localizedMessage ?: "Sign-in failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithEmail(
        email: String,
        pass: String,
        preferMatchmakerPortal: Boolean = false,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || pass.isBlank()) {
            val msg = "Please enter both email and password."
            _errorMessage.value = msg
            onComplete(false, msg)
            return
        }
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val isDedicatedAdmin = trimmedEmail.equals("tarunahmedsakal100@gmail.com", ignoreCase = true)
                try {
                    auth.signInWithEmailAndPassword(trimmedEmail, pass).await()
                } catch (authEx: Exception) {
                    if (isDedicatedAdmin) {
                        // If dedicated admin account hasn't been created in Firebase Auth with this password yet, create it!
                        try {
                            val res = auth.createUserWithEmailAndPassword(trimmedEmail, pass).await()
                            val uid = res.user?.uid ?: ""
                            val adminProf = UserProfile(
                                userId = uid,
                                email = trimmedEmail,
                                displayName = "Tarun Ahmed (Admin)",
                                role = "admin"
                            )
                            repository.saveUserProfile(adminProf)
                            _userProfile.value = adminProf
                            _isAdmin.value = true
                            _accountType.value = AccountType.ADMIN
                        } catch (_: Exception) {
                            throw authEx
                        }
                    } else {
                        throw authEx
                    }
                }
                if (preferMatchmakerPortal && !isDedicatedAdmin) {
                    _accountType.value = AccountType.MATCHMAKER
                }
                _isLoading.value = false
                onComplete(true, null)
            } catch (e: Exception) {
                _isLoading.value = false
                val msg = e.localizedMessage ?: "Failed to sign in. Please verify your email and password."
                _errorMessage.value = msg
                onComplete(false, msg)
            }
        }
    }

    fun signInWithEmail(email: String, pass: String, onComplete: (Boolean) -> Unit = {}) {
        signInWithEmail(email, pass, preferMatchmakerPortal = false) { success, _ ->
            onComplete(success)
        }
    }

    fun signUpMemberWithEmail(
        email: String,
        pass: String,
        fullName: String,
        requireLoginAfterCreate: Boolean = false,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || pass.length < 6) {
            _errorMessage.value = "Please provide a valid email and password (minimum 6 characters)."
            onComplete(false)
            return
        }
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                var uid = ""
                try {
                    val res = auth.createUserWithEmailAndPassword(trimmedEmail, pass).await()
                    uid = res.user?.uid ?: ""
                } catch (collisionEx: Exception) {
                    if (collisionEx.message?.contains("already in use", ignoreCase = true) == true) {
                        try {
                            val signRes = auth.signInWithEmailAndPassword(trimmedEmail, pass).await()
                            uid = signRes.user?.uid ?: ""
                        } catch (_: Exception) {
                            throw collisionEx
                        }
                    } else {
                        throw collisionEx
                    }
                }

                val roleToSet = if (trimmedEmail.equals("tarunahmedsakal100@gmail.com", ignoreCase = true)) "admin" else "user"
                val newProfile = UserProfile(
                    userId = uid,
                    email = trimmedEmail,
                    displayName = fullName.ifBlank { if (roleToSet == "admin") "Tarun Ahmed (Admin)" else "Member" },
                    role = roleToSet
                )
                repository.saveUserProfile(newProfile)

                if (requireLoginAfterCreate) {
                    // Explicit sign-out requested
                    auth.signOut()
                    _currentUser.value = null
                    _userProfile.value = null
                    _accountType.value = AccountType.USER
                } else {
                    _currentUser.value = auth.currentUser
                    _userProfile.value = newProfile
                    _accountType.value = if (roleToSet == "admin") AccountType.ADMIN else AccountType.USER
                }

                _isLoading.value = false
                onComplete(true)
            } catch (e: Exception) {
                _isLoading.value = false
                val friendlyMsg = when {
                    e.message?.contains("already in use", ignoreCase = true) == true ->
                        "This email is already registered. Please switch to 'Sign In' to enter your password, or tap 'Forgot Password'."
                    e.message?.contains("badly formatted", ignoreCase = true) == true ->
                        "The email address format is invalid. Please check your email."
                    e.message?.contains("weak-password", ignoreCase = true) == true ->
                        "Password is too weak. Please use at least 6 characters with letters and numbers."
                    else -> e.localizedMessage ?: "Registration failed. Please try again."
                }
                _errorMessage.value = friendlyMsg
                onComplete(false)
            }
        }
    }

    fun registerMatchmakerWithCredentials(
        email: String,
        pass: String,
        profile: MatchmakerProfile,
        requireLoginAfterCreate: Boolean = false,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || pass.length < 6) {
            _errorMessage.value = "Please provide a valid email and password (minimum 6 characters)."
            onComplete(false)
            return
        }
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                var uid = ""
                try {
                    val authRes = auth.createUserWithEmailAndPassword(trimmedEmail, pass).await()
                    uid = authRes.user?.uid ?: ""
                } catch (collisionEx: Exception) {
                    if (collisionEx.message?.contains("already in use", ignoreCase = true) == true) {
                        try {
                            val signRes = auth.signInWithEmailAndPassword(trimmedEmail, pass).await()
                            uid = signRes.user?.uid ?: ""
                        } catch (_: Exception) {
                            throw collisionEx
                        }
                    } else {
                        throw collisionEx
                    }
                }

                val completeMmProfile = profile.copy(
                    matchmakerId = uid,
                    email = trimmedEmail,
                    verificationStatus = "pending",
                    currentPlanId = "plan_free_launch",
                    maxManagedProfiles = 10,
                    termsAccepted = true
                )
                repository.registerMatchmaker(completeMmProfile)

                // Save user record with role = "matchmaker"
                val mmUserProf = UserProfile(
                    userId = uid,
                    email = trimmedEmail,
                    displayName = profile.fullName.ifBlank { "Matchmaker" },
                    role = "matchmaker"
                )
                repository.saveUserProfile(mmUserProf)

                if (requireLoginAfterCreate) {
                    auth.signOut()
                    _currentUser.value = null
                    _userProfile.value = null
                    _matchmakerProfile.value = null
                    _accountType.value = AccountType.USER
                } else {
                    _currentUser.value = auth.currentUser
                    _matchmakerProfile.value = completeMmProfile
                    _userProfile.value = mmUserProf
                    _accountType.value = AccountType.MATCHMAKER
                }

                _isLoading.value = false
                onComplete(true)
            } catch (e: Exception) {
                _isLoading.value = false
                val friendlyMsg = when {
                    e.message?.contains("already in use", ignoreCase = true) == true ->
                        "This email is already registered. Please switch to 'Sign In' or use 'Forgot Password'."
                    e.message?.contains("badly formatted", ignoreCase = true) == true ->
                        "The email address format is invalid. Please check your email."
                    e.message?.contains("weak-password", ignoreCase = true) == true ->
                        "Password is too weak. Please use at least 6 characters."
                    else -> e.localizedMessage ?: "Matchmaker registration failed."
                }
                _errorMessage.value = friendlyMsg
                onComplete(false)
            }
        }
    }

    fun completeOnboarding(profile: UserProfile, onComplete: () -> Unit) {
        val user = auth.currentUser ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val roleToSet = if (user.email?.lowercase() == "tarunahmedsakal100@gmail.com" || user.uid == "xWDJ4WM2qiZXYLCAO924hAJFDuz1") "admin" else "user"
            val updated = profile.copy(
                userId = user.uid,
                email = user.email ?: "",
                displayName = if (profile.displayName.isNotBlank()) profile.displayName else (user.displayName ?: "Member"),
                photoUrl = if (profile.photoUrl.isNotBlank()) profile.photoUrl else (user.photoUrl?.toString() ?: ""),
                role = roleToSet
            )
            val result = repository.saveUserProfile(updated)
            _isLoading.value = false
            if (result.isSuccess) {
                _userProfile.value = updated
                onComplete()
            } else {
                _errorMessage.value = "Failed to save profile: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    companion object {
        const val TARUN_USER_UID = "FhMFYaXqiafcyqDWrdx0I4s7EBw2"
        const val TARUN_MATCHMAKER_UID = "ghotok_tarun_pro_101"
        const val TARUN_ADMIN_UID = "ZjO7C62fDZfRSXKYllQSoRFXFYg1"
    }

    fun enterDemoMode(type: AccountType) {
        _accountType.value = type
        _isDemoMode.value = true

        when (type) {
            AccountType.ADMIN -> {
                _demoUid.value = TARUN_ADMIN_UID
                _isAdmin.value = true
                _userProfile.value = UserProfile(
                    userId = TARUN_ADMIN_UID,
                    email = "tarunahmedsakal100@gmail.com",
                    displayName = "Tarun Ahmed (Admin)",
                    role = "admin",
                    gender = "male",
                    age = 30,
                    country = "United Kingdom",
                    city = "London",
                    countryOfOrigin = "Bangladesh (Sylhet)",
                    religion = "",
                    education = "MSc in Software Engineering",
                    profession = "Platform Director & Admin",
                    maritalStatus = "Never Married",
                    height = "5'10\"",
                    bio = "Official Administrator of BiyeAbroad Matrimony Platform. Overseeing verified matchmaking, member security, and platform operations.",
                    verificationStatus = "verified",
                    membershipTier = "platinum"
                )
            }
            AccountType.MATCHMAKER -> {
                _demoUid.value = TARUN_MATCHMAKER_UID
                _isAdmin.value = false
                val mm = MatchmakerProfile(
                    matchmakerId = TARUN_MATCHMAKER_UID,
                    fullName = "Ghotok Tarun Ahmed (Matchmaker)",
                    photoUrl = "",
                    email = "ghotok.tarun@biyeabroad.com",
                    phone = "+880 1711-002233",
                    country = "Bangladesh",
                    city = "Sylhet & London",
                    languages = listOf("Bengali", "English", "Sylheti"),
                    experienceYears = 8,
                    bio = "Trusted, professional matrimonial matchmaker connecting noble Bangladeshi families across the UK, USA, Canada, and Bangladesh with dignity and mutual consent.",
                    referenceInfo = "Community Leader & Senior Matchmaker, Sylhet Welfare Association",
                    verificationStatus = "verified",
                    currentPlanId = "plan_free_launch",
                    maxManagedProfiles = 10,
                    managedProfileCount = 4,
                    termsAccepted = true
                )
                _matchmakerProfile.value = mm
                _userProfile.value = null
            }
            AccountType.USER -> {
                _demoUid.value = TARUN_USER_UID
                _isAdmin.value = false
                _matchmakerProfile.value = null
                _userProfile.value = UserProfile(
                    userId = TARUN_USER_UID,
                    email = "tarun.member@biyeabroad.com",
                    displayName = "Tarun Ahmed (Member)",
                    role = "user",
                    gender = "male",
                    age = 27,
                    country = "United Kingdom",
                    city = "London",
                    countryOfOrigin = "Bangladesh (Sylhet)",
                    religion = "",
                    education = "BSc Computer Science",
                    profession = "Software Consultant",
                    maritalStatus = "Never Married",
                    height = "5'9\"",
                    bio = "Warm, balanced, and family-oriented Bangladeshi professional living in London. Looking for an educated, kind-hearted life partner.",
                    verificationStatus = "verified",
                    membershipTier = "gold"
                )
            }
        }
    }

    fun enterDemoMode(asAdmin: Boolean) {
        enterDemoMode(if (asAdmin) AccountType.ADMIN else AccountType.USER)
    }

    fun selectAccountType(type: AccountType) {
        _accountType.value = type
        if (_isDemoMode.value) {
            enterDemoMode(type)
        }
    }

    fun registerMatchmaker(profile: MatchmakerProfile, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val res = repository.registerMatchmaker(profile)
            _isLoading.value = false
            if (res.isSuccess) {
                _matchmakerProfile.value = profile
                _accountType.value = AccountType.MATCHMAKER
                onComplete(true)
            } else {
                _errorMessage.value = res.exceptionOrNull()?.message ?: "Matchmaker registration failed."
                onComplete(false)
            }
        }
    }

    fun switchAccountIdentity(toAdmin: Boolean) {
        enterDemoMode(asAdmin = toAdmin)
    }

    fun exitDemoMode() {
        _isDemoMode.value = false
        _demoUid.value = null
        if (auth.currentUser == null) {
            _userProfile.value = null
            _matchmakerProfile.value = null
            _accountType.value = AccountType.USER
            _isAdmin.value = false
        }
    }

    fun signOut(context: Context) {
        _isDemoMode.value = false
        _demoUid.value = null
        _matchmakerProfile.value = null
        _userProfile.value = null
        _accountType.value = AccountType.USER
        _isAdmin.value = false
        val credentialManager = CredentialManager.create(context)
        auth.signOut()
        viewModelScope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e("Auth", "Clear credentials failed: ${e.message}")
            }
        }
    }

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, String?) -> Unit) {
        val trimmed = email.trim()
        if (trimmed.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            onComplete(false, "Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(trimmed).await()
                onComplete(
                    true,
                    "Password reset email sent to $trimmed. Please check your inbox (and spam folder) to set a new password."
                )
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("user-not-found", ignoreCase = true) == true ->
                        "No account found with this email address. Please check spelling or create an account."
                    e.message?.contains("invalid-email", ignoreCase = true) == true ->
                        "The email address format is invalid."
                    else -> e.localizedMessage ?: "Failed to send reset email. Please try again."
                }
                onComplete(false, msg)
            }
        }
    }

    fun isAuthorizedAdminUser(user: FirebaseUser?): Boolean {
        if (user == null) return false
        val email = user.email?.trim()?.lowercase() ?: ""
        val uid = user.uid
        return email == "tarunahmedsakal100@gmail.com" ||
                uid == TARUN_ADMIN_UID ||
                uid == "ZjO7C62fDZfRSXKYllQSoRFXFYg1" ||
                uid == "xWDJ4WM2qiZXYLCAO924hAJFDuz1" ||
                _userProfile.value?.role == "admin"
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
