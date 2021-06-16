package com.gvelez.tvshowapp.models

import retrofit2.http.Url


data class TvShowItem(
    val id: Int,
    val image: Image,
    val name: String,
    val url: String
)