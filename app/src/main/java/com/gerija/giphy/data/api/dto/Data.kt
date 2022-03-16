package com.gerija.giphy.data.api.dto


import java.io.Serializable

data class Data(
    val id: String,
    val images: Images
): Serializable