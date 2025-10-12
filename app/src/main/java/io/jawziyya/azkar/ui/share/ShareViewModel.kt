package io.jawziyya.azkar.ui.share

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jawziyya.azkar.database.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShareViewModel(
    private val azkarId: Long,
    private val application: Application,
    private val databaseHelper: DatabaseHelper,
) : ViewModel() {

    private val internalState: MutableStateFlow<ShareScreenState> =
        MutableStateFlow(ShareScreenState())
    val state: StateFlow<ShareScreenState> get() = internalState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            internalState.update {
                it.copy(
                    azkar = databaseHelper.getAzkar(azkarId),
                )
            }
        }
    }

    fun onEvent(event: ShareScreenEvent): Unit = when (event) {
        is ShareScreenEvent.OnDefaultShare -> shareImage(event.bitmap)
        is ShareScreenEvent.OnSaveGallery -> saveToGallery(event.bitmap)
    }

    private fun saveToGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val timestamp =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val fileName = "azkar_$timestamp.jpg"

                    // For Android 10+ (API 29+), use MediaStore
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            put(
                                android.provider.MediaStore.MediaColumns.RELATIVE_PATH,
                                Environment.DIRECTORY_PICTURES + "/Azkar"
                            )
                        }
                    }

                    val uri = application.contentResolver.insert(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )

                    uri?.let { imageUri ->
                        application.contentResolver.openOutputStream(imageUri)
                            ?.use { outputStream ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                            }
                        Timber.d("Image saved to gallery: $imageUri")
                        Result.success(Unit)
                    } ?: run {
                        throw Exception("Failed to create image URI")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save image to gallery")
                Result.failure(e)
            }
        }
    }

    private fun shareImage(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val timestamp =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val fileName = "azkar_$timestamp.jpg"

                    // Create temporary file for sharing
                    val cacheDir = application.cacheDir
                    val tempFile = File(cacheDir, fileName)

                    tempFile.outputStream().use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    }

                    val uri = androidx.core.content.FileProvider.getUriForFile(
                        application,
                        "${application.packageName}.fileprovider",
                        tempFile
                    )

                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "image/jpeg"
                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    application.startActivity(
                        android.content.Intent.createChooser(
                            intent,
                            "Share Azkar"
                        )
                    )
                    Timber.d("Image shared successfully")
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to share image")
                Result.failure(e)
            }
        }
    }
}