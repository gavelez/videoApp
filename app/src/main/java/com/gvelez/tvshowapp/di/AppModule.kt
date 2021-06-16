package com.gvelez.tvshowapp.di

import com.gvelez.tvshowapp.api.ApiService
import com.gvelez.tvshowapp.api.ApiServiceVideo
import com.gvelez.tvshowapp.helper.Constants
import com.gvelez.tvshowapp.helper.Constants.BASE_URL
import com.gvelez.tvshowapp.helper.Constants.BASE_URL_VIDEO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): ApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRetrofitVideoInstance(): ApiServiceVideo =
        Retrofit.Builder()
            .baseUrl(BASE_URL_VIDEO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceVideo::class.java)

}