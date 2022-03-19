package com.gerija.giphy.data.api.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Images(
    @SerializedName("original") var original: Original? = Original()
) : Serializable