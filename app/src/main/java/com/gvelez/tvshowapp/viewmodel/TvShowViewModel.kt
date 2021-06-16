package com.gvelez.tvshowapp.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.gvelez.tvshowapp.PlayerActivity
import com.gvelez.tvshowapp.adapter.TvShowAdapter
import com.gvelez.tvshowapp.helper.MyApplication
import com.gvelez.tvshowapp.models.TvShowItem
import com.gvelez.tvshowapp.repository.TvShowRepository
import com.gvelez.tvshowapp.repository.VideoRepository
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowViewModel
@Inject
constructor(
    private val repository: TvShowRepository,
    private val videoRepository: VideoRepository,
    @ApplicationContext private val context: Context
) : ViewModel(),
    TvShowAdapter.ItemListener {

    private val _response = MutableLiveData<List<TvShowItem>>()
    val responseTvShow: LiveData<List<TvShowItem>>
        get() = _response

    init {
        getAllTvShows()
    }

    private fun getAllTvShows() = viewModelScope.launch {
        repository.getTvShows().let { response ->

            if (response.isSuccessful) {
                _response.postValue(response.body())
            } else {
                Log.d("tag", "getAllTvShows Error: ${response.code()}")
            }
        }
    }

    private fun launchVideo(videoUrl: String = "") {
        val intent = Intent(context, PlayerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ContextCompat.startActivity(context, intent, null)
    }

    override fun onItemClick(view: View?) {
        launchVideo()
    }


}













