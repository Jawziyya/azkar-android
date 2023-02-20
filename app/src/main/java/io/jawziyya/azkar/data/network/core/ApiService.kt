package io.jawziyya.azkar.data.network.core

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Created by uvays on 09.06.2022.
 */

interface ApiService {

    @Streaming
    @GET
    suspend fun getFile(@Url url: String): ResponseBody
}