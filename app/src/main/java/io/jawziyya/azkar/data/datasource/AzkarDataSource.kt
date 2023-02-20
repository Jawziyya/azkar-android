package io.jawziyya.azkar.data.datasource

import android.content.res.Resources
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.network.response.AzkarResponse

/**
 * Created by uvays on 06.06.2022.
 */

class AzkarDataSource(
    private val resources: Resources,
    private val moshi: Moshi,
) {

    suspend fun get(): List<AzkarResponse> {
        val json = resources.openRawResource(R.raw.azkar).bufferedReader().use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, AzkarResponse::class.java)
        val adapter = moshi.adapter<List<AzkarResponse>>(type)
        return adapter.fromJson(json) ?: emptyList()
    }
}