package io.jawziyya.azkar.data.repository

import io.jawziyya.azkar.App
import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.data.network.core.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.*

/**
 * Created by uvays on 09.06.2022.
 */

class FileRepository(
    private val application: App,
    private val apiService: ApiService,
) {

    private val assetsCacheFileName: String = "ASSETS_CACHE_FILE_NAME"

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAudioFile(zikr: Zikr): File? = withContext(Dispatchers.IO) {
        if (zikr.audioFileName == null) {
            return@withContext null
        }

        val dir = File(application.cacheDir, "audio")
        dir.mkdirs()

        val file = File(dir, assetsCacheFileName)
        if (file.exists()) {
            file.delete()
        }

        try {
            val source = application.assets.open(zikr.audioFileName).source()
            val sink = file.sink().buffer()
            sink.writeAll(source)
            sink.close()
        } catch (e: Exception) {
            Timber.d(zikr.audioFileName)
            Timber.e(e)
            return@withContext null
        }

        return@withContext file
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAudioFile(url: String): File? {
        val dir = File(application.cacheDir, "audio")
        dir.mkdirs()

        val fileName = url.substringAfterLast("/")
        val file = File(dir, fileName)

        if (file.exists()) {
            return file
        }

        val response = apiService.getFile(url)

        try {
            val sink = file.sink().buffer()
            sink.writeAll(response.source())
            sink.close()
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

        return file
    }
}