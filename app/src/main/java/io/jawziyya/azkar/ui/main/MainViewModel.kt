package io.jawziyya.azkar.ui.main

import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.ui.zikr.ZikrScreenKey
import io.jawziyya.azkar.ui.azkar.AzkarScreenKey
import com.zhuinden.simplestack.Backstack

/**
 * Created by uvays on 06.06.2022.
 */

class MainViewModel(private val backstack: Backstack) {

    fun onAzkarCategoryClick(azkarCategory: AzkarCategory) {
        if (azkarCategory.main) {
            backstack.goTo(ZikrScreenKey(azkarCategory = azkarCategory, azkarIndex = 0))
        } else {
            backstack.goTo(AzkarScreenKey(azkarCategory = azkarCategory))
        }
    }
}