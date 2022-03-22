package com.gerija.giphy.domain

import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.data.api.dto.GifsContainer

interface GifsRepository {

    suspend fun getTopGifs(offset: Int): List<Data>

    suspend fun getSearchGifs(searchQuery: String, offset: Int): List<Data>

    suspend fun loadData()

    fun delete(data: Data)

}