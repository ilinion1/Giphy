package com.gerija.giphy.data.repository

import com.gerija.giphy.data.api.ApiFactory
import com.gerija.giphy.data.api.dto.GifsContainer
import com.gerija.giphy.domain.GigsRepository

class GifsRepositoryImpl: GigsRepository {

    override suspend fun getGifsAreTrending(offset: Int): Result<GifsContainer> {
        return Result.success(ApiFactory.create().getGifsAreTrending(offset = offset))
    }

    override suspend fun getSearchGifs(field: String, offset: Int): Result<GifsContainer> {
        return Result.success(ApiFactory.create().getSearchGifs(field = field, offset = offset))
    }
}