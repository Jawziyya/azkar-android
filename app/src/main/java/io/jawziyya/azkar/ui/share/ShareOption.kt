package io.jawziyya.azkar.ui.share

import androidx.annotation.StringRes
import io.jawziyya.azkar.R

enum class ShareOption(@StringRes val titleRes: Int) {
    Default(titleRes = R.string.share_option_default),
    Gallery(titleRes = R.string.share_option_gallery),
}