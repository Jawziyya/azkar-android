package io.jawziyya.azkar.database.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 23.02.2023.
 */

@Immutable
@Parcelize
data class Fadail(
    val id: Long,
    val text: String?,
    val sourceRaw: String?,
    val sourceExt: String?,
) : Parcelable {
    val source: Source? get() = Source.fromValue(sourceRaw).firstOrNull()
}
