package com.steegler.pursandroid.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.steegler.pursandroid.Constants
import com.steegler.pursandroid.Playground
import com.steegler.pursandroid.network.PursAPI
import com.steegler.pursandroid.repo.PursRepo
import com.steegler.pursandroid.repo.PursRepoImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


interface DependencyProvider {

    val api: PursAPI
    val localStorage: PursAPI

    val repo: PursRepo
    val localRepo: PursRepo

    companion object {
        val instance: DependencyProvider = DependencyProviderImpl
    }

    object DependencyProviderImpl : DependencyProvider {


        override val api: PursAPI by lazy {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val contentType = "application/json".toMediaType()
            Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(Json.asConverterFactory(contentType))
                .build()
                .create(PursAPI::class.java)
        }

        override val repo: PursRepo by lazy { PursRepoImpl(api) }


        override val localStorage: PursAPI by lazy { Playground() }
        override val localRepo: PursRepo by lazy { localStorage as PursRepo }


    }
}