package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.data.remote.CloudinaryUploader
import com.example.data.remote.PreparedPhotoUpload
import com.example.ui.theme.biyeAbroadTextFieldColors
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.DarkText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarmIvory
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * Saves an image picked from the user's gallery directly to the app's internal storage directory.
 * This guarantees ZERO cloud storage costs ($0 billing) as files are kept locally on device.
 */
fun savePhotoToAppStorage(context: Context, sourceUri: Uri): String? {
    return try {
        val photosDir = File(context.filesDir, "profile_photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        val targetFile = File(photosDir, "profile_${System.currentTimeMillis()}.jpg")
        val copied = context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
            true
        } ?: false
        if (copied && targetFile.exists() && targetFile.length() > 0L) {
            targetFile.absolutePath
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Saves a captured Camera Bitmap to app internal storage.
 */
fun saveBitmapToAppStorage(context: Context, bitmap: android.graphics.Bitmap): String? {
    return try {
        val photosDir = File(context.filesDir, "profile_photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        val targetFile = File(photosDir, "profile_cam_${System.currentTimeMillis()}.jpg")
        FileOutputStream(targetFile).use { output ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, output)
        }
        targetFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Reusable Preview & Progress Dialog for Cloudinary Profile Photo Uploads.
 * Displays the selected image preview, file format & size validation info,
 * live upload progress bar, error/retry state, and completion confirmation.
 */
@Composable
fun CloudinaryPhotoUploadDialog(
    prepared: PreparedPhotoUpload,
    isUploading: Boolean,
    uploadProgress: Float,
    errorMessage: String?,
    successMessage: String?,
    onConfirmUpload: () -> Unit,
    onChooseAnother: () -> Unit,
    onDismiss: () -> Unit
) {
    val previewBitmap = remember(prepared.bytes) {
        CloudinaryUploader.decodeSampledPreviewBitmap(prepared.bytes, maxDimension = 1024)?.asImageBitmap()
    }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = BurgundyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (successMessage != null) "Photo Uploaded" else "Preview & Upload Photo",
                    fontWeight = FontWeight.Bold,
                    color = BurgundyDeep,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Image Preview
                Box(
                    modifier = Modifier
                        .size(148.dp)
                        .clip(CircleShape)
                        .background(WarmIvory)
                        .border(2.5.dp, GoldAccent, CircleShape)
                        .testTag("cloudinary_photo_preview"),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewBitmap != null) {
                        Image(
                            bitmap = previewBitmap,
                            contentDescription = "Selected Profile Photo Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Photo Preview Placeholder",
                            tint = BurgundyPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Format & Size Badge
                Surface(
                    color = WarmIvory,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${prepared.formatLabel} • ${CloudinaryUploader.formatSizeMbOrKb(prepared.sizeBytes)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BurgundyDeep
                        )
                        Text(
                            text = "Max 5 MB",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }

                // Upload Progress Indicator
                if (isUploading) {
                    val pct = (uploadProgress * 100f).roundToInt().coerceIn(0, 100)
                    Column(
                        modifier = Modifier.fillMaxWidth().testTag("cloudinary_upload_progress"),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Uploading photo...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = BurgundyPrimary
                            )
                            Text(
                                text = "$pct%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDeep
                            )
                        }
                        LinearProgressIndicator(
                            progress = { uploadProgress.coerceIn(0.02f, 1f) },
                            color = BurgundyPrimary,
                            trackColor = GoldLight.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }

                // Error Message
                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A)),
                        modifier = Modifier.fillMaxWidth().testTag("cloudinary_upload_error")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Upload Error",
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage,
                                fontSize = 12.sp,
                                color = Color(0xFFB71C1C),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Success Message
                if (!successMessage.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                        modifier = Modifier.fillMaxWidth().testTag("cloudinary_upload_success")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Upload Success",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = successMessage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B5E20),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (successMessage != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_cloudinary_done")
                ) {
                    Text("Done", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onConfirmUpload,
                    enabled = !isUploading,
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_cloudinary_upload")
                ) {
                    Text(
                        text = if (isUploading) "Uploading..." else if (errorMessage != null) "Retry Upload" else "Upload & Save Photo",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            if (!isUploading && successMessage == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onChooseAnother) {
                        Text("Change", color = BurgundyPrimary)
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MutedText)
                    }
                }
            }
        }
    )
}

/**
 * Reusable Interactive Date of Birth Picker Dialog.
 * Allows quick selection of Year, Month, and Day without tedious month-by-month pagination.
 */
@Composable
fun BiyeAbroadDatePickerDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Parse initial date (expected format YYYY-MM-DD or DD/MM/YYYY)
    val parsed = remember(initialDate) {
        try {
            val parts = initialDate.trim().split("-", "/", ".")
            if (parts.size == 3) {
                val y = if (parts[0].length == 4) parts[0].toInt() else parts[2].toInt()
                val m = if (parts[0].length == 4) parts[1].toInt() else parts[0].toInt()
                val d = if (parts[0].length == 4) parts[2].toInt() else parts[1].toInt()
                Triple(y, m.coerceIn(1, 12), d.coerceIn(1, 31))
            } else {
                Triple(1998, 5, 15)
            }
        } catch (_: Exception) {
            Triple(1998, 5, 15)
        }
    }

    var selectedYear by remember { mutableIntStateOf(parsed.first) }
    var selectedMonth by remember { mutableIntStateOf(parsed.second) }
    var selectedDay by remember { mutableIntStateOf(parsed.third) }
    var customTypedDate by remember { mutableStateOf(initialDate) }
    var activeTab by remember { mutableStateOf("year") } // "year", "month", "day"

    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr",
        "May", "Jun", "Jul", "Aug",
        "Sep", "Oct", "Nov", "Dec"
    )

    // Complete adult age range (1930 - 2015) covering every member
    val years = remember { (2015 downTo 1930).toList() }
    val daysInMonth = remember(selectedYear, selectedMonth) {
        try {
            java.time.YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
        } catch (_: Exception) {
            30
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BurgundyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Set Date of Birth",
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDeep,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Type any date or select from calendar",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                // Direct text writing option
                OutlinedTextField(
                    value = customTypedDate,
                    onValueChange = { input ->
                        customTypedDate = input
                        try {
                            val parts = input.trim().split("-", "/", ".")
                            if (parts.size == 3) {
                                val y = if (parts[0].length == 4) parts[0].toInt() else parts[2].toInt()
                                val m = if (parts[0].length == 4) parts[1].toInt() else parts[0].toInt()
                                val d = if (parts[0].length == 4) parts[2].toInt() else parts[1].toInt()
                                if (y in 1930..2025 && m in 1..12 && d in 1..31) {
                                    selectedYear = y
                                    selectedMonth = m
                                    selectedDay = d
                                }
                            }
                        } catch (_: Exception) {}
                    },
                    label = { Text("Type Any Date") },
                    placeholder = { Text("e.g. 1998-05-15 or 15/05/1998") },
                    colors = biyeAbroadTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("input_custom_date")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Date Display
                Surface(
                    color = WarmIvory,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Selected Date of Birth:",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (customTypedDate.isNotBlank()) customTypedDate else String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay.coerceAtMost(daysInMonth)),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BurgundyDeep
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selector Tabs (Year / Month / Day)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf(
                        Triple("year", "Year", "$selectedYear"),
                        Triple("month", "Month", monthNames.getOrElse(selectedMonth - 1) { "$selectedMonth" }),
                        Triple("day", "Day", "$selectedDay")
                    )

                    tabs.forEach { (tabKey, label, displayValue) ->
                        val isSelected = activeTab == tabKey
                        Surface(
                            onClick = { activeTab = tabKey },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BurgundyPrimary else PureWhite,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) BurgundyPrimary else GoldBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = if (isSelected) GoldLight else MutedText
                                )
                                Text(
                                    text = displayValue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PureWhite else DarkText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content based on active tab
                when (activeTab) {
                    "year" -> {
                        Text("Select Birth Year:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.height(180.dp)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(years) { yr ->
                                    val isYrSelected = yr == selectedYear
                                    Surface(
                                        onClick = {
                                            selectedYear = yr
                                            activeTab = "month" // Automatically flow to next step
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isYrSelected) BurgundyPrimary else PureWhite,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isYrSelected) BurgundyPrimary else GoldBorder),
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$yr",
                                                fontSize = 12.sp,
                                                fontWeight = if (isYrSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isYrSelected) PureWhite else DarkText
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "month" -> {
                        Text("Select Birth Month:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.height(180.dp)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(monthNames.indices.toList()) { idx ->
                                    val mNum = idx + 1
                                    val isMSelected = mNum == selectedMonth
                                    Surface(
                                        onClick = {
                                            selectedMonth = mNum
                                            activeTab = "day" // Automatically flow to next step
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isMSelected) BurgundyPrimary else PureWhite,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isMSelected) BurgundyPrimary else GoldBorder),
                                        modifier = Modifier.height(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                                            Text(
                                                text = monthNames[idx],
                                                fontSize = 11.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = if (isMSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isMSelected) PureWhite else DarkText
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "day" -> {
                        Text("Select Birth Day:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Spacer(modifier = Modifier.height(6.dp))
                        val days = (1..daysInMonth).toList()
                        Box(modifier = Modifier.height(180.dp)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(days) { d ->
                                    val isDSelected = d == selectedDay
                                    Surface(
                                        onClick = { selectedDay = d },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isDSelected) BurgundyPrimary else PureWhite,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDSelected) BurgundyPrimary else GoldBorder),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$d",
                                                fontSize = 12.sp,
                                                fontWeight = if (isDSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isDSelected) PureWhite else DarkText
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalDay = selectedDay.coerceAtMost(daysInMonth)
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, finalDay)
                    val finalDate = if (customTypedDate.isNotBlank()) customTypedDate.trim() else formattedDate
                    onDateSelected(finalDate)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary)
            ) {
                Text("Confirm", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BurgundyPrimary)
            }
        }
    )
}

/**
 * Clean & Prominent Gender Selector with only Male & Female options.
 * Male is displayed first and Female second as requested by user.
 */
@Composable
fun GenderSelectionCards(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        Triple("male", "Male (Groom)", Icons.Default.Male),
        Triple("female", "Female (Bride)", Icons.Default.Female)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Gender (Select Groom or Bride) *",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { (code, label, icon) ->
                val isSelected = selectedGender.equals(code, ignoreCase = true)
                Surface(
                    onClick = { onGenderSelected(code) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFFFFF1F2) else PureWhite,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BurgundyPrimary else GoldBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("gender_card_$code")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = BurgundyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = BurgundyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            color = if (isSelected) BurgundyPrimary else DarkText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Profile Photo Selector backed by Cloudinary unsigned uploads (v0c1td87 / BiyeAbroad):
 * - Allows JPG, JPEG, PNG, and WEBP formats up to 5 MB.
 * - Shows image preview and live upload progress before applying the secure_url & public_id.
 * - Preserves preset cultural diaspora avatars.
 */
@Composable
fun ProfilePhotoPickerSection(
    currentPhotoUrl: String,
    onPhotoSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    onPhotoUploaded: ((secureUrl: String, publicId: String) -> Unit)? = null,
    onPersistUploadedPhoto: (suspend (secureUrl: String, publicId: String) -> Result<Unit>)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uploader = remember { CloudinaryUploader() }

    var showSourceDialog by remember { mutableStateOf(false) }
    var pendingPrepared by remember { mutableStateOf<PreparedPhotoUpload?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var uploadSuccess by remember { mutableStateOf<String?>(null) }
    var selectionError by remember { mutableStateOf<String?>(null) }
    var selectionStatus by remember { mutableStateOf<String?>(null) }

    val handlePreparedResult: (Result<PreparedPhotoUpload>) -> Unit = { res ->
        res.onSuccess { prepared ->
            selectionError = null
            selectionStatus = null
            uploadError = null
            uploadSuccess = null
            uploadProgress = 0f
            pendingPrepared = prepared
        }.onFailure { err ->
            val msg = err.message ?: "Could not load selected image. Allowed: JPG, JPEG, PNG, WEBP up to 5 MB."
            selectionStatus = null
            selectionError = msg
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    val handleCancelledSelection: () -> Unit = {
        selectionError = null
        selectionStatus = "Photo selection cancelled. Existing photo kept."
        Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
    }

    // Android Photo Picker (Zero broad storage permissions required)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                handlePreparedResult(CloudinaryUploader.preparePhotoFromUri(context, uri))
            }
        } else {
            handleCancelledSelection()
        }
    }

    // ACTION_OPEN_DOCUMENT Browser (Shows Device Folders, Downloads, Pictures, Gallery & Drive even if MediaStore Photo Picker is empty)
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                handlePreparedResult(CloudinaryUploader.preparePhotoFromUri(context, uri))
            }
        } else {
            handleCancelledSelection()
        }
    }

    // Generic Gallery / Content Picker fallback (Zero broad storage permissions required)
    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                handlePreparedResult(CloudinaryUploader.preparePhotoFromUri(context, uri))
            }
        } else {
            handleCancelledSelection()
        }
    }

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scope.launch {
                handlePreparedResult(CloudinaryUploader.preparePhotoFromBitmap(bitmap))
            }
        } else {
            handleCancelledSelection()
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (_: Exception) {
                val msg = "No camera app is available on this device. Please choose a photo from Device Folders."
                selectionError = msg
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        } else {
            val msg = "Camera permission was denied. You can still select a photo from your device folders without storage permissions."
            selectionError = msg
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    pendingPrepared?.let { prepared ->
        CloudinaryPhotoUploadDialog(
            prepared = prepared,
            isUploading = isUploading,
            uploadProgress = uploadProgress,
            errorMessage = uploadError,
            successMessage = uploadSuccess,
            onConfirmUpload = {
                if (isUploading) return@CloudinaryPhotoUploadDialog
                scope.launch {
                    isUploading = true
                    uploadError = null
                    uploadSuccess = null
                    uploadProgress = 0.05f
                    val result = uploader.uploadPreparedPhoto(prepared) { progress ->
                        uploadProgress = progress
                    }
                    result.onSuccess { uploaded ->
                        val persistResult = if (onPersistUploadedPhoto != null) {
                            onPersistUploadedPhoto(uploaded.secureUrl, uploaded.publicId)
                        } else {
                            Result.success(Unit)
                        }
                        isUploading = false
                        persistResult.onSuccess {
                            uploadProgress = 1.0f
                            uploadSuccess = if (onPersistUploadedPhoto != null) {
                                "Photo uploaded & saved to your BiyeAbroad profile!"
                            } else {
                                "Photo uploaded! Save your profile below to finish."
                            }
                            onPhotoSelected(uploaded.secureUrl)
                            onPhotoUploaded?.invoke(uploaded.secureUrl, uploaded.publicId)
                            Toast.makeText(context, "Profile photo updated successfully!", Toast.LENGTH_SHORT).show()
                        }.onFailure { dbErr ->
                            uploadError = "Photo uploaded to Cloudinary, but could not save to your profile: ${dbErr.message ?: "Please retry."}"
                        }
                    }.onFailure { err ->
                        isUploading = false
                        uploadError = err.message ?: "Upload failed. Please check your connection and retry."
                    }
                }
            },
            onChooseAnother = {
                pendingPrepared = null
                uploadError = null
                uploadSuccess = null
                showSourceDialog = true
            },
            onDismiss = {
                if (!isUploading) {
                    pendingPrepared = null
                    uploadError = null
                    uploadSuccess = null
                }
            }
        )
    }

    val sampleAvatars = listOf(
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800", // Male 1
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=800", // Male 2
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=800", // Male 3
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=800", // Female 1
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=800", // Female 2
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800"  // Female 3
    )

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            containerColor = PureWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = BurgundyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Profile Photo Source", fontWeight = FontWeight.Bold, color = BurgundyDeep, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Choose a JPG, JPEG, PNG, or WEBP photo (max 5 MB). If Gallery shows empty folders, use Browse Photos & Downloads:",
                        fontSize = 12.sp,
                        color = DarkText
                    )

                    Button(
                        onClick = {
                            showSourceDialog = false
                            try {
                                openDocumentLauncher.launch(
                                    arrayOf("image/*", "image/jpeg", "image/jpg", "image/png", "image/webp")
                                )
                            } catch (_: Exception) {
                                try {
                                    getContentLauncher.launch("image/*")
                                } catch (_: Exception) {
                                    selectionError = "No file or gallery picker found on this device."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_browse_device_files_helper")
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp), tint = PureWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browse Photos & Downloads (All Folders)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showSourceDialog = false
                            try {
                                if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    getContentLauncher.launch("image/*")
                                }
                            } catch (_: Exception) {
                                try {
                                    getContentLauncher.launch("image/*")
                                } catch (_: Exception) {
                                    selectionError = "Unable to open Photo Picker on this device."
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_pick_gallery_photo")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = BurgundyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Android Photo Picker (Gallery)", fontSize = 12.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showSourceDialog = false
                            try {
                                getContentLauncher.launch("image/*")
                            } catch (_: Exception) {
                                selectionError = "No external gallery app available on this device."
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_pick_any_gallery_helper")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = BurgundyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose via Any Gallery App", fontSize = 12.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showSourceDialog = false
                            val hasCam = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            if (hasCam) {
                                try {
                                    cameraLauncher.launch(null)
                                } catch (_: Exception) {
                                    selectionError = "Unable to start camera on this device."
                                }
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_take_camera_photo")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = BurgundyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo with Camera", fontSize = 12.sp, color = BurgundyPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSourceDialog = false }) {
                    Text("Cancel", color = BurgundyPrimary)
                }
            }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Photo Avatar Preview (Clickable to change)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF1F2))
                    .border(2.dp, GoldBorder, CircleShape)
                    .clickable { showSourceDialog = true }
            ) {
                if (currentPhotoUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data(currentPhotoUrl)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = BurgundyPrimary,
                        modifier = Modifier.size(44.dp).align(Alignment.Center)
                    )
                }

                // Camera icon overlay badge
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .background(BurgundyPrimary, CircleShape)
                        .border(1.dp, PureWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Upload",
                        tint = PureWhite,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { showSourceDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_choose_gallery_photo")
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = PureWhite)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentPhotoUrl.isNotBlank()) "Change Photo" else "Upload / Change Photo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "JPG, PNG, WEBP • Max 5 MB",
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }
                if (!selectionError.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectionError!!,
                        fontSize = 11.sp,
                        color = Color(0xFFC62828),
                        lineHeight = 14.sp,
                        modifier = Modifier.testTag("photo_selection_error")
                    )
                } else if (!selectionStatus.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectionStatus!!,
                        fontSize = 11.sp,
                        color = MutedText,
                        lineHeight = 14.sp,
                        modifier = Modifier.testTag("photo_selection_status")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset Avatars for instant 1-tap selection
        Text(
            text = "Or choose a dignified Bangladeshi avatar:",
            fontSize = 11.sp,
            color = DarkText,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(sampleAvatars) { url ->
                val isSelected = currentPhotoUrl == url
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) BurgundyPrimary else GoldBorder,
                            shape = CircleShape
                        )
                        .clickable {
                            onPhotoSelected(url)
                            Toast.makeText(context, "Avatar selected", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = "Avatar Preset",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
