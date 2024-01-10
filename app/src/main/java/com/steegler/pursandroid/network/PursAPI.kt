package com.steegler.pursandroid.network

import com.steegler.pursandroid.Constants
import com.steegler.pursandroid.LocationItemResponse
import retrofit2.http.GET


interface PursAPI {

    @GET(Constants.FILE_PATH)
    suspend fun getData(): LocationItemResponse
}