package com.gerija.giphy.data.api.dto

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Images(
    @Embedded
    @SerializedName("original") var original: Original? = Original()
) : Serializable