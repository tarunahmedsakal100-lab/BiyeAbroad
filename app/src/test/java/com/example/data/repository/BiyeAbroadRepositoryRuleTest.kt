package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BiyeAbroadRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveUserProfile_authenticatedUser_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = BiyeAbroadRepository(firestore)

        val profile = UserProfile(
            userId = uid,
            displayName = "Alice Khan",
            email = ALICE_EMAIL,
            age = 25,
            country = "United Kingdom",
            city = "London",
            profession = "Architect"
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(profile)
        }
        assertTrue(result.isSuccess)

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserProfile(uid).first()
        }
        assertNotNull(observed)
        assertEquals("Alice Khan", observed?.displayName)
    }

    @Test
    fun sendInterest_authenticatedUser_createsDocument() = runBlocking {
        val senderUid = signInTestUser(ALICE_EMAIL)
        val bobUid = signInTestUser(BOB_EMAIL)
        // sign back as Alice
        signInTestUser(ALICE_EMAIL)
        val repository = BiyeAbroadRepository(firestore)

        val sendResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.sendInterest(bobUid)
        }
        assertTrue(sendResult.isSuccess)
    }

    @Test
    fun observeUserProfile_unauthenticatedUser_failsWhenWriting() = runBlocking {
        auth.signOut()
        val repository = BiyeAbroadRepository(firestore)
        val profile = UserProfile(
            userId = "unauth",
            displayName = "Ghost"
        )
        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(profile)
        }
        assertTrue(result.isFailure)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@biyeabroad.com"
        const val BOB_EMAIL = "bob@biyeabroad.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
