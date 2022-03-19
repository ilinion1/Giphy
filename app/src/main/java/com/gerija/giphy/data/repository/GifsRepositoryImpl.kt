package com.gerija.giphy.data.repository


import com.gerija.giphy.data.api.ApiService
import com.gerija.giphy.data.api.dto.GifsContainer
import com.gerija.giphy.domain.GifsRepository
import javax.inject.Inject

class GifsRepositoryImpl @Inject constructor(private val api: ApiService): GifsRepository {

    override suspend fun getTopGifs(offset: Int): Result<GifsContainer> {
        return Result.success(api.getGifsAreTrending(offset = offset))
    }

    override suspend fun getSearchGifs(field: String, offset: Int): Result<GifsContainer> {
        return Result.success(api.getSearchGifs(field = field, offset = offset))
    }
}