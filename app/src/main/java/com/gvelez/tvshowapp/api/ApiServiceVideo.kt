package com.gvelez.tvshowapp.api

import com.google.gson.JsonElement
import com.gvelez.tvshowapp.helper.Constants
import com.gvelez.tvshowapp.models.TvShowResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceVideo {

    @GET(Constants.END_POINT_VIDEO)
    suspend fun getVideoUrl(): Response<JsonElement>

}