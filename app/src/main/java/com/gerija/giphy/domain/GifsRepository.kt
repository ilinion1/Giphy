package com.gerija.giphy.domain

import com.gerija.giphy.data.api.dto.GifsContainer

interface GifsRepository {

    suspend fun getTopGifs(offset: Int): Result<GifsContainer>

    suspend fun getSearchGifs(field: String, offset: Int): Result<GifsContainer>
}