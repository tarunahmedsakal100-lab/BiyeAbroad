package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val email: String = "",
    val address: String = "", // House, Street, Area / Permanent & Current Address
    val age: Int = 24,
    val dateOfBirth: String = "", // Format: "YYYY-MM-DD" or "DD/MM/YYYY"
    val gender: String = "male", // "male", "female"
    val country: String = "United Kingdom",
    val city: String = "London",
    val countryOfOrigin: String = "Bangladesh",
    val nationality: String = "British",
    val languages: List<String> = listOf("Bengali", "English"),
    val religion: String = "", // Non-religious base; customized by users freely
    val religiousDenomination: String = "",
    val openToDifferentReligions: Boolean = true,
    val education: String = "MSc in Computer Science",
    val profession: String = "Software Engineer",
    val maritalStatus: String = "Never Married",
    val height: String = "5'5\"",
    val bio: String = "Dedicated, kind, and family-oriented professional looking for a life partner seeking marriage.",
    val interests: List<String> = listOf("Halal Travel", "Reading", "Tech", "Cooking"),
    val partnerPreferences: String = "Seeking an educated, kind, and family-oriented life partner living abroad or in Bangladesh.",
    val familyInvolvementPreference: String = "Joint decision (Self + Family)",
    val photoUrl: String = "",
    val photoUrls: List<String> = emptyList(),
    val photoPublicId: String = "",
    val verificationStatus: String = "unverified", // "unverified", "pending", "verified", "rejected"
    val membershipTier: String = "free", // "free", "silver", "gold", "platinum"
    val isSuspended: Boolean = false,
    val role: String = "user", // "user", "admin"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    /**
     * Resolves display name preferring First Name + Last Name when available.
     */
    fun resolvedName(): String {
        return if (firstName.isNotBlank() || lastName.isNotBlank()) {
            "$firstName $lastName".trim()
        } else if (displayName.isNotBlank()) {
            displayName
        } else {
            "Member"
        }
    }

    /**
     * Resolves the actual age, dynamically calculating from dateOfBirth if available,
     * otherwise falls back to the stored age.
     */
    fun resolvedAge(): Int {
        if (dateOfBirth.isBlank()) return age
        return try {
            val parts = dateOfBirth.trim().split("-", "/", ".")
            if (parts.size == 3) {
                val year = if (parts[0].length == 4) parts[0].toInt() else parts[2].toInt()
                val month = if (parts[0].length == 4) parts[1].toInt() else parts[0].toInt()
                val day = if (parts[0].length == 4) parts[2].toInt() else parts[1].toInt()
                val birthDate = java.time.LocalDate.of(year, month.coerceIn(1, 12), day.coerceIn(1, 28))
                val now = java.time.LocalDate.now()
                java.time.Period.between(birthDate, now).years.coerceAtLeast(18)
            } else {
                age
            }
        } catch (_: Exception) {
            age
        }
    }
}

@IgnoreExtraProperties
data class Interest(
    @DocumentId val id: String = "",
    val interestId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val status: String = "pending", // "pending", "accepted", "declined"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Conversation(
    @DocumentId val id: String = "",
    val conversationId: String = "",
    val members: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastSenderId: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Message(
    @DocumentId val id: String = "",
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Report(
    @DocumentId val id: String = "",
    val reportId: String = "",
    val reporterId: String = "",
    val reportedUserId: String = "",
    val reason: String = "",
    val details: String = "",
    val status: String = "open", // "open", "reviewed", "resolved"
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class VerificationRequest(
    @DocumentId val id: String = "",
    val requestId: String = "",
    val userId: String = "",
    val documentType: String = "passport", // "passport", "national_id", "driving_license"
    val documentNumber: String = "",
    val status: String = "pending", // "pending", "approved", "rejected"
    val adminNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FamilyInvitation(
    @DocumentId val id: String = "",
    val invitationId: String = "",
    val inviterUserId: String = "",
    val inviterName: String = "",
    val inviteeEmail: String = "",
    val relationship: String = "Guardian", // "Father", "Mother", "Brother", "Sister", "Uncle", "Aunt", "Guardian"
    val permissions: List<String> = listOf("view_matches", "family_chat"),
    val status: String = "pending", // "pending", "accepted", "declined", "expired", "revoked"
    val token: String = "",
    val expiresAt: Timestamp? = null,
    val acceptedByUid: String = "",
    val acceptedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FamilyMembership(
    @DocumentId val id: String = "",
    val membershipId: String = "",
    val primaryUserId: String = "",
    val familyMemberUid: String = "",
    val familyMemberEmail: String = "",
    val familyMemberName: String = "",
    val relationship: String = "Guardian",
    val role: String = "family_member",
    val permissions: List<String> = listOf("view_matches", "family_chat"),
    val status: String = "active", // "active", "revoked"
    val invitationId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FamilyConsentRequest(
    @DocumentId val id: String = "",
    val requestId: String = "",
    val requesterUserId: String = "",
    val requesterName: String = "",
    val targetUserId: String = "",
    val targetName: String = "",
    val requesterConsented: Boolean = true,
    val targetConsented: Boolean = false,
    val status: String = "pending", // "pending", "approved", "declined", "cancelled"
    val familyChatId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FamilyChat(
    @DocumentId val id: String = "",
    val chatId: String = "",
    val primaryUserIds: List<String> = emptyList(),
    val participantUids: List<String> = emptyList(),
    val title: String = "",
    val status: String = "active", // "active", "archived"
    val consentRequestId: String = "",
    val lastMessage: String = "",
    val lastSenderId: String = "",
    val lastSenderName: String = "",
    val lastSenderRelation: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FamilyChatMessage(
    @DocumentId val id: String = "",
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "seeker", // "seeker", "family_member"
    val senderRelationship: String = "Self", // "Self", "Father", "Mother", etc.
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

// ==========================================
// MATCHMAKER (GHOTOK) SYSTEM MODELS
// ==========================================

@IgnoreExtraProperties
data class MatchmakerProfile(
    @DocumentId val id: String = "",
    val matchmakerId: String = "",
    val fullName: String = "",
    val photoUrl: String = "",
    val email: String = "",
    val phone: String = "",
    val country: String = "Bangladesh",
    val city: String = "Dhaka",
    val languages: List<String> = listOf("Bengali", "English"),
    val experienceYears: Int = 5,
    val bio: String = "",
    val referenceInfo: String = "",
    val verificationStatus: String = "pending", // "pending", "verified", "rejected", "suspended"
    val currentPlanId: String = "plan_free_launch",
    val maxManagedProfiles: Int = 10,
    val managedProfileCount: Int = 0,
    val termsAccepted: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ManagedProfile(
    @DocumentId val id: String = "",
    val managedProfileId: String = "",
    val matchmakerId: String = "",
    val matchmakerName: String = "",
    val profileType: String = "bride", // "bride", "groom"
    val fullName: String = "",
    val dateOfBirth: String = "1998-05-15",
    val age: Int = 26,
    val gender: String = "female", // "female", "male"
    val country: String = "United Kingdom",
    val city: String = "London",
    val nationality: String = "British Bangladeshi",
    val education: String = "BSc in Economics",
    val profession: String = "Financial Analyst",
    val maritalStatus: String = "Never Married",
    val height: String = "5'7\"",
    val languages: List<String> = listOf("Bengali", "English"),
    val religion: String = "",
    val bio: String = "",
    val partnerPreferences: String = "",
    val photoUrl: String = "",
    val ownerName: String = "",
    val ownerRelationship: String = "Parent",
    val ownerEmail: String = "",
    val ownerPhone: String = "",
    val ownerUserId: String = "",
    val consentStatus: String = "pending_consent", // "draft", "pending_consent", "approved", "rejected", "suspended", "revoked"
    val inviteToken: String = "",
    val permissions: List<String> = listOf("view_approved_info", "view_matchmaking_status", "receive_notifications"),
    val isPublished: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ProfileManagementRequest(
    @DocumentId val id: String = "",
    val requestId: String = "",
    val matchmakerId: String = "",
    val matchmakerName: String = "",
    val matchmakerEmail: String = "",
    val managedProfileId: String = "",
    val candidateName: String = "",
    val candidateEmail: String = "",
    val candidatePhone: String = "",
    val profileType: String = "bride",
    val inviteToken: String = "",
    val status: String = "pending", // "pending", "accepted", "declined", "revoked"
    val grantedPermissions: List<String> = listOf("view_approved_info", "view_matchmaking_status", "receive_notifications"),
    val ownerUserId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MatchSuggestion(
    @DocumentId val id: String = "",
    val suggestionId: String = "",
    val matchmakerId: String = "",
    val matchmakerName: String = "",
    val profileAId: String = "",
    val profileAName: String = "",
    val profileAOwnerId: String = "",
    val profileBId: String = "",
    val profileBName: String = "",
    val profileBOwnerId: String = "",
    val notes: String = "",
    val status: String = "pending", // "pending", "accepted_by_owner", "declined_by_owner", "matched"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MatchmakerPlan(
    @DocumentId val id: String = "",
    val planId: String = "free_launch",
    val name: String = "Free Launch Plan",
    val priceText: String = "Free ($0)",
    val maxManagedProfiles: Int = 10,
    val features: List<String> = listOf(
        "Free registration",
        "Matchmaker profile",
        "Up to 10 managed profiles",
        "Profile owner consent management",
        "Basic profile management",
        "Basic match suggestions",
        "Standard support"
    ),
    val isActive: Boolean = true,
    val isComingSoon: Boolean = false,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MatchmakerSubscription(
    @DocumentId val id: String = "",
    val subscriptionId: String = "",
    val matchmakerId: String = "",
    val planId: String = "free_launch",
    val status: String = "active", // "active", "trial", "expired", "suspended"
    val profileLimit: Int = 10,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MatchmakerAuditLog(
    @DocumentId val id: String = "",
    val logId: String = "",
    val matchmakerId: String = "",
    val action: String = "",
    val targetProfileId: String = "",
    val details: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
)
