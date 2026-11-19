package com.example.data.remote

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

data class PreparedPhotoUpload(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
    val formatLabel: String,
    val sizeBytes: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PreparedPhotoUpload) return false
        return bytes.contentEquals(other.bytes) &&
            fileName == other.fileName &&
            mimeType == other.mimeType &&
            formatLabel == other.formatLabel &&
            sizeBytes == other.sizeBytes
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + formatLabel.hashCode()
        result = 31 * result + sizeBytes.hashCode()
        return result
    }
}

data class CloudinaryUploadResult(
    val secureUrl: String,
    val publicId: String,
    val format: String = "",
    val bytes: Long = 0L
)

class CloudinaryUploader(
    private val cloudName: String = CLOUD_NAME,
    private val uploadPreset: String = UPLOAD_PRESET,
    private val uploadUrl: String = "https://api.cloudinary.com/v1_1/$cloudName/image/upload",
    private val client: OkHttpClient = defaultOkHttpClient()
) {

    companion object {
        const val CLOUD_NAME = "v0c1td87"
        const val UPLOAD_PRESET = "BiyeAbroad"
        const val MAX_FILE_SIZE_BYTES = 5L * 1024L * 1024L // 5 MB

        val ALLOWED_MIME_TYPES = setOf(
            "image/jpeg",
            "image/jpg",
            "image/pjpeg",
            "image/png",
            "image/x-png",
            "image/webp"
        )

        val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")

        fun defaultOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        fun formatSizeMbOrKb(bytes: Long): String {
            return if (bytes >= 1024L * 1024L) {
                String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
            } else {
                String.format(Locale.US, "%.0f KB", (bytes / 1024.0).coerceAtLeast(1.0))
            }
        }

        /**
         * Detects JPG/JPEG, PNG, or WEBP strictly from file header magic bytes.
         * Returns Pair(mimeType, formatLabel) or null if unsupported.
         */
        fun detectImageFormatFromBytes(bytes: ByteArray): Pair<String, String>? {
            // JPEG / JPG: FF D8 FF
            if (bytes.size >= 3 &&
                bytes[0] == 0xFF.toByte() &&
                bytes[1] == 0xD8.toByte() &&
                bytes[2] == 0xFF.toByte()
            ) {
                return "image/jpeg" to "JPEG"
            }

            // PNG: 89 50 4E 47 0D 0A 1A 0A
            if (bytes.size >= 8 &&
                bytes[0] == 0x89.toByte() &&
                bytes[1] == 0x50.toByte() &&
                bytes[2] == 0x4E.toByte() &&
                bytes[3] == 0x47.toByte() &&
                bytes[4] == 0x0D.toByte() &&
                bytes[5] == 0x0A.toByte() &&
                bytes[6] == 0x1A.toByte() &&
                bytes[7] == 0x0A.toByte()
            ) {
                return "image/png" to "PNG"
            }

            // WEBP: "RIFF" .... "WEBP"
            if (bytes.size >= 12 &&
                bytes[0] == 'R'.code.toByte() &&
                bytes[1] == 'I'.code.toByte() &&
                bytes[2] == 'F'.code.toByte() &&
                bytes[3] == 'F'.code.toByte() &&
                bytes[8] == 'W'.code.toByte() &&
                bytes[9] == 'E'.code.toByte() &&
                bytes[10] == 'B'.code.toByte() &&
                bytes[11] == 'P'.code.toByte()
            ) {
                return "image/webp" to "WEBP"
            }

            return null
        }

        fun validateFormatMetadata(mimeType: String?, fileName: String?): Boolean {
            val cleanMime = mimeType?.trim()?.lowercase(Locale.US)
            if (!cleanMime.isNullOrBlank()) {
                if (cleanMime in ALLOWED_MIME_TYPES) return true
                if (cleanMime != "application/octet-stream" && cleanMime != "image/*") {
                    return false
                }
            }
            val ext = fileName?.substringAfterLast('.', "")?.trim()?.lowercase(Locale.US)
            if (!ext.isNullOrBlank() && ext in ALLOWED_EXTENSIONS) {
                return true
            }
            // Allow magic-byte inspection to decide if metadata is generic/missing
            return cleanMime.isNullOrBlank() || cleanMime == "application/octet-stream" || cleanMime == "image/*"
        }

        suspend fun preparePhotoFromUri(
            context: Context,
            uri: Uri
        ): Result<PreparedPhotoUpload> = withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val reportedMime = resolver.getType(uri)
                val meta = queryUriMetadata(resolver, uri)
                val fileName = meta.first ?: "profile_photo.jpg"
                val reportedSize = meta.second

                if (!validateFormatMetadata(reportedMime, fileName)) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Unsupported image format. Only JPG, JPEG, PNG, and WEBP files are allowed.")
                    )
                }

                if (reportedSize != null && reportedSize > MAX_FILE_SIZE_BYTES) {
                    return@withContext Result.failure(
                        IllegalArgumentException(
                            "Image is too large (${formatSizeMbOrKb(reportedSize)}). Maximum upload size is 5 MB."
                        )
                    )
                }

                val stream = try {
                    resolver.openInputStream(uri)
                        ?: resolver.openAssetFileDescriptor(uri, "r")?.createInputStream()
                } catch (sec: SecurityException) {
                    return@withContext Result.failure(
                        SecurityException("Permission denied reading the selected photo. Please pick it again using Browse Photos & Downloads.", sec)
                    )
                }

                val bytes = stream?.use { input ->
                    val output = ByteArrayOutputStream()
                    val buffer = ByteArray(8 * 1024)
                    var total = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        total += read
                        if (total > MAX_FILE_SIZE_BYTES) {
                            return@withContext Result.failure(
                                IllegalArgumentException(
                                    "Image exceeds the 5 MB maximum upload limit. Please select a smaller photo."
                                )
                            )
                        }
                        output.write(buffer, 0, read)
                    }
                    output.toByteArray()
                } ?: return@withContext Result.failure(
                    IOException("Unable to read the selected image from device storage.")
                )

                if (bytes.isEmpty()) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Selected image file is empty.")
                    )
                }

                val detected = detectImageFormatFromBytes(bytes)
                    ?: return@withContext Result.failure(
                        IllegalArgumentException("Unsupported image format. Only JPG, JPEG, PNG, and WEBP images are allowed.")
                    )

                val normalizedFileName = ensureExtension(fileName, detected.second)
                Result.success(
                    PreparedPhotoUpload(
                        bytes = bytes,
                        fileName = normalizedFileName,
                        mimeType = detected.first,
                        formatLabel = detected.second,
                        sizeBytes = bytes.size.toLong()
                    )
                )
            } catch (sec: SecurityException) {
                Result.failure(
                    SecurityException("Permission denied accessing this photo. Please try choosing from Device Files / Downloads.", sec)
                )
            } catch (_: OutOfMemoryError) {
                Result.failure(IllegalArgumentException("Selected photo is too large to process. Please select an image under 5 MB."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        fun decodeSampledPreviewBitmap(bytes: ByteArray, maxDimension: Int = 1024): Bitmap? {
            return try {
                val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, boundsOpts)
                var inSampleSize = 1
                val width = boundsOpts.outWidth
                val height = boundsOpts.outHeight
                if (width > maxDimension || height > maxDimension) {
                    val halfWidth = width / 2
                    val halfHeight = height / 2
                    while ((halfWidth / inSampleSize) >= maxDimension &&
                        (halfHeight / inSampleSize) >= maxDimension
                    ) {
                        inSampleSize *= 2
                    }
                }
                val decodeOpts = BitmapFactory.Options().apply {
                    this.inSampleSize = inSampleSize
                }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts)
            } catch (_: Throwable) {
                null
            }
        }

        suspend fun preparePhotoFromBitmap(
            bitmap: Bitmap,
            fileName: String = "camera_profile_${System.currentTimeMillis()}.jpg"
        ): Result<PreparedPhotoUpload> = withContext(Dispatchers.IO) {
            try {
                var quality = 92
                var encodedBytes = ByteArray(0)
                while (quality >= 60) {
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                    encodedBytes = baos.toByteArray()
                    if (encodedBytes.size <= MAX_FILE_SIZE_BYTES) break
                    quality -= 10
                }
                if (encodedBytes.isEmpty()) {
                    return@withContext Result.failure(IllegalStateException("Failed to encode camera image."))
                }
                if (encodedBytes.size > MAX_FILE_SIZE_BYTES) {
                    return@withContext Result.failure(
                        IllegalArgumentException(
                            "Captured image exceeds 5 MB (${formatSizeMbOrKb(encodedBytes.size.toLong())})."
                        )
                    )
                }
                Result.success(
                    PreparedPhotoUpload(
                        bytes = encodedBytes,
                        fileName = ensureExtension(fileName, "JPEG"),
                        mimeType = "image/jpeg",
                        formatLabel = "JPEG",
                        sizeBytes = encodedBytes.size.toLong()
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        private fun queryUriMetadata(resolver: ContentResolver, uri: Uri): Pair<String?, Long?> {
            var name: String? = null
            var size: Long? = null
            try {
                resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIdx >= 0 && !cursor.isNull(nameIdx)) {
                            name = cursor.getString(nameIdx)
                        }
                        if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) {
                            size = cursor.getLong(sizeIdx)
                        }
                    }
                }
            } catch (_: Exception) {
                // Ignore provider metadata failures; fallback to stream size & magic bytes
            }
            return name to size
        }

        private fun ensureExtension(fileName: String, formatLabel: String): String {
            val ext = when (formatLabel.uppercase(Locale.US)) {
                "PNG" -> "png"
                "WEBP" -> "webp"
                else -> "jpg"
            }
            val currentExt = fileName.substringAfterLast('.', "").lowercase(Locale.US)
            return if (currentExt in ALLOWED_EXTENSIONS) {
                fileName
            } else {
                val base = fileName.substringBeforeLast('.').ifBlank { "profile_photo" }
                "$base.$ext"
            }
        }
    }

    suspend fun uploadPreparedPhoto(
        prepared: PreparedPhotoUpload,
        onProgress: (Float) -> Unit = {}
    ): Result<CloudinaryUploadResult> = withContext(Dispatchers.IO) {
        try {
            if (prepared.sizeBytes > MAX_FILE_SIZE_BYTES || prepared.bytes.size > MAX_FILE_SIZE_BYTES) {
                return@withContext Result.failure(
                    IllegalArgumentException("Image exceeds maximum 5 MB upload size.")
                )
            }
            if (detectImageFormatFromBytes(prepared.bytes) == null) {
                return@withContext Result.failure(
                    IllegalArgumentException("Only JPG, JPEG, PNG, and WEBP formats are supported.")
                )
            }

            onProgress(0.02f)

            val fileRequestBody = CountingByteArrayRequestBody(
                bytes = prepared.bytes,
                contentType = prepared.mimeType,
                onProgress = onProgress
            )

            // Unsigned upload: strictly uses upload_preset only (no API Secret on client)
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", uploadPreset)
                .addFormDataPart("file", prepared.fileName, fileRequestBody)
                .build()

            val request = Request.Builder()
                .url(uploadUrl)
                .post(multipartBody)
                .build()

            client.newCall(request).execute().use { response ->
                val rawBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val cloudinaryErr = try {
                        JSONObject(rawBody).optJSONObject("error")?.optString("message")
                    } catch (_: Exception) {
                        null
                    }
                    val message = cloudinaryErr?.takeIf { it.isNotBlank() }
                        ?: "Cloudinary upload failed (HTTP ${response.code}). Please try again."
                    return@withContext Result.failure(IOException(message))
                }

                val json = JSONObject(rawBody)
                val secureUrl = json.optString("secure_url").trim()
                val publicId = json.optString("public_id").trim()
                val format = json.optString("format").trim()
                val uploadedBytes = json.optLong("bytes", prepared.sizeBytes)

                if (secureUrl.isBlank() || !secureUrl.startsWith("https://")) {
                    return@withContext Result.failure(
                        IOException("Cloudinary response did not include a valid secure_url.")
                    )
                }
                if (publicId.isBlank()) {
                    return@withContext Result.failure(
                        IOException("Cloudinary response did not include a valid public_id.")
                    )
                }

                onProgress(1.0f)
                Result.success(
                    CloudinaryUploadResult(
                        secureUrl = secureUrl,
                        publicId = publicId,
                        format = format,
                        bytes = uploadedBytes
                    )
                )
            }
        } catch (e: IOException) {
            Result.failure(
                IOException(e.message ?: "Network error while uploading photo. Please check your internet connection.", e)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private class CountingByteArrayRequestBody(
        private val bytes: ByteArray,
        private val contentType: String,
        private val onProgress: (Float) -> Unit
    ) : RequestBody() {

        override fun contentType() = contentType.toMediaType()

        override fun contentLength(): Long = bytes.size.toLong()

        override fun writeTo(sink: BufferedSink) {
            val total = bytes.size.coerceAtLeast(1)
            val chunkSize = 8 * 1024
            var offset = 0
            while (offset < bytes.size) {
                val count = minOf(chunkSize, bytes.size - offset)
                sink.write(bytes, offset, count)
                offset += count
                val progress = (offset.toFloat() / total.toFloat()).coerceIn(0.02f, 0.98f)
                onProgress(progress)
            }
        }
    }
}
