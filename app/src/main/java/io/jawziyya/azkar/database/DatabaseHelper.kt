package io.jawziyya.azkar.database

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.CancellationSignal
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import io.jawziyya.azkar.R
import io.jawziyya.azkar.database.core.SQLiteAssetHelper
import io.jawziyya.azkar.database.model.Azkar
import io.jawziyya.azkar.database.model.AzkarCategory
import io.jawziyya.azkar.database.model.Fadail
import io.jawziyya.azkar.database.model.Hadith
import io.jawziyya.azkar.database.model.Source
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.settings.LanguageOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class DatabaseHelper(
    context: Context,
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
) : SQLiteAssetHelper(context, "azkar.db", null, 2) {

    private val database: SQLiteDatabase by lazy { readableDatabase }

    init {
        setForcedUpgrade()
    }

    private fun getLanguageOption(): LanguageOption = sharedPreferences
        .getString(Settings.languageKey, null)
        ?.let { value -> LanguageOption.valueOf(value) }
        ?: LanguageOption.getFallback()

    private suspend fun <T : Any> get(
        query: String,
        arguments: Array<String> = emptyArray(),
        mapper: (Cursor) -> T?,
    ): List<T> = suspendCancellableCoroutine { continuation ->
        try {
            val cancellationSignal = CancellationSignal()

            val cursor = database.rawQuery(query, arguments, cancellationSignal)

            if (cursor.count <= 0) {
                continuation.resume(emptyList())
                return@suspendCancellableCoroutine
            }

            val list = mutableListOf<T>()

            while (cursor.moveToNext()) {
                val item = mapper(cursor)
                if (item != null) {
                    list.add(item)
                }
            }

            cursor.close()

            continuation.resume(list)
            continuation.invokeOnCancellation { cancellationSignal.cancel() }
        } catch (e: Exception) {
            continuation.resume(emptyList())
        }
    }

    suspend fun getFadailList(): List<Fadail> = withContext(Dispatchers.IO) {
        val selectedLanguageOption = getLanguageOption()
        val languageOption = LanguageOption.mainArray.find { it == selectedLanguageOption }
            ?: LanguageOption.RUSSIAN

        val columns = listOfNotNull(
            "id",
            if (languageOption.main) "text_${languageOption.value} as text" else null,
            "source",
            "source_ext",
        )
            .joinToString(separator = ", ")

        return@withContext get(
            query = "select $columns from fadail",
            mapper = { cursor ->
                Fadail(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    text = cursor.getStringOrNull(cursor.getColumnIndex("text")),
                    sourceRaw = cursor.getString(cursor.getColumnIndexOrThrow("source")),
                    sourceExt = cursor.getString(cursor.getColumnIndexOrThrow("source_ext")),
                )
            },
        )
    }

    suspend fun getAzkarList(
        category: AzkarCategory
    ): List<Azkar> = withContext(Dispatchers.IO) {
        val languageOption = getLanguageOption()
        val language = languageOption.value

        val columns = listOfNotNull(
            "azkar.id",
            "`azkar+azkar_group`.`id`as azkarCategoryId",
            "azkar.repeats",
            "azkar.source",
            "azkar.text",
            "azkar.hadith",
            "azkar.audio_id",
            "`azkar+azkar_group`.`order`",
            "`azkar+azkar_group`.`group` as category",
            "audios.link as audioName",
            "azkar_$language.title",
            "azkar_$language.text as translation",
            "azkar_$language.benefits",
            "azkar_$language.notes",
            "azkar_$language.notes",
            if (languageOption.main) "azkar_$language.transliteration" else null
        )
            .joinToString(separator = ", ")

        return@withContext get(
            query = """
            select $columns
            from azkar 
            inner join `azkar+azkar_group` on azkar.id=`azkar+azkar_group`.azkar_id
            inner join audios on azkar.audio_id=audios.id
            inner join azkar_$language on azkar.id=azkar_$language.id
            where `azkar+azkar_group`.`group`=?
            order by `azkar+azkar_group`.`order` asc
            """.trimIndent(),
            arguments = arrayOf(category.value),
            mapper = { cursor ->
                val order = cursor.getInt(cursor.getColumnIndexOrThrow("order"))
                val fallbackTitle = "${resources.getString(R.string.dhikr_fallback_name)}${order}"
                val categoryValue = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val benefits = cursor.getStringOrNull(cursor.getColumnIndex("benefits"))
                    ?.takeIf { it.isNotBlank() }

                return@get Azkar(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    azkarCategoryId = cursor.getLong(cursor.getColumnIndexOrThrow("azkarCategoryId")),
                    category = AzkarCategory.fromValue(categoryValue)!!,
                    repeats = cursor.getInt(cursor.getColumnIndexOrThrow("repeats")),
                    repeatsLeft = cursor.getInt(cursor.getColumnIndexOrThrow("repeats")),
                    source = Source.fromValue(cursor.getString(cursor.getColumnIndexOrThrow("source"))),
                    text = cursor.getString(cursor.getColumnIndexOrThrow("text")),
                    title = cursor.getStringOrNull(cursor.getColumnIndex("title"))
                        ?: fallbackTitle,
                    translation = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("translation")),
                    transliteration = cursor.getStringOrNull(cursor.getColumnIndex("transliteration")),
                    benefits = benefits,
                    notes = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("notes")),
                    hadith = cursor.getLongOrNull(cursor.getColumnIndexOrThrow("hadith")),
                    audioName = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("audioName")),
                    order = order,
                )
            },
        )
    }

    suspend fun getHadith(id: Long): Hadith? = withContext(Dispatchers.IO) {
        val languageOption = getLanguageOption()

        val columns = listOfNotNull(
            "id",
            "text",
            if (languageOption.main) "translation_${languageOption.value} as translation" else null,
            "source",
            "source_ext",
        )
            .joinToString(separator = ", ")


        return@withContext get(
            query = """
                select $columns
                from ahadith 
                where id=?
            """.trimIndent(),
            arguments = arrayOf(id.toString()),
            mapper = { cursor ->
                Hadith(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    text = cursor.getString(cursor.getColumnIndexOrThrow("text")),
                    translation = cursor.getStringOrNull(cursor.getColumnIndex("translation")),
                    sources = Source.fromValue(cursor.getString(cursor.getColumnIndexOrThrow("source"))),
                    sourceExt = cursor.getString(cursor.getColumnIndexOrThrow("source_ext")),
                )
            }
        )
            .firstOrNull()
    }
}