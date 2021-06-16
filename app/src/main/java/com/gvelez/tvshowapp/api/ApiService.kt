package com.gvelez.tvshowapp.api

import com.gvelez.tvshowapp.helper.Constants
import com.gvelez.tvshowapp.models.TvShowResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET(Constants.END_POINT)
    suspend fun getTvShows():Response<TvShowResponse>

}