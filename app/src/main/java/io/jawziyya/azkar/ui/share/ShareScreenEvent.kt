package io.jawziyya.azkar.ui.share

import android.graphics.Bitmap

sealed interface ShareScreenEvent {
    data class OnSaveGallery(val bitmap: Bitmap) : ShareScreenEvent
    data class OnDefaultShare(val bitmap: Bitmap) : ShareScreenEvent
}