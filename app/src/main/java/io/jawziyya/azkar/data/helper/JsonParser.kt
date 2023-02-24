package io.jawziyya.azkar.data.helper

import android.content.res.Resources
import androidx.annotation.RawRes
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * Created by uvays on 23.02.2023.
 */

class JsonParser(
    val resources: Resources,
    val moshi: Moshi,
) {
    inline fun <reified T> parse(@RawRes rawRes: Int): List<T> {
        val json = resources.openRawResource(rawRes).bufferedReader().use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        val adapter = moshi.adapter<List<T>>(type)
        return adapter.fromJson(json) ?: emptyList()
    }
}