package com.gerija.giphy.data.api.dto


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(
    @SerializedName("id") var id: String? = null,
    @SerializedName("images") var images: Images? = Images()
): Serializable