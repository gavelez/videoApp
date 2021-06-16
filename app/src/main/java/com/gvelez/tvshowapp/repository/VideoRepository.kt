package com.gvelez.tvshowapp.repository

import com.gvelez.tvshowapp.api.ApiServiceVideo
import javax.inject.Inject

class VideoRepository
@Inject
constructor(private val apiService: ApiServiceVideo) {
    suspend fun getVideoUrl() = apiService.getVideoUrl()
}