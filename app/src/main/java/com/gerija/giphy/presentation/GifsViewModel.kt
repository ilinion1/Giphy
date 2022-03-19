package com.gerija.giphy.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.data.api.dto.GifsContainer
import com.gerija.giphy.domain.GetSearchGifsUseCase
import com.gerija.giphy.domain.GetTopGifsUseCase
import javax.inject.Inject

class GifsViewModel @Inject constructor(
    private val getTopGifsUseCase: GetTopGifsUseCase,
    private val getSearchGifsUseCase: GetSearchGifsUseCase
) : ViewModel() {

    private var topGifs = MutableLiveData<GifsContainer>()
    val _topGifs: LiveData<GifsContainer> get() = topGifs

    private var searchGigs = MutableLiveData<GifsContainer>()
    val _searchGifs: LiveData<GifsContainer> get() = searchGigs

    var mainUrl = MutableLiveData<String?>()
    var gifList1 = MutableLiveData<ArrayList<Data>>()
    var position1 = MutableLiveData<Int>()

    suspend fun getTopGifs(offset: Int) {
        getTopGifsUseCase(offset).onSuccess {
            topGifs.value = it
        }
    }

    suspend fun getSearchGifs(field: String, offset: Int) {
        getSearchGifsUseCase(field, offset).onSuccess {
            searchGigs.value = it
        }
    }
}
