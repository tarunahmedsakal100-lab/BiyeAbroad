package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Conversation
import com.example.data.model.FamilyChat
import com.example.data.model.FamilyChatMessage
import com.example.data.model.FamilyConsentRequest
import com.example.data.model.FamilyInvitation
import com.example.data.model.FamilyMembership
import com.example.data.model.Interest
import com.example.data.model.ManagedProfile
import com.example.data.model.MatchmakerAuditLog
import com.example.data.model.MatchmakerPlan
import com.example.data.model.MatchmakerProfile
import com.example.data.model.MatchmakerSubscription
import com.example.data.model.MatchSuggestion
import com.example.data.model.Message
import com.example.data.model.ProfileManagementRequest
import com.example.data.model.Report
import com.example.data.model.UserProfile
import com.example.data.model.VerificationRequest
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class BiyeAbroadRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    // Observe user profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { it.toObject(UserProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                throw e
            }
    }

    // Save or update profile
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val uid = (auth.currentUser?.uid?.takeIf { it.isNotBlank() } ?: profile.userId.takeIf { it.isNotBlank() })
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        if (profile.photoUrl.trim().startsWith("data:", ignoreCase = true) ||
            profile.photoUrls.any { it.trim().startsWith("data:", ignoreCase = true) }
        ) {
            return Result.failure(IllegalArgumentException("Base64 image data cannot be stored in Firestore."))
        }
        val path = "users/$uid"
        return try {
            val userDoc = db.collection("users").document(uid)
            val snapshot = try { userDoc.get().await() } catch (_: Exception) { null }
            val isNew = snapshot == null || !snapshot.exists()

            val safeDisplayName = profile.resolvedName().ifBlank { "Member" }
            val safeEmail = (auth.currentUser?.email ?: profile.email).ifBlank { "member@biyeabroad.com" }

            val data = mutableMapOf<String, Any>(
                "userId" to uid,
                "firstName" to profile.firstName,
                "lastName" to profile.lastName,
                "displayName" to safeDisplayName,
                "email" to safeEmail,
                "address" to profile.address,
                "age" to profile.resolvedAge(),
                "dateOfBirth" to profile.dateOfBirth,
                "gender" to profile.gender,
                "country" to profile.country,
                "city" to profile.city,
                "countryOfOrigin" to profile.countryOfOrigin,
                "nationality" to profile.nationality,
                "languages" to profile.languages,
                "religion" to profile.religion,
                "religiousDenomination" to profile.religiousDenomination,
                "openToDifferentReligions" to profile.openToDifferentReligions,
                "education" to profile.education,
                "profession" to profile.profession,
                "maritalStatus" to profile.maritalStatus,
                "height" to profile.height,
                "bio" to profile.bio,
                "interests" to profile.interests,
                "partnerPreferences" to profile.partnerPreferences,
                "familyInvolvementPreference" to profile.familyInvolvementPreference,
                "photoUrl" to profile.photoUrl,
                "photoUrls" to profile.photoUrls,
                "photoPublicId" to profile.photoPublicId,
                "verificationStatus" to profile.verificationStatus,
                "membershipTier" to profile.membershipTier,
                "isSuspended" to profile.isSuspended,
                "role" to profile.role,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (isNew) {
                data["createdAt"] = FieldValue.serverTimestamp()
            }

            userDoc.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // Update only profile photo fields (secure_url + public_id) in Firestore
    suspend fun updateUserProfilePhoto(
        userId: String,
        secureUrl: String,
        publicId: String,
        fallbackProfile: UserProfile? = null
    ): Result<Unit> {
        val cleanUrl = secureUrl.trim()
        val cleanPublicId = publicId.trim()
        if (cleanUrl.isBlank() || !cleanUrl.startsWith("https://")) {
            return Result.failure(IllegalArgumentException("A valid HTTPS Cloudinary URL is required."))
        }
        if (cleanUrl.startsWith("data:", ignoreCase = true)) {
            return Result.failure(IllegalArgumentException("Base64 image data cannot be stored in Firestore."))
        }
        if (cleanPublicId.isBlank()) {
            return Result.failure(IllegalArgumentException("A valid Cloudinary public_id is required."))
        }

        val uid = auth.currentUser?.uid?.takeIf { it.isNotBlank() } ?: userId
        if (uid.isBlank()) {
            return Result.failure(IllegalStateException("User not authenticated"))
        }

        val path = "users/$uid"
        return try {
            val userDoc = db.collection("users").document(uid)
            val snapshot = try { userDoc.get().await() } catch (_: Exception) { null }
            if (snapshot != null && snapshot.exists()) {
                userDoc.update(
                    mapOf(
                        "photoUrl" to cleanUrl,
                        "photoUrls" to listOf(cleanUrl),
                        "photoPublicId" to cleanPublicId,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val base = (fallbackProfile ?: UserProfile(
                    userId = uid,
                    email = auth.currentUser?.email ?: "member@biyeabroad.com",
                    displayName = auth.currentUser?.displayName ?: "Member"
                )).copy(
                    userId = uid,
                    photoUrl = cleanUrl,
                    photoUrls = listOf(cleanUrl),
                    photoPublicId = cleanPublicId
                )
                return saveUserProfile(base)
            }

            // Keep matchmaker document photo in sync if user is also a matchmaker
            try {
                val mmDoc = db.collection("matchmakers").document(uid)
                val mmSnap = mmDoc.get().await()
                if (mmSnap.exists()) {
                    mmDoc.update(
                        mapOf(
                            "photoUrl" to cleanUrl,
                            "photoPublicId" to cleanPublicId,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            } catch (_: Exception) {
                // Non-fatal if user has no matchmaker document
            }

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // Observe all members for discovery
    fun observeAllUsers(): Flow<List<UserProfile>> {
        val path = "users"
        return db.collection("users")
            .snapshots()
            .map { snap ->
                snap.toObjects(UserProfile::class.java)
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    // Send Interest
    suspend fun sendInterest(receiverId: String): Result<String> {
        return try {
            val senderId = requireUserId()
            val interestId = UUID.randomUUID().toString()
            val path = "interests/$interestId"
            val payload = mapOf(
                "interestId" to interestId,
                "senderId" to senderId,
                "receiverId" to receiverId,
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("interests").document(interestId).set(payload).await()
            Result.success(interestId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "interests")
            Result.failure(e)
        }
    }

    // Observe Interests sent by current user
    fun observeSentInterests(userId: String): Flow<List<Interest>> {
        val path = "interests"
        return db.collection("interests")
            .whereEqualTo("senderId", userId)
            .snapshots()
            .map { it.toObjects(Interest::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    // Observe Interests received by current user
    fun observeReceivedInterests(userId: String): Flow<List<Interest>> {
        val path = "interests"
        return db.collection("interests")
            .whereEqualTo("receiverId", userId)
            .snapshots()
            .map { it.toObjects(Interest::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    // Update Interest status (Accept/Decline)
    suspend fun updateInterestStatus(interest: Interest, newStatus: String): Result<Unit> {
        val path = "interests/${interest.interestId}"
        return try {
            val doc = db.collection("interests").document(interest.interestId)
            doc.update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // If accepted, also ensure conversation exists!
            if (newStatus == "accepted") {
                val convId = listOf(interest.senderId, interest.receiverId).sorted().joinToString("_")
                db.collection("conversations").document(convId).set(
                    mapOf(
                        "conversationId" to convId,
                        "members" to listOf(interest.senderId, interest.receiverId),
                        "lastMessage" to "Interest accepted! Say Hello.",
                        "lastSenderId" to interest.receiverId,
                        "updatedAt" to FieldValue.serverTimestamp()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // Observe conversations
    fun observeConversations(userId: String): Flow<List<Conversation>> {
        val path = "conversations"
        return db.collection("conversations")
            .whereArrayContains("members", userId)
            .snapshots()
            .map { it.toObjects(Conversation::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    // Observe messages in conversation
    fun observeMessages(conversationId: String): Flow<List<Message>> {
        val path = "conversations/$conversationId/messages"
        return db.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("createdAt")
            .snapshots()
            .map { it.toObjects(Message::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    // Send a message
    suspend fun sendMessage(conversationId: String, text: String): Result<Unit> {
        val uid = requireUserId()
        val msgId = UUID.randomUUID().toString()
        val path = "conversations/$conversationId/messages/$msgId"
        return try {
            val msgData = mapOf(
                "messageId" to msgId,
                "conversationId" to conversationId,
                "senderId" to uid,
                "text" to text,
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("conversations").document(conversationId)
                .collection("messages").document(msgId).set(msgData).await()

            db.collection("conversations").document(conversationId).update(
                mapOf(
                    "lastMessage" to text,
                    "lastSenderId" to uid,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // Submit Report
    suspend fun submitReport(reportedUserId: String, reason: String, details: String): Result<String> {
        val uid = requireUserId()
        val reportId = UUID.randomUUID().toString()
        val path = "reports/$reportId"
        return try {
            val reportData = mapOf(
                "reportId" to reportId,
                "reporterId" to uid,
                "reportedUserId" to reportedUserId,
                "reason" to reason,
                "details" to details,
                "status" to "open",
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("reports").document(reportId).set(reportData).await()
            Result.success(reportId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // Submit Verification Request
    suspend fun submitVerificationRequest(documentType: String, documentNumber: String): Result<String> {
        val uid = requireUserId()
        val reqId = UUID.randomUUID().toString()
        val path = "verificationRequests/$reqId"
        return try {
            val reqData = mapOf(
                "requestId" to reqId,
                "userId" to uid,
                "documentType" to documentType,
                "documentNumber" to documentNumber,
                "status" to "pending",
                "adminNotes" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("verificationRequests").document(reqId).set(reqData).await()
            // Mark user status as pending
            db.collection("users").document(uid).update("verificationStatus", "pending").await()
            Result.success(reqId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // --- Admin Operations ---
    fun observeAllReports(): Flow<List<Report>> {
        val path = "reports"
        return db.collection("reports")
            .snapshots()
            .map { it.toObjects(Report::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    fun observeAllVerificationRequests(): Flow<List<VerificationRequest>> {
        val path = "verificationRequests"
        return db.collection("verificationRequests")
            .snapshots()
            .map { it.toObjects(VerificationRequest::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun reviewVerification(requestId: String, userId: String, status: String, notes: String): Result<Unit> {
        val path = "verificationRequests/$requestId"
        return try {
            db.collection("verificationRequests").document(requestId).update(
                mapOf(
                    "status" to status,
                    "adminNotes" to notes,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            db.collection("users").document(userId).update("verificationStatus", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun setUserSuspension(userId: String, isSuspended: Boolean): Result<Unit> {
        val path = "users/$userId"
        return try {
            db.collection("users").document(userId).update("isSuspended", isSuspended).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun setUserVerification(userId: String, status: String): Result<Unit> {
        val path = "users/$userId"
        return try {
            db.collection("users").document(userId).update("verificationStatus", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteUserAccount(userId: String): Result<Unit> {
        val path = "users/$userId"
        return try {
            db.collection("users").document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // ==========================================
    // FAMILY INVITATION & MEMBERSHIP OPERATIONS
    // ==========================================

    suspend fun sendFamilyInvitation(
        inviteeEmail: String,
        relationship: String,
        permissions: List<String> = listOf("view_matches", "family_chat")
    ): Result<String> {
        val uid = requireUserId()
        val path = "familyInvitations"
        return try {
            val userDoc = db.collection("users").document(uid).get().await()
            val inviterName = userDoc.getString("displayName") ?: "BiyeAbroad Member"

            val invitationId = UUID.randomUUID().toString()
            val token = "INV-" + UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
            val expiresAt = Timestamp((System.currentTimeMillis() / 1000) + (7 * 24 * 60 * 60), 0) // 7 days

            val payload = mapOf(
                "invitationId" to invitationId,
                "inviterUserId" to uid,
                "inviterName" to inviterName,
                "inviteeEmail" to inviteeEmail.trim().lowercase(),
                "relationship" to relationship,
                "permissions" to permissions,
                "status" to "pending",
                "token" to token,
                "expiresAt" to expiresAt,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.collection("familyInvitations").document(invitationId).set(payload).await()
            Result.success(token)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeFamilyInvitations(primaryUserId: String): Flow<List<FamilyInvitation>> {
        val path = "familyInvitations"
        return db.collection("familyInvitations")
            .whereEqualTo("inviterUserId", primaryUserId)
            .snapshots()
            .map { snap -> snap.toObjects(FamilyInvitation::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    fun observeIncomingFamilyInvitations(inviteeEmail: String): Flow<List<FamilyInvitation>> {
        val cleanEmail = inviteeEmail.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        val path = "familyInvitations"
        return db.collection("familyInvitations")
            .whereEqualTo("inviteeEmail", cleanEmail)
            .snapshots()
            .map { snap -> snap.toObjects(FamilyInvitation::class.java).filter { it.status == "pending" } }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    fun observeFamilyMemberships(primaryUserId: String): Flow<List<FamilyMembership>> {
        val path = "familyMemberships"
        return db.collection("familyMemberships")
            .whereEqualTo("primaryUserId", primaryUserId)
            .snapshots()
            .map { snap -> snap.toObjects(FamilyMembership::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun revokeFamilyMembership(membershipId: String): Result<Unit> {
        val path = "familyMemberships/$membershipId"
        return try {
            db.collection("familyMemberships").document(membershipId).update(
                mapOf(
                    "status" to "revoked",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun revokeFamilyInvitation(invitationId: String): Result<Unit> {
        val path = "familyInvitations/$invitationId"
        return try {
            db.collection("familyInvitations").document(invitationId).update(
                mapOf(
                    "status" to "revoked",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun acceptFamilyInvitation(
        invitationId: String,
        familyMemberName: String
    ): Result<Unit> {
        val familyMemberUid = requireUserId()
        val familyMemberEmail = getCurrentUserEmail() ?: ""
        val path = "familyInvitations/$invitationId"

        return try {
            val cleanCode = invitationId.trim()
            var targetDoc = db.collection("familyInvitations").document(cleanCode).get().await()
            if (!targetDoc.exists()) {
                val upperCode = cleanCode.uppercase()
                val tokenQuery = db.collection("familyInvitations")
                    .whereEqualTo("token", upperCode)
                    .limit(1)
                    .get()
                    .await()
                if (!tokenQuery.isEmpty) {
                    targetDoc = tokenQuery.documents[0]
                } else {
                    val exactTokenQuery = db.collection("familyInvitations")
                        .whereEqualTo("token", cleanCode)
                        .limit(1)
                        .get()
                        .await()
                    if (!exactTokenQuery.isEmpty) {
                        targetDoc = exactTokenQuery.documents[0]
                    } else {
                        return Result.failure(IllegalArgumentException("Invalid invitation code or link. Please verify the code."))
                    }
                }
            }
            val invDocRef = targetDoc.reference
            val invDoc = targetDoc
            val resolvedInvitationId = targetDoc.id

            val status = invDoc.getString("status") ?: ""
            if (status != "pending") {
                return Result.failure(IllegalStateException("Invitation is already $status."))
            }

            val primaryUserId = invDoc.getString("inviterUserId") ?: ""
            val relationship = invDoc.getString("relationship") ?: "Guardian"
            @Suppress("UNCHECKED_CAST")
            val permissions = (invDoc.get("permissions") as? List<String>) ?: listOf("view_matches", "family_chat")

            // Update invitation
            invDocRef.update(
                mapOf(
                    "status" to "accepted",
                    "acceptedByUid" to familyMemberUid,
                    "acceptedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // Create membership
            val membershipId = "${primaryUserId}_${familyMemberUid}"
            val membershipPayload = mapOf(
                "membershipId" to membershipId,
                "primaryUserId" to primaryUserId,
                "familyMemberUid" to familyMemberUid,
                "familyMemberEmail" to familyMemberEmail,
                "familyMemberName" to familyMemberName.ifBlank { "Family Member" },
                "relationship" to relationship,
                "role" to "family_member",
                "permissions" to permissions,
                "status" to "active",
                "invitationId" to resolvedInvitationId,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("familyMemberships").document(membershipId).set(membershipPayload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // ==========================================
    // TWO-SIDED FAMILY CONSENT REQUESTS
    // ==========================================

    suspend fun sendFamilyConsentRequest(targetUserId: String, targetName: String): Result<String> {
        val requesterUserId = requireUserId()
        val requestId = "${requesterUserId}_${targetUserId}"
        val path = "familyConsentRequests/$requestId"

        return try {
            val userDoc = db.collection("users").document(requesterUserId).get().await()
            val requesterName = userDoc.getString("displayName") ?: "BiyeAbroad Member"

            val payload = mapOf(
                "requestId" to requestId,
                "requesterUserId" to requesterUserId,
                "requesterName" to requesterName,
                "targetUserId" to targetUserId,
                "targetName" to targetName,
                "requesterConsented" to true,
                "targetConsented" to false,
                "status" to "pending",
                "familyChatId" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.collection("familyConsentRequests").document(requestId).set(payload).await()
            Result.success(requestId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeFamilyConsentRequests(userId: String): Flow<List<FamilyConsentRequest>> {
        val path = "familyConsentRequests"
        // Observe requests where current user is requester
        return db.collection("familyConsentRequests")
            .whereEqualTo("targetUserId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(FamilyConsentRequest::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    fun observeSentFamilyConsentRequests(userId: String): Flow<List<FamilyConsentRequest>> {
        val path = "familyConsentRequests"
        return db.collection("familyConsentRequests")
            .whereEqualTo("requesterUserId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(FamilyConsentRequest::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun respondToFamilyConsent(requestId: String, approved: Boolean): Result<Unit> {
        val path = "familyConsentRequests/$requestId"
        return try {
            val requestDocRef = db.collection("familyConsentRequests").document(requestId)
            val requestDoc = requestDocRef.get().await()
            if (!requestDoc.exists()) {
                return Result.failure(IllegalArgumentException("Consent request not found."))
            }

            if (!approved) {
                requestDocRef.update(
                    mapOf(
                        "status" to "declined",
                        "targetConsented" to false,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                return Result.success(Unit)
            }

            // Both parties agree: Create new Family Chat
            val requesterUserId = requestDoc.getString("requesterUserId") ?: ""
            val targetUserId = requestDoc.getString("targetUserId") ?: ""
            val requesterName = requestDoc.getString("requesterName") ?: "Family"
            val targetName = requestDoc.getString("targetName") ?: "Family"

            // Gather family members for both parties
            val reqMembers = db.collection("familyMemberships")
                .whereEqualTo("primaryUserId", requesterUserId)
                .whereEqualTo("status", "active")
                .get().await()
                .documents.mapNotNull { it.getString("familyMemberUid") }

            val targetMembers = db.collection("familyMemberships")
                .whereEqualTo("primaryUserId", targetUserId)
                .whereEqualTo("status", "active")
                .get().await()
                .documents.mapNotNull { it.getString("familyMemberUid") }

            val allParticipants = (listOf(requesterUserId, targetUserId) + reqMembers + targetMembers).distinct()
            val chatId = UUID.randomUUID().toString()

            val chatPayload = mapOf(
                "chatId" to chatId,
                "primaryUserIds" to listOf(requesterUserId, targetUserId),
                "participantUids" to allParticipants,
                "title" to "Family Discussion: $requesterName & $targetName",
                "status" to "active",
                "consentRequestId" to requestId,
                "lastMessage" to "Family discussion room opened by mutual consent.",
                "lastSenderId" to "system",
                "lastSenderName" to "BiyeAbroad Concierge",
                "lastSenderRelation" to "Verified Matchmaking",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.collection("familyChats").document(chatId).set(chatPayload).await()

            // Update consent request
            requestDocRef.update(
                mapOf(
                    "status" to "approved",
                    "targetConsented" to true,
                    "familyChatId" to chatId,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // ==========================================
    // FAMILY CHATS & MESSAGES
    // ==========================================

    fun observeFamilyChats(userId: String): Flow<List<FamilyChat>> {
        val path = "familyChats"
        return db.collection("familyChats")
            .whereArrayContains("participantUids", userId)
            .snapshots()
            .map { snap -> snap.toObjects(FamilyChat::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    fun observeFamilyChatMessages(chatId: String): Flow<List<FamilyChatMessage>> {
        val path = "familyChats/$chatId/messages"
        return db.collection("familyChats")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt")
            .snapshots()
            .map { snap -> snap.toObjects(FamilyChatMessage::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun sendFamilyChatMessage(
        chatId: String,
        text: String,
        senderRelationship: String = "Self"
    ): Result<String> {
        val uid = requireUserId()
        val path = "familyChats/$chatId/messages"

        return try {
            val userDoc = db.collection("users").document(uid).get().await()
            val senderName = userDoc.getString("displayName") ?: "Family Member"
            val messageId = UUID.randomUUID().toString()

            val msgPayload = mapOf(
                "messageId" to messageId,
                "chatId" to chatId,
                "senderId" to uid,
                "senderName" to senderName,
                "senderRole" to (if (senderRelationship == "Self") "seeker" else "family_member"),
                "senderRelationship" to senderRelationship,
                "text" to text,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("familyChats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(msgPayload)
                .await()

            // Update familyChat summary
            db.collection("familyChats").document(chatId).update(
                mapOf(
                    "lastMessage" to text,
                    "lastSenderId" to uid,
                    "lastSenderName" to senderName,
                    "lastSenderRelation" to senderRelationship,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(messageId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // ==========================================
    // MATCHMAKER (GHOTOK) OPERATIONS
    // ==========================================

    suspend fun registerMatchmaker(profile: MatchmakerProfile): Result<Unit> {
        val uid = profile.matchmakerId.ifBlank { requireUserId() }
        val path = "matchmakers/$uid"
        return try {
            val payload = mapOf(
                "matchmakerId" to uid,
                "fullName" to profile.fullName,
                "photoUrl" to profile.photoUrl,
                "email" to (getCurrentUserEmail() ?: profile.email),
                "phone" to profile.phone,
                "country" to profile.country,
                "city" to profile.city,
                "languages" to profile.languages,
                "experienceYears" to profile.experienceYears,
                "bio" to profile.bio,
                "referenceInfo" to profile.referenceInfo,
                "verificationStatus" to "pending",
                "currentPlanId" to "plan_free_launch",
                "maxManagedProfiles" to 10,
                "managedProfileCount" to 0,
                "termsAccepted" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.collection("matchmakers").document(uid).set(payload, com.google.firebase.firestore.SetOptions.merge()).await()

            // Also record application
            db.collection("matchmakerApplications").document(uid).set(
                mapOf(
                    "matchmakerId" to uid,
                    "fullName" to profile.fullName,
                    "email" to profile.email,
                    "phone" to profile.phone,
                    "status" to "pending",
                    "submittedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // Initialize Free Launch Subscription
            db.collection("matchmakerSubscriptions").document(uid).set(
                mapOf(
                    "subscriptionId" to uid,
                    "matchmakerId" to uid,
                    "planId" to "plan_free_launch",
                    "status" to "active",
                    "profileLimit" to 10,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // Audit Log
            db.collection("matchmakerAuditLogs").document(UUID.randomUUID().toString()).set(
                mapOf(
                    "matchmakerId" to uid,
                    "action" to "REGISTER_MATCHMAKER",
                    "details" to "Registered new Ghotok account with Free Launch plan (10 profile limit).",
                    "timestamp" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeMatchmakerProfile(matchmakerId: String): Flow<MatchmakerProfile?> {
        val path = "matchmakers/$matchmakerId"
        return db.collection("matchmakers").document(matchmakerId)
            .snapshots()
            .map { it.toObject(MatchmakerProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                throw e
            }
    }

    fun observeAllMatchmakers(): Flow<List<MatchmakerProfile>> {
        val path = "matchmakers"
        return db.collection("matchmakers")
            .snapshots()
            .map { snap -> snap.toObjects(MatchmakerProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun saveManagedProfile(profile: ManagedProfile): Result<String> {
        val uid = profile.matchmakerId.ifBlank { requireUserId() }
        val profileId = profile.managedProfileId.ifBlank { UUID.randomUUID().toString() }
        val path = "managedProfiles/$profileId"

        return try {
            val mmDoc = db.collection("matchmakers").document(uid).get().await()
            val maxProfiles = mmDoc.getLong("maxManagedProfiles")?.toInt() ?: 10
            val currentCount = mmDoc.getLong("managedProfileCount")?.toInt() ?: 0

            if (profile.managedProfileId.isBlank() && currentCount >= maxProfiles) {
                return Result.failure(IllegalStateException("Profile limit reached ($maxProfiles). Free launch plan supports up to $maxProfiles managed profiles."))
            }

            val inviteToken = profile.inviteToken.ifBlank {
                "CON-" + UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
            }

            val payload = mapOf(
                "managedProfileId" to profileId,
                "matchmakerId" to uid,
                "matchmakerName" to (profile.matchmakerName.ifBlank { mmDoc.getString("fullName") ?: "Matchmaker" }),
                "profileType" to profile.profileType,
                "fullName" to profile.fullName,
                "dateOfBirth" to profile.dateOfBirth,
                "age" to profile.age,
                "gender" to profile.gender,
                "country" to profile.country,
                "city" to profile.city,
                "nationality" to profile.nationality,
                "education" to profile.education,
                "profession" to profile.profession,
                "maritalStatus" to profile.maritalStatus,
                "height" to profile.height,
                "languages" to profile.languages,
                "religion" to profile.religion,
                "bio" to profile.bio,
                "partnerPreferences" to profile.partnerPreferences,
                "photoUrl" to profile.photoUrl,
                "ownerName" to profile.ownerName,
                "ownerRelationship" to profile.ownerRelationship,
                "ownerEmail" to profile.ownerEmail.trim().lowercase(),
                "ownerPhone" to profile.ownerPhone,
                "ownerUserId" to profile.ownerUserId,
                "consentStatus" to profile.consentStatus,
                "inviteToken" to inviteToken,
                "permissions" to profile.permissions,
                "isPublished" to (profile.consentStatus == "approved"),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            val docRef = db.collection("managedProfiles").document(profileId)
            val isNew = !docRef.get().await().exists()
            docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()

            if (isNew) {
                docRef.update("createdAt", FieldValue.serverTimestamp()).await()
                db.collection("matchmakers").document(uid).set(
                    mapOf(
                        "matchmakerId" to uid,
                        "managedProfileCount" to FieldValue.increment(1),
                        "updatedAt" to FieldValue.serverTimestamp()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()

                // Create Profile Management Consent Request
                val requestId = UUID.randomUUID().toString()
                db.collection("profileManagementRequests").document(requestId).set(
                    mapOf(
                        "requestId" to requestId,
                        "matchmakerId" to uid,
                        "matchmakerName" to (profile.matchmakerName.ifBlank { mmDoc.getString("fullName") ?: "Matchmaker" }),
                        "matchmakerEmail" to (mmDoc.getString("email") ?: getCurrentUserEmail() ?: ""),
                        "managedProfileId" to profileId,
                        "candidateName" to profile.fullName,
                        "candidateEmail" to profile.ownerEmail.trim().lowercase(),
                        "candidatePhone" to profile.ownerPhone,
                        "profileType" to profile.profileType,
                        "inviteToken" to inviteToken,
                        "status" to "pending",
                        "grantedPermissions" to profile.permissions,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            Result.success(inviteToken)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeManagedProfiles(matchmakerId: String): Flow<List<ManagedProfile>> {
        val path = "managedProfiles"
        return db.collection("managedProfiles")
            .whereEqualTo("matchmakerId", matchmakerId)
            .snapshots()
            .map { snap -> snap.toObjects(ManagedProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    fun observeApprovedManagedProfiles(): Flow<List<ManagedProfile>> {
        val path = "managedProfiles"
        return db.collection("managedProfiles")
            .whereEqualTo("isPublished", true)
            .snapshots()
            .map { snap -> snap.toObjects(ManagedProfile::class.java).filter { it.consentStatus == "approved" } }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    fun observeOwnerConsentRequestsByEmail(ownerEmail: String): Flow<List<ProfileManagementRequest>> {
        val cleanEmail = ownerEmail.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        val path = "profileManagementRequests"
        return db.collection("profileManagementRequests")
            .whereEqualTo("candidateEmail", cleanEmail)
            .snapshots()
            .map { snap -> snap.toObjects(ProfileManagementRequest::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    fun observeOwnerConsentRequestsByUid(ownerUserId: String): Flow<List<ProfileManagementRequest>> {
        if (ownerUserId.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        val path = "profileManagementRequests"
        return db.collection("profileManagementRequests")
            .whereEqualTo("ownerUserId", ownerUserId)
            .snapshots()
            .map { snap -> snap.toObjects(ProfileManagementRequest::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    suspend fun claimManagementRequestByToken(token: String, ownerUserId: String): Result<Unit> {
        val cleanToken = token.trim()
        if (cleanToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter a verification code."))
        }
        return try {
            var query = db.collection("profileManagementRequests")
                .whereEqualTo("inviteToken", cleanToken)
                .limit(1)
                .get()
                .await()
            if (query.isEmpty) {
                query = db.collection("profileManagementRequests")
                    .whereEqualTo("inviteToken", cleanToken.uppercase())
                    .limit(1)
                    .get()
                    .await()
            }
            if (query.isEmpty) {
                return Result.failure(IllegalArgumentException("Verification code not found. Please check the code from your matchmaker."))
            }
            val doc = query.documents[0]
            respondToProfileOwnerConsent(doc.id, approved = true, ownerUserId = ownerUserId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "profileManagementRequests")
            Result.failure(e)
        }
    }

    suspend fun respondToProfileOwnerConsent(
        requestId: String,
        approved: Boolean,
        ownerUserId: String,
        permissions: List<String> = listOf("view_approved_info", "view_matchmaking_status", "receive_notifications")
    ): Result<Unit> {
        val path = "profileManagementRequests/$requestId"
        return try {
            val reqDocRef = db.collection("profileManagementRequests").document(requestId)
            val reqDoc = reqDocRef.get().await()
            if (!reqDoc.exists()) {
                return Result.failure(IllegalArgumentException("Management request not found."))
            }

            val managedProfileId = reqDoc.getString("managedProfileId") ?: ""
            val matchmakerId = reqDoc.getString("matchmakerId") ?: ""

            if (approved) {
                reqDocRef.update(
                    mapOf(
                        "status" to "accepted",
                        "ownerUserId" to ownerUserId,
                        "grantedPermissions" to permissions,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()

                db.collection("managedProfiles").document(managedProfileId).update(
                    mapOf(
                        "consentStatus" to "approved",
                        "isPublished" to true,
                        "ownerUserId" to ownerUserId,
                        "permissions" to permissions,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()

                db.collection("matchmakerAuditLogs").document(UUID.randomUUID().toString()).set(
                    mapOf(
                        "matchmakerId" to matchmakerId,
                        "action" to "OWNER_CONSENT_GRANTED",
                        "targetProfileId" to managedProfileId,
                        "details" to "Profile owner $ownerUserId approved draft biodata and granted permissions: $permissions",
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                reqDocRef.update(
                    mapOf(
                        "status" to "declined",
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()

                db.collection("managedProfiles").document(managedProfileId).update(
                    mapOf(
                        "consentStatus" to "rejected",
                        "isPublished" to false,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun revokeMatchmakerAccess(managedProfileId: String, ownerUserId: String): Result<Unit> {
        val path = "managedProfiles/$managedProfileId"
        return try {
            val docRef = db.collection("managedProfiles").document(managedProfileId)
            val doc = docRef.get().await()
            val matchmakerId = doc.getString("matchmakerId") ?: ""

            docRef.update(
                mapOf(
                    "consentStatus" to "revoked",
                    "isPublished" to false,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            db.collection("matchmakerAuditLogs").document(UUID.randomUUID().toString()).set(
                mapOf(
                    "matchmakerId" to matchmakerId,
                    "action" to "ACCESS_REVOKED",
                    "targetProfileId" to managedProfileId,
                    "details" to "Profile owner $ownerUserId revoked matchmaker management access.",
                    "timestamp" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun submitMatchSuggestion(
        matchmakerId: String,
        matchmakerName: String,
        profileAId: String,
        profileAName: String,
        profileAOwnerId: String,
        profileBId: String,
        profileBName: String,
        profileBOwnerId: String,
        notes: String
    ): Result<String> {
        val suggestionId = UUID.randomUUID().toString()
        val path = "matchSuggestions/$suggestionId"
        return try {
            val payload = mapOf(
                "suggestionId" to suggestionId,
                "matchmakerId" to matchmakerId,
                "matchmakerName" to matchmakerName,
                "profileAId" to profileAId,
                "profileAName" to profileAName,
                "profileAOwnerId" to profileAOwnerId,
                "profileBId" to profileBId,
                "profileBName" to profileBName,
                "profileBOwnerId" to profileBOwnerId,
                "notes" to notes,
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("matchSuggestions").document(suggestionId).set(payload).await()
            Result.success(suggestionId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeMatchSuggestions(matchmakerId: String): Flow<List<MatchSuggestion>> {
        val path = "matchSuggestions"
        return db.collection("matchSuggestions")
            .whereEqualTo("matchmakerId", matchmakerId)
            .snapshots()
            .map { snap -> snap.toObjects(MatchSuggestion::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun adminVerifyMatchmaker(matchmakerId: String, status: String): Result<Unit> {
        val path = "matchmakers/$matchmakerId"
        return try {
            db.collection("matchmakers").document(matchmakerId).update(
                mapOf(
                    "verificationStatus" to status,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            db.collection("matchmakerApplications").document(matchmakerId).update(
                mapOf(
                    "status" to status,
                    "reviewedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminSetMatchmakerSuspension(matchmakerId: String, isSuspended: Boolean): Result<Unit> {
        val path = "matchmakers/$matchmakerId"
        val status = if (isSuspended) "suspended" else "verified"
        return try {
            db.collection("matchmakers").document(matchmakerId).update(
                mapOf(
                    "verificationStatus" to status,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminUpdatePlanLimit(matchmakerId: String, newLimit: Int): Result<Unit> {
        val path = "matchmakers/$matchmakerId"
        return try {
            db.collection("matchmakers").document(matchmakerId).update(
                mapOf(
                    "maxManagedProfiles" to newLimit,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            db.collection("matchmakerSubscriptions").document(matchmakerId).update(
                mapOf(
                    "profileLimit" to newLimit,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    fun observeMatchmakerAuditLogs(): Flow<List<MatchmakerAuditLog>> {
        val path = "matchmakerAuditLogs"
        return db.collection("matchmakerAuditLogs")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .snapshots()
            .map { snap -> snap.toObjects(MatchmakerAuditLog::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }
}
