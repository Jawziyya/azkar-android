package io.jawziyya.azkar.data.repository

import android.app.Application
import io.jawziyya.azkar.database.model.Azkar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.File

/**
 * Created by uvays on 09.06.2022.
 */

class FileRepository(private val application: Application) {

    private val assetsCacheFileName: String = "ASSETS_CACHE_FILE_NAME"

    suspend fun getAudioFile(azkar: Azkar): File? = withContext(Dispatchers.IO) {
        if (azkar.audioName == null) {
            return@withContext null
        }

        val dir = File(application.cacheDir, "audio")
        dir.mkdirs()

        val file = File(dir, assetsCacheFileName)
        if (file.exists()) {
            file.delete()
        }

        try {
            val source = application.assets.open("audio/${azkar.audioName}").source()
            val sink = file.sink().buffer()
            sink.writeAll(source)
            sink.close()
        } catch (e: Exception) {
            Timber.d(azkar.audioName)
            Timber.e(e)
            return@withContext null
        }

        return@withContext file
    }
}