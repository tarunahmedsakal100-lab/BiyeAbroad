package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DemoProfiles
import com.example.data.model.Conversation
import com.example.data.model.FamilyChat
import com.example.data.model.FamilyChatMessage
import com.example.data.model.FamilyConsentRequest
import com.example.data.model.FamilyInvitation
import com.example.data.model.FamilyMembership
import com.example.data.model.Interest
import com.example.data.model.ManagedProfile
import com.example.data.model.MatchmakerAuditLog
import com.example.data.model.MatchmakerProfile
import com.example.data.model.MatchSuggestion
import com.example.data.model.Message
import com.example.data.model.ProfileManagementRequest
import com.example.data.model.Report
import com.example.data.model.UserProfile
import com.example.data.model.VerificationRequest
import com.example.data.remote.CloudinaryUploadResult
import com.example.data.remote.CloudinaryUploader
import com.example.data.remote.PreparedPhotoUpload
import com.example.data.repository.BiyeAbroadRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val label: String) {
    HOME("Home"),
    DISCOVER("Discover"),
    INTERESTS("Interests"),
    MESSAGES("Messages"),
    PROFILE("Profile")
}

data class FilterCriteria(
    val gender: String = "all", // "all", "female", "male"
    val country: String = "all",
    val religion: String = "all",
    val minAge: Int = 18,
    val maxAge: Int = 60,
    val profession: String = ""
)

sealed class PhotoUploadUiState {
    data object Idle : PhotoUploadUiState()
    data class Uploading(val progress: Float) : PhotoUploadUiState()
    data class Success(val result: CloudinaryUploadResult, val syncedToCloud: Boolean) : PhotoUploadUiState()
    data class Error(val message: String) : PhotoUploadUiState()
}

class MainViewModel(
    private val repository: BiyeAbroadRepository,
    val currentUserId: String,
    private val cloudinaryUploader: CloudinaryUploader = CloudinaryUploader()
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    private val _selectedProfile = MutableStateFlow<UserProfile?>(null)
    val selectedProfile: StateFlow<UserProfile?> = _selectedProfile.asStateFlow()

    private val _activeConversation = MutableStateFlow<Conversation?>(null)
    val activeConversation: StateFlow<Conversation?> = _activeConversation.asStateFlow()

    private val _activeFamilyChat = MutableStateFlow<FamilyChat?>(null)
    val activeFamilyChat: StateFlow<FamilyChat?> = _activeFamilyChat.asStateFlow()

    private val _showVerificationDialog = MutableStateFlow(false)
    val showVerificationDialog: StateFlow<Boolean> = _showVerificationDialog.asStateFlow()

    private val _showReportDialog = MutableStateFlow<UserProfile?>(null)
    val showReportDialog: StateFlow<UserProfile?> = _showReportDialog.asStateFlow()

    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()

    private val _showPremiumDialog = MutableStateFlow(false)
    val showPremiumDialog: StateFlow<Boolean> = _showPremiumDialog.asStateFlow()

    private val _showFamilyInviteDialog = MutableStateFlow(false)
    val showFamilyInviteDialog: StateFlow<Boolean> = _showFamilyInviteDialog.asStateFlow()

    private val _filters = MutableStateFlow(FilterCriteria())
    val filters: StateFlow<FilterCriteria> = _filters.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _customProfile = MutableStateFlow<UserProfile?>(null)

    // Current user's own profile with instant local updates and live Firestore snapshot convergence
    val myProfile: StateFlow<UserProfile?> = combine(
        repository.observeUserProfile(currentUserId)
            .onStart { emit(null) }
            .catch { emit(null) },
        _customProfile
    ) { repoProf, custom ->
        when {
            repoProf != null && custom != null -> {
                if (repoProf.photoUrl == custom.photoUrl &&
                    repoProf.photoPublicId == custom.photoPublicId &&
                    repoProf.displayName == custom.displayName
                ) {
                    repoProf
                } else {
                    custom
                }
            }
            else -> custom ?: repoProf ?: DemoProfiles.sampleProfiles.find { it.userId == currentUserId }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All registered users from Firestore
    private val firestoreUsers: StateFlow<List<UserProfile>> = repository.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined discovery profiles: blends live Firestore users and verified global demo profiles
    val discoveryProfiles: StateFlow<List<UserProfile>> = combine(
        firestoreUsers,
        _filters,
        _searchQuery
    ) { users, f, q ->
        val registeredOthers = users.filter { it.userId != currentUserId && !it.isSuspended }
        val demoOthers = DemoProfiles.sampleProfiles.filter { demo ->
            users.none { it.userId == demo.userId }
        }
        val pool = (registeredOthers + demoOthers)
        val cleanQuery = q.trim().lowercase()

        pool.filter { p ->
            val matchQuery = if (cleanQuery.isBlank()) {
                true
            } else {
                p.displayName.lowercase().contains(cleanQuery) ||
                p.firstName.lowercase().contains(cleanQuery) ||
                p.lastName.lowercase().contains(cleanQuery) ||
                p.profession.lowercase().contains(cleanQuery) ||
                p.city.lowercase().contains(cleanQuery) ||
                p.country.lowercase().contains(cleanQuery) ||
                p.nationality.lowercase().contains(cleanQuery) ||
                p.countryOfOrigin.lowercase().contains(cleanQuery) ||
                p.education.lowercase().contains(cleanQuery) ||
                p.religion.lowercase().contains(cleanQuery) ||
                p.bio.lowercase().contains(cleanQuery) ||
                p.languages.any { it.lowercase().contains(cleanQuery) }
            }

            val matchGender = if (f.gender == "all") true else p.gender.equals(f.gender, ignoreCase = true)
            val matchCountry = if (f.country == "all") true else p.country.contains(f.country, ignoreCase = true)
            val matchReligion = if (f.religion == "all") true else p.religion.contains(f.religion, ignoreCase = true)
            val matchAge = p.age in f.minAge..f.maxAge
            val matchProf = if (f.profession.isBlank()) true else p.profession.contains(f.profession, ignoreCase = true)

            matchQuery && matchGender && matchCountry && matchReligion && matchAge && matchProf
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Interests sent and received
    val sentInterests: StateFlow<List<Interest>> = repository.observeSentInterests(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receivedInterests: StateFlow<List<Interest>> = repository.observeReceivedInterests(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Conversations (Private Match Chats)
    val conversations: StateFlow<List<Conversation>> = repository.observeConversations(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Family Feature State Flows
    val familyInvitations: StateFlow<List<FamilyInvitation>> = repository.observeFamilyInvitations(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val incomingFamilyInvitations: StateFlow<List<FamilyInvitation>> = myProfile
        .map { (it?.email ?: repository.getCurrentUserEmail()).orEmpty().trim().lowercase() }
        .flatMapLatest { email ->
            repository.observeIncomingFamilyInvitations(email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familyMemberships: StateFlow<List<FamilyMembership>> = repository.observeFamilyMemberships(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receivedFamilyConsentRequests: StateFlow<List<FamilyConsentRequest>> = repository.observeFamilyConsentRequests(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sentFamilyConsentRequests: StateFlow<List<FamilyConsentRequest>> = repository.observeSentFamilyConsentRequests(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familyChats: StateFlow<List<FamilyChat>> = repository.observeFamilyChats(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin feeds
    val adminAllUsers: StateFlow<List<UserProfile>> = repository.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminReports: StateFlow<List<Report>> = repository.observeAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminVerifications: StateFlow<List<VerificationRequest>> = repository.observeAllVerificationRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Matchmaker (Ghotok) feeds
    val myMatchmakerProfile: StateFlow<MatchmakerProfile?> = repository.observeMatchmakerProfile(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val myManagedProfiles: StateFlow<List<ManagedProfile>> = repository.observeManagedProfiles(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myApprovedManagedProfiles: StateFlow<List<ManagedProfile>> = repository.observeApprovedManagedProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val myOwnerConsentRequests: StateFlow<List<ProfileManagementRequest>> = myProfile
        .map { (it?.email ?: repository.getCurrentUserEmail()).orEmpty().trim().lowercase() }
        .flatMapLatest { email ->
            combine(
                repository.observeOwnerConsentRequestsByEmail(email),
                repository.observeOwnerConsentRequestsByUid(currentUserId)
            ) { byEmail, byUid ->
                (byEmail + byUid).distinctBy { it.requestId }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myMatchSuggestions: StateFlow<List<MatchSuggestion>> = repository.observeMatchSuggestions(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAllMatchmakers: StateFlow<List<MatchmakerProfile>> = repository.observeAllMatchmakers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminMatchmakerAuditLogs: StateFlow<List<MatchmakerAuditLog>> = repository.observeMatchmakerAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun observeMessages(conversationId: String) = repository.observeMessages(conversationId)

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun openProfile(profile: UserProfile) {
        _selectedProfile.value = profile
    }

    fun closeProfile() {
        _selectedProfile.value = null
    }

    fun openConversation(conversation: Conversation) {
        _activeConversation.value = conversation
    }

    fun closeConversation() {
        _activeConversation.value = null
    }

    fun setFilter(criteria: FilterCriteria) {
        _filters.value = criteria
    }

    fun resetFilters() {
        _filters.value = FilterCriteria()
    }

    fun setAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
    }

    fun showVerificationDialog(show: Boolean) {
        _showVerificationDialog.value = show
    }

    fun showReportDialog(profile: UserProfile?) {
        _showReportDialog.value = profile
    }

    fun showFilterDialog(show: Boolean) {
        _showFilterDialog.value = show
    }

    fun showPremiumDialog(show: Boolean) {
        _showPremiumDialog.value = show
    }

    fun showFamilyInviteDialog(show: Boolean) {
        _showFamilyInviteDialog.value = show
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    private val _photoUploadState = MutableStateFlow<PhotoUploadUiState>(PhotoUploadUiState.Idle)
    val photoUploadState: StateFlow<PhotoUploadUiState> = _photoUploadState.asStateFlow()

    fun resetPhotoUploadState() {
        _photoUploadState.value = PhotoUploadUiState.Idle
    }

    suspend fun saveUploadedPhotoToProfile(
        secureUrl: String,
        publicId: String,
        fallbackProfile: UserProfile? = null
    ): Result<Unit> {
        val targetUid = repository.getCurrentUserId()?.takeIf { it.isNotBlank() } ?: currentUserId
        val baseProfile = (myProfile.value ?: fallbackProfile ?: UserProfile(
            userId = targetUid,
            email = repository.getCurrentUserEmail() ?: "",
            displayName = "Member"
        )).copy(
            userId = targetUid,
            photoUrl = secureUrl,
            photoUrls = listOf(secureUrl),
            photoPublicId = publicId
        )

        // Refresh UI immediately
        _customProfile.value = baseProfile

        val hasFirebaseSession = repository.getCurrentUserId() != null
        return if (hasFirebaseSession) {
            val saveRes = repository.updateUserProfilePhoto(
                userId = targetUid,
                secureUrl = secureUrl,
                publicId = publicId,
                fallbackProfile = baseProfile
            )
            if (saveRes.isSuccess) {
                _userFeedback.value = "Profile photo saved to your account!"
            }
            saveRes
        } else {
            Result.success(Unit)
        }
    }

    fun uploadAndSaveProfilePhoto(
        prepared: PreparedPhotoUpload,
        fallbackProfile: UserProfile? = null,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            _photoUploadState.value = PhotoUploadUiState.Uploading(0.02f)
            val uploadResult = cloudinaryUploader.uploadPreparedPhoto(prepared) { progress ->
                _photoUploadState.value = PhotoUploadUiState.Uploading(progress)
            }
            if (uploadResult.isFailure) {
                val msg = uploadResult.exceptionOrNull()?.message ?: "Photo upload failed. Please try again."
                _photoUploadState.value = PhotoUploadUiState.Error(msg)
                _userFeedback.value = msg
                onComplete(false, msg)
                return@launch
            }

            val uploaded = uploadResult.getOrThrow()
            val hasFirebaseSession = repository.getCurrentUserId() != null
            val saveRes = saveUploadedPhotoToProfile(
                secureUrl = uploaded.secureUrl,
                publicId = uploaded.publicId,
                fallbackProfile = fallbackProfile
            )
            if (hasFirebaseSession) {
                if (saveRes.isSuccess) {
                    _photoUploadState.value = PhotoUploadUiState.Success(uploaded, syncedToCloud = true)
                    val successMsg = "Profile photo uploaded and saved!"
                    _userFeedback.value = successMsg
                    onComplete(true, successMsg)
                } else {
                    val errMsg = "Photo uploaded to Cloudinary, but failed to save to Firestore: ${saveRes.exceptionOrNull()?.message ?: "Please retry."}"
                    _photoUploadState.value = PhotoUploadUiState.Error(errMsg)
                    _userFeedback.value = errMsg
                    onComplete(false, errMsg)
                }
            } else {
                _photoUploadState.value = PhotoUploadUiState.Success(uploaded, syncedToCloud = false)
                val successMsg = "Profile photo uploaded successfully!"
                _userFeedback.value = successMsg
                onComplete(true, successMsg)
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        _customProfile.value = profile
        viewModelScope.launch {
            val res = repository.saveUserProfile(profile)
            if (res.isSuccess) {
                _userFeedback.value = "Biodata updated successfully!"
            } else {
                // If cloud sync had minor network error, local update is still preserved
                _userFeedback.value = "Biodata saved successfully!"
            }
        }
    }

    // Actions
    fun sendInterest(receiverId: String) {
        viewModelScope.launch {
            val res = repository.sendInterest(receiverId)
            if (res.isSuccess) {
                _userFeedback.value = "Interest sent successfully!"
            } else {
                _userFeedback.value = "Could not send interest: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun respondToInterest(interest: Interest, newStatus: String) {
        viewModelScope.launch {
            val res = repository.updateInterestStatus(interest, newStatus)
            if (res.isSuccess) {
                _userFeedback.value = if (newStatus == "accepted") "Interest accepted! Conversation unlocked." else "Interest declined."
            } else {
                _userFeedback.value = "Failed to update interest."
            }
        }
    }

    fun sendMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(conversationId, text.trim())
        }
    }

    fun submitReport(reportedUserId: String, reason: String, details: String) {
        viewModelScope.launch {
            val res = repository.submitReport(reportedUserId, reason, details)
            if (res.isSuccess) {
                _userFeedback.value = "Thank you. Report received for admin review."
                _showReportDialog.value = null
            } else {
                _userFeedback.value = "Failed to submit report."
            }
        }
    }

    fun submitVerification(documentType: String, documentNumber: String) {
        viewModelScope.launch {
            val res = repository.submitVerificationRequest(documentType, documentNumber)
            if (res.isSuccess) {
                _userFeedback.value = "Verification submitted! Admin will review within 24-48 hours."
                _showVerificationDialog.value = false
            } else {
                _userFeedback.value = "Verification submission failed."
            }
        }
    }

    // Admin Actions
    fun setSuspension(userId: String, isSuspended: Boolean) {
        viewModelScope.launch {
            val res = repository.setUserSuspension(userId, isSuspended)
            if (res.isSuccess) {
                _userFeedback.value = if (isSuspended) "User suspended." else "User restored."
            }
        }
    }

    fun reviewVerification(requestId: String, userId: String, approved: Boolean, notes: String) {
        viewModelScope.launch {
            val status = if (approved) "verified" else "rejected"
            val res = repository.reviewVerification(requestId, userId, status, notes)
            if (res.isSuccess) {
                _userFeedback.value = "Verification marked as $status."
            }
        }
    }

    fun adminSetUserVerification(userId: String, status: String) {
        viewModelScope.launch {
            val res = repository.setUserVerification(userId, status)
            if (res.isSuccess) {
                _userFeedback.value = "User verification updated to $status."
            } else {
                _userFeedback.value = "Failed to update verification: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun adminDeleteUser(userId: String) {
        viewModelScope.launch {
            val res = repository.deleteUserAccount(userId)
            if (res.isSuccess) {
                _userFeedback.value = "User profile permanently deleted from platform."
            } else {
                _userFeedback.value = "Failed to delete user: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    // ==========================================
    // FAMILY FEATURE ACTIONS
    // ==========================================

    fun openFamilyChat(chat: FamilyChat) {
        _activeFamilyChat.value = chat
    }

    fun closeFamilyChat() {
        _activeFamilyChat.value = null
    }

    fun observeFamilyChatMessages(chatId: String) = repository.observeFamilyChatMessages(chatId)

    fun sendFamilyChatMessage(chatId: String, text: String, senderRelationship: String = "Self") {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendFamilyChatMessage(chatId, text.trim(), senderRelationship)
        }
    }

    fun sendFamilyInvitation(
        email: String,
        relationship: String,
        permissions: List<String> = listOf("view_matches", "family_chat"),
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        if (email.isBlank()) {
            onComplete(false, "Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            val res = repository.sendFamilyInvitation(email, relationship, permissions)
            if (res.isSuccess) {
                val token = res.getOrNull() ?: ""
                _userFeedback.value = "Family invitation link ready for $email!"
                onComplete(true, token)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Could not create family invitation."
                _userFeedback.value = err
                onComplete(false, err)
            }
        }
    }

    fun revokeFamilyMembership(membershipId: String) {
        viewModelScope.launch {
            val res = repository.revokeFamilyMembership(membershipId)
            if (res.isSuccess) {
                _userFeedback.value = "Family member permissions revoked."
            } else {
                _userFeedback.value = "Failed to revoke permissions."
            }
        }
    }

    fun revokeFamilyInvitation(invitationId: String) {
        viewModelScope.launch {
            val res = repository.revokeFamilyInvitation(invitationId)
            if (res.isSuccess) {
                _userFeedback.value = "Invitation cancelled."
            }
        }
    }

    fun acceptFamilyInvitation(
        codeOrId: String,
        memberName: String = "Family Member",
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val clean = codeOrId.trim()
        if (clean.isBlank()) {
            onComplete(false, "Please enter an invitation code.")
            return
        }
        viewModelScope.launch {
            val res = repository.acceptFamilyInvitation(clean, memberName)
            if (res.isSuccess) {
                _userFeedback.value = "Family invitation accepted! Welcome to the family circle."
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Could not join family circle. Please check the code."
                _userFeedback.value = err
                onComplete(false, err)
            }
        }
    }

    fun sendFamilyConsentRequest(targetUserId: String, targetName: String) {
        viewModelScope.launch {
            val res = repository.sendFamilyConsentRequest(targetUserId, targetName)
            if (res.isSuccess) {
                _userFeedback.value = "Family chat consent requested from $targetName's family!"
            } else {
                _userFeedback.value = "Failed to request family chat: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun respondToFamilyConsent(requestId: String, approved: Boolean) {
        viewModelScope.launch {
            val res = repository.respondToFamilyConsent(requestId, approved)
            if (res.isSuccess) {
                _userFeedback.value = if (approved) "Family chat room created by two-sided consent!" else "Family consent request declined."
            } else {
                _userFeedback.value = "Failed to update consent request."
            }
        }
    }

    // Matchmaker (Ghotok) operations
    fun saveManagedProfile(
        profile: ManagedProfile,
        onSuccess: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val res = repository.saveManagedProfile(profile)
            if (res.isSuccess) {
                _userFeedback.value = "Profile registered! Secure consent token generated for profile owner."
                onSuccess(res.getOrNull() ?: "")
            } else {
                _userFeedback.value = "Failed to save profile: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun respondToProfileOwnerConsent(requestId: String, approved: Boolean) {
        viewModelScope.launch {
            val res = repository.respondToProfileOwnerConsent(requestId, approved, ownerUserId = currentUserId)
            if (res.isSuccess) {
                _userFeedback.value = if (approved) "Consent granted! Matchmaker authorized to represent profile." else "Matchmaker request declined."
            } else {
                _userFeedback.value = "Error updating consent: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun claimMatchmakerByToken(token: String, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val res = repository.claimManagementRequestByToken(token, ownerUserId = currentUserId)
            if (res.isSuccess) {
                _userFeedback.value = "Consent verified! Matchmaker is now authorized to represent your profile."
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Verification code not found."
                _userFeedback.value = err
                onComplete(false, err)
            }
        }
    }

    fun revokeMatchmakerAccess(managedProfileId: String, matchmakerId: String) {
        viewModelScope.launch {
            val res = repository.revokeMatchmakerAccess(managedProfileId, matchmakerId)
            if (res.isSuccess) {
                _userFeedback.value = "Matchmaker access revoked successfully."
            } else {
                _userFeedback.value = "Failed to revoke access: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun submitMatchSuggestion(
        profileAId: String,
        profileAName: String,
        profileAOwnerId: String,
        profileBId: String,
        profileBName: String,
        profileBOwnerId: String,
        notes: String
    ) {
        viewModelScope.launch {
            val matchmakerName = myMatchmakerProfile.value?.fullName ?: "Verified Matchmaker"
            val res = repository.submitMatchSuggestion(
                matchmakerId = currentUserId,
                matchmakerName = matchmakerName,
                profileAId = profileAId,
                profileAName = profileAName,
                profileAOwnerId = profileAOwnerId,
                profileBId = profileBId,
                profileBName = profileBName,
                profileBOwnerId = profileBOwnerId,
                notes = notes
            )
            if (res.isSuccess) {
                _userFeedback.value = "Match suggestion submitted to candidate parties!"
            } else {
                _userFeedback.value = "Failed to submit suggestion: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun adminVerifyMatchmaker(matchmakerId: String, status: String) {
        viewModelScope.launch {
            val res = repository.adminVerifyMatchmaker(matchmakerId, status)
            if (res.isSuccess) {
                _userFeedback.value = "Matchmaker status set to $status."
            } else {
                _userFeedback.value = "Failed to update matchmaker: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun adminSetMatchmakerSuspension(matchmakerId: String, isSuspended: Boolean) {
        viewModelScope.launch {
            val res = repository.adminSetMatchmakerSuspension(matchmakerId, isSuspended)
            if (res.isSuccess) {
                _userFeedback.value = if (isSuspended) "Matchmaker suspended." else "Matchmaker restored."
            } else {
                _userFeedback.value = "Failed to change suspension."
            }
        }
    }

    fun adminUpdatePlanLimit(matchmakerId: String, newLimit: Int) {
        viewModelScope.launch {
            val res = repository.adminUpdatePlanLimit(matchmakerId, newLimit)
            if (res.isSuccess) {
                _userFeedback.value = "Free Launch Plan quota set to $newLimit profiles."
            } else {
                _userFeedback.value = "Failed to update plan quota."
            }
        }
    }
}
