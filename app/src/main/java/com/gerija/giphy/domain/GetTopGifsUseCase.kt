package com.gerija.giphy.domain



import javax.inject.Inject

class GetTopGifsUseCase @Inject constructor(private val repository: GifsRepository) {

    suspend operator fun invoke(offset: Int) =  repository.getTopGifs(offset)
}