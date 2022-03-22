package com.gerija.giphy.domain

import com.gerija.giphy.data.api.dto.Data
import javax.inject.Inject

class DeleteUseCase @Inject constructor(private val repository: GifsRepository) {
    operator fun invoke(data: Data) = repository.delete(data)
}