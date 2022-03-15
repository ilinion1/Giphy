package com.gerija.giphy.domain

import com.gerija.giphy.data.api.dto.GifsContainer

interface GigsRepository {

    suspend fun getGifsAreTrending(offset: Int): Result<GifsContainer>

    suspend fun getSearchGifs(field: String, offset: Int): Result<GifsContainer>
}