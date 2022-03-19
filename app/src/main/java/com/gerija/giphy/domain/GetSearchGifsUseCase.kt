package com.gerija.giphy.domain


import javax.inject.Inject

class GetSearchGifsUseCase @Inject constructor(private val repository: GifsRepository) {

    suspend operator fun invoke(field: String,offset: Int) = repository.getSearchGifs(field, offset)
}