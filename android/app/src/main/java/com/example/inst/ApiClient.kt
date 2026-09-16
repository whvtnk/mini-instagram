package com.example.inst // Өзіңнің пакетің

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // ӨЗІҢНІҢ RENDER-ДЕГІ СІЛТЕМЕҢДІ ЖАЗАМЫЗ
    private const val BASE_URL = "http://10.0.2.2:8000/"
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON-ды аудару үшін
            .build()
            .create(ApiService::class.java)
    }
}