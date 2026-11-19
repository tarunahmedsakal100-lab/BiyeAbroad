package com.example.data.remote

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserProfile
import com.example.data.repository.BiyeAbroadRepository
import com.example.ui.components.CloudinaryPhotoUploadDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.viewmodel.MainViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CloudinaryUploaderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private var server: HttpServer? = null

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        server?.stop(0)
        server = null
    }

    private fun validJpegBytes(size: Int = 64): ByteArray {
        val bytes = ByteArray(size.coerceAtLeast(16))
        bytes[0] = 0xFF.toByte()
        bytes[1] = 0xD8.toByte()
        bytes[2] = 0xFF.toByte()
        bytes[3] = 0xE0.toByte()
        return bytes
    }

    private fun validPngBytes(size: Int = 64): ByteArray {
        val bytes = ByteArray(size.coerceAtLeast(16))
        bytes[0] = 0x89.toByte()
        bytes[1] = 0x50.toByte()
        bytes[2] = 0x4E.toByte()
        bytes[3] = 0x47.toByte()
        bytes[4] = 0x0D.toByte()
        bytes[5] = 0x0A.toByte()
        bytes[6] = 0x1A.toByte()
        bytes[7] = 0x0A.toByte()
        return bytes
    }

    private fun validWebpBytes(size: Int = 64): ByteArray {
        val bytes = ByteArray(size.coerceAtLeast(16))
        "RIFF".toByteArray().copyInto(bytes, 0)
        "WEBP".toByteArray().copyInto(bytes, 8)
        return bytes
    }

    @Test
    fun cloudinaryConstants_matchRequestedConfig() {
        assertEquals("v0c1td87", CloudinaryUploader.CLOUD_NAME)
        assertEquals("BiyeAbroad", CloudinaryUploader.UPLOAD_PRESET)
        assertEquals(5L * 1024L * 1024L, CloudinaryUploader.MAX_FILE_SIZE_BYTES)
    }

    @Test
    fun detectImageFormatFromBytes_allowsJpgPngWebpAndRejectsOthers() {
        assertEquals("image/jpeg" to "JPEG", CloudinaryUploader.detectImageFormatFromBytes(validJpegBytes()))
        assertEquals("image/png" to "PNG", CloudinaryUploader.detectImageFormatFromBytes(validPngBytes()))
        assertEquals("image/webp" to "WEBP", CloudinaryUploader.detectImageFormatFromBytes(validWebpBytes()))

        val gifBytes = "GIF89a..........".toByteArray()
        assertNull(CloudinaryUploader.detectImageFormatFromBytes(gifBytes))

        val pdfBytes = "%PDF-1.7........".toByteArray()
        assertNull(CloudinaryUploader.detectImageFormatFromBytes(pdfBytes))
    }

    @Test
    fun preparePhotoFromUri_acceptsValidPngUnder5Mb(): Unit = runBlocking {
        val tempFile = File(context.cacheDir, "sample_avatar.png")
        tempFile.writeBytes(validPngBytes(1024))
        try {
            val result = CloudinaryUploader.preparePhotoFromUri(context, Uri.fromFile(tempFile))
            assertTrue(result.isSuccess)
            val prepared = result.getOrThrow()
            assertEquals("PNG", prepared.formatLabel)
            assertEquals("image/png", prepared.mimeType)
            assertEquals(1024L, prepared.sizeBytes)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun preparePhotoFromUri_rejectsFilesOver5Mb(): Unit = runBlocking {
        val tempFile = File(context.cacheDir, "too_big.jpg")
        val oversized = validJpegBytes((5 * 1024 * 1024) + 1024)
        tempFile.writeBytes(oversized)
        try {
            val result = CloudinaryUploader.preparePhotoFromUri(context, Uri.fromFile(tempFile))
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("5 MB") == true)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun preparePhotoFromUri_rejectsUnsupportedGifFormat(): Unit = runBlocking {
        val tempFile = File(context.cacheDir, "animated.gif")
        tempFile.writeBytes("GIF89a..........".toByteArray())
        try {
            val result = CloudinaryUploader.preparePhotoFromUri(context, Uri.fromFile(tempFile))
            assertTrue(result.isFailure)
            val errMsg = result.exceptionOrNull()?.message.orEmpty()
            assertTrue("Unexpected error message: $errMsg", errMsg.contains("JPG, JPEG, PNG, and WEBP"))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun preparePhotoFromBitmap_producesValidJpegUnderLimit() = runBlocking {
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        val result = CloudinaryUploader.preparePhotoFromBitmap(bitmap)
        assertTrue(result.isSuccess)
        val prepared = result.getOrThrow()
        assertEquals("JPEG", prepared.formatLabel)
        assertEquals("image/jpeg", prepared.mimeType)
        assertTrue(prepared.sizeBytes in 1..CloudinaryUploader.MAX_FILE_SIZE_BYTES)
    }

    @Test
    fun uploadPreparedPhoto_sendsUnsignedPresetAndParsesSecureUrlAndPublicId() = runBlocking {
        var capturedBody = ""
        var capturedMethod = ""

        val httpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        httpServer.createContext("/v1_1/v0c1td87/image/upload") { exchange ->
            capturedMethod = exchange.requestMethod
            capturedBody = exchange.requestBody.readBytes().toString(StandardCharsets.ISO_8859_1)
            val responseJson = """
                {
                  "secure_url": "https://res.cloudinary.com/v0c1td87/image/upload/v1731880000/biyeabroad/profile_123.jpg",
                  "public_id": "biyeabroad/profile_123",
                  "format": "jpg",
                  "bytes": 2048
                }
            """.trimIndent()
            val responseBytes = responseJson.toByteArray(StandardCharsets.UTF_8)
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, responseBytes.size.toLong())
            exchange.responseBody.use { it.write(responseBytes) }
        }
        httpServer.start()
        server = httpServer

        val port = httpServer.address.port
        val uploader = CloudinaryUploader(
            cloudName = "v0c1td87",
            uploadPreset = "BiyeAbroad",
            uploadUrl = "http://127.0.0.1:$port/v1_1/v0c1td87/image/upload"
        )

        val prepared = PreparedPhotoUpload(
            bytes = validJpegBytes(2048),
            fileName = "profile.jpg",
            mimeType = "image/jpeg",
            formatLabel = "JPEG",
            sizeBytes = 2048L
        )

        val progressUpdates = mutableListOf<Float>()
        val result = uploader.uploadPreparedPhoto(prepared) { progress ->
            progressUpdates.add(progress)
        }

        assertTrue(result.isSuccess)
        val uploaded = result.getOrThrow()
        assertEquals("POST", capturedMethod)
        assertTrue(capturedBody.contains("name=\"upload_preset\""))
        assertTrue(capturedBody.contains("BiyeAbroad"))
        assertTrue(capturedBody.contains("name=\"file\""))
        assertFalse(capturedBody.contains("api_secret"))
        assertEquals(
            "https://res.cloudinary.com/v0c1td87/image/upload/v1731880000/biyeabroad/profile_123.jpg",
            uploaded.secureUrl
        )
        assertEquals("biyeabroad/profile_123", uploaded.publicId)
        assertTrue(progressUpdates.isNotEmpty())
        assertEquals(1.0f, progressUpdates.last(), 0.001f)
    }

    @Test
    fun uploadPreparedPhoto_surfacesCloudinaryErrorMessageOnHttp400() = runBlocking {
        val httpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        httpServer.createContext("/v1_1/v0c1td87/image/upload") { exchange ->
            val errJson = """{"error":{"message":"Upload preset must be whitelisted for unsigned uploads"}}"""
            val errBytes = errJson.toByteArray(StandardCharsets.UTF_8)
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(400, errBytes.size.toLong())
            exchange.responseBody.use { it.write(errBytes) }
        }
        httpServer.start()
        server = httpServer

        val uploader = CloudinaryUploader(
            uploadUrl = "http://127.0.0.1:${httpServer.address.port}/v1_1/v0c1td87/image/upload"
        )

        val prepared = PreparedPhotoUpload(
            bytes = validWebpBytes(512),
            fileName = "avatar.webp",
            mimeType = "image/webp",
            formatLabel = "WEBP",
            sizeBytes = 512L
        )

        val result = uploader.uploadPreparedPhoto(prepared)
        assertTrue(result.isFailure)
        assertEquals(
            "Upload preset must be whitelisted for unsigned uploads",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun repository_neverStoresBase64ImageDataInFirestore(): Unit = runBlocking {
        val app = if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(
                context,
                FirebaseOptions.Builder()
                    .setApplicationId("com.Tarun.BiyeAbroad")
                    .setProjectId("demo-no-project")
                    .setApiKey("fake-api-key")
                    .build()
            )
        } else {
            FirebaseApp.getInstance()
        }
        val db = FirebaseFirestore.getInstance(app, context.getString(com.example.R.string.firestore_database_id))
        val repository = BiyeAbroadRepository(db)

        val saveRes = repository.saveUserProfile(
            UserProfile(
                userId = "user_1",
                displayName = "Test User",
                email = "test@biyeabroad.com",
                photoUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRg=="
            )
        )
        assertTrue(saveRes.isFailure)
        assertTrue(saveRes.exceptionOrNull()?.message?.contains("Base64") == true)

        val photoRes = repository.updateUserProfilePhoto(
            userId = "user_1",
            secureUrl = "data:image/png;base64,iVBORw0KGgo=",
            publicId = "id_1"
        )
        assertTrue(photoRes.isFailure)
    }

    @Test
    fun cloudinaryPhotoUploadDialog_displaysPreviewProgressErrorAndSuccess() {
        val prepared = PreparedPhotoUpload(
            bytes = validJpegBytes(2048),
            fileName = "avatar.jpg",
            mimeType = "image/jpeg",
            formatLabel = "JPEG",
            sizeBytes = 2048L
        )

        composeTestRule.setContent {
            CloudinaryPhotoUploadDialog(
                prepared = prepared,
                isUploading = true,
                uploadProgress = 0.65f,
                errorMessage = null,
                successMessage = null,
                onConfirmUpload = {},
                onChooseAnother = {},
                onDismiss = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("cloudinary_photo_preview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cloudinary_upload_progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()
        composeTestRule.onNodeWithText("JPEG • 2 KB").assertIsDisplayed()
    }

    @Test
    fun preparePhotoFromUri_readsContentSchemeUriViaContentResolver(): Unit = runBlocking {
        val authority = "com.Tarun.BiyeAbroad.testprovider"
        val imageFile = File(context.cacheDir, "gallery_pick_test.jpg")
        val bmp = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
        ByteArrayOutputStream().use { baos ->
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            imageFile.writeBytes(baos.toByteArray())
        }

        val provider = object : ContentProvider() {
            override fun onCreate(): Boolean = true
            override fun query(
                uri: Uri,
                projection: Array<out String>?,
                selection: String?,
                selectionArgs: Array<out String>?,
                sortOrder: String?
            ): Cursor {
                val cursor = MatrixCursor(arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE))
                cursor.addRow(arrayOf<Any>("picked_from_gallery.jpg", imageFile.length()))
                return cursor
            }
            override fun getType(uri: Uri): String = "image/jpeg"
            override fun insert(uri: Uri, values: ContentValues?): Uri? = null
            override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
            override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
            override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
                return ParcelFileDescriptor.open(imageFile, ParcelFileDescriptor.MODE_READ_ONLY)
            }
        }

        val info = android.content.pm.ProviderInfo().apply { this.authority = authority }
        provider.attachInfo(context, info)
        org.robolectric.shadows.ShadowContentResolver.registerProviderInternal(authority, provider)

        try {
            val contentUri = Uri.parse("content://$authority/images/42")
            assertEquals("content", contentUri.scheme)
            val result = CloudinaryUploader.preparePhotoFromUri(context, contentUri)
            assertTrue("Expected content:// URI to prepare cleanly: ${result.exceptionOrNull()}", result.isSuccess)
            val prepared = result.getOrThrow()
            assertEquals("JPEG", prepared.formatLabel)
            assertEquals("image/jpeg", prepared.mimeType)
            assertEquals("picked_from_gallery.jpg", prepared.fileName)

            val preview = CloudinaryUploader.decodeSampledPreviewBitmap(prepared.bytes)
            assertNotNull(preview)
        } finally {
            imageFile.delete()
        }
    }

    @Test
    fun editProfileDialog_changePhotoOpensGalleryAndFolderSourceDialog() {
        val sampleProfile = UserProfile(
            userId = "member_99",
            firstName = "Nusrat",
            lastName = "Jahan",
            displayName = "Nusrat Jahan",
            email = "nusrat@biyeabroad.com",
            age = 27,
            gender = "female",
            country = "Canada",
            city = "Toronto",
            profession = "Physician"
        )

        composeTestRule.setContent {
            EditProfileDialog(
                profile = sampleProfile,
                onDismiss = {},
                onSave = {},
                onPersistUploadedPhoto = { _, _ -> Result.success(Unit) }
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("btn_choose_gallery_photo").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Select Profile Photo Source").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_browse_device_files_helper").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_pick_gallery_photo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_pick_any_gallery_helper").assertIsDisplayed()
    }

    @Test
    fun mainViewModel_uploadAndSaveProfilePhoto_updatesPhotoWhilePreservingBiodata(): Unit = runBlocking {
        val app = if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(
                context,
                FirebaseOptions.Builder()
                    .setApplicationId("com.Tarun.BiyeAbroad")
                    .setProjectId("demo-no-project")
                    .setApiKey("fake-api-key")
                    .build()
            )
        } else {
            FirebaseApp.getInstance()
        }
        val db = FirebaseFirestore.getInstance(app, context.getString(com.example.R.string.firestore_database_id))
        val repository = BiyeAbroadRepository(db)

        val httpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        httpServer.createContext("/v1_1/v0c1td87/image/upload") { exchange ->
            val responseJson = """
                {
                  "secure_url": "https://res.cloudinary.com/v0c1td87/image/upload/v1731900000/biyeabroad/preserved_bio.png",
                  "public_id": "biyeabroad/preserved_bio",
                  "format": "png",
                  "bytes": 1024
                }
            """.trimIndent()
            val responseBytes = responseJson.toByteArray(StandardCharsets.UTF_8)
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, responseBytes.size.toLong())
            exchange.responseBody.use { it.write(responseBytes) }
        }
        httpServer.start()
        server = httpServer

        val uploader = CloudinaryUploader(
            uploadUrl = "http://127.0.0.1:${httpServer.address.port}/v1_1/v0c1td87/image/upload"
        )
        val viewModel = MainViewModel(
            repository = repository,
            currentUserId = "member_99",
            cloudinaryUploader = uploader
        )

        val initialProfile = UserProfile(
            userId = "member_99",
            firstName = "Nusrat",
            lastName = "Jahan",
            displayName = "Nusrat Jahan",
            email = "nusrat@biyeabroad.com",
            age = 27,
            gender = "female",
            country = "Canada",
            city = "Toronto",
            profession = "Physician",
            bio = "Family-oriented diaspora physician in Toronto."
        )

        val prepared = PreparedPhotoUpload(
            bytes = validPngBytes(1024),
            fileName = "portrait.png",
            mimeType = "image/png",
            formatLabel = "PNG",
            sizeBytes = 1024L
        )

        val collectJob = launch(kotlinx.coroutines.Dispatchers.Unconfined) {
            viewModel.myProfile.collect {}
        }
        var callbackOk = false
        var callbackMsg = ""
        viewModel.uploadAndSaveProfilePhoto(prepared, fallbackProfile = initialProfile) { ok, msg ->
            callbackOk = ok
            callbackMsg = msg
        }
        for (i in 0..80) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            if ((callbackOk || callbackMsg.isNotBlank()) && viewModel.myProfile.value != null) break
            delay(25)
        }
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        val updated = viewModel.myProfile.value
        collectJob.cancel()
        assertTrue("Expected upload callback success, got ok=$callbackOk msg=$callbackMsg", callbackOk)
        assertNotNull(updated)
        assertEquals(
            "https://res.cloudinary.com/v0c1td87/image/upload/v1731900000/biyeabroad/preserved_bio.png",
            updated?.photoUrl
        )
        assertEquals("biyeabroad/preserved_bio", updated?.photoPublicId)
        assertEquals("Nusrat Jahan", updated?.displayName)
        assertEquals("Physician", updated?.profession)
        assertEquals("Toronto", updated?.city)
        assertEquals("Family-oriented diaspora physician in Toronto.", updated?.bio)
    }
}
