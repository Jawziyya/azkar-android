package io.jawziyya.azkar

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import io.jawziyya.azkar.data.datasource.AzkarDataSource
import io.jawziyya.azkar.data.mapper.AzkarResponseToModelMapper
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.network.core.ApiService
import io.jawziyya.azkar.data.repository.AzkarRepository
import io.jawziyya.azkar.data.repository.FileRepository
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by uvays on 05.06.2022.
 */

class App : Application() {

    companion object {
        lateinit var instance: App

        private const val CONNECTION_TIMEOUT_MILLIS = 20000L
        private const val READ_TIMEOUT_MILLIS = 20000L
        private const val WRITE_TIMEOUT_MILLIS = 20000L
    }

    lateinit var globalServices: GlobalServices

    override fun onCreate() {
        super.onCreate()

        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (!this::globalServices.isInitialized) {
            val moshi = createMoshi()
            val okHttpClient = createOkHttpClient(
                interceptors = listOf(createHttpLoggingInterceptor())
            )
            val apiService = createApiService(client = okHttpClient)
            val azkarDataSource = AzkarDataSource(resources, moshi)

            globalServices =
                GlobalServices
                    .builder()
                    .rebind<Application>(this)
                    .add(resources)
                    .add(moshi)
                    .add(apiService)
                    .add(AzkarRepository(azkarDataSource, AzkarResponseToModelMapper()))
                    .add(FileRepository(this, apiService))
                    .build()
        }
    }

    private fun createApiService(client: OkHttpClient): ApiService {
        return Retrofit
            .Builder()
            .client(client)
            .baseUrl("https://azkar.ru/")
            .build()
            .create(ApiService::class.java)
    }

    private fun createMoshi(): Moshi {
        return Moshi
            .Builder()
            .build()
    }

    private fun createOkHttpClient(interceptors: List<Interceptor>): OkHttpClient {
        val builder = OkHttpClient
            .Builder()
            .connectTimeout(CONNECTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)

        builder.interceptors() += interceptors

        return builder.build()
    }

    private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return interceptor
    }
}