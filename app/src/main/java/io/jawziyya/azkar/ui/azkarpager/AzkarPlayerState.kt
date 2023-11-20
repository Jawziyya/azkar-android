package io.jawziyya.azkar.ui.azkarpager

import android.net.Uri

/**
 * Created by uvays on 15.09.2022.
 */

data class AzkarPlayerState(
    val azkarId: Long? = null,
    val duration: Long = 0L,
    val timestamp: Long = 0L,
    val playing: Boolean = false,
    val loading: Boolean = false,
    val uri: Uri? = null,
)
