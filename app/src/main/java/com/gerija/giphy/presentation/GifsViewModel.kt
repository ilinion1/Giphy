package com.gerija.giphy.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gerija.giphy.data.api.dto.GifsContainer
import com.gerija.giphy.data.repository.GifsRepositoryImpl
import kotlinx.coroutines.launch

class GifsViewModel(private val repository: GifsRepositoryImpl):  ViewModel() {

    private var topGifs = MutableLiveData<GifsContainer>()
    val _topGifs: LiveData<GifsContainer> get() = topGifs

    private var searchGigs = MutableLiveData<GifsContainer>()
    val _searchGifs: LiveData<GifsContainer> get() = searchGigs


    suspend fun getTopGifs(offset: Int){
        repository.getGifsAreTrending(offset).onSuccess{
            topGifs.value = it
        }
    }

    suspend fun getSearchGifs(field: String, offset: Int){
        repository.getSearchGifs(field, offset).onSuccess {
            searchGigs.value = it
        }
    }

}