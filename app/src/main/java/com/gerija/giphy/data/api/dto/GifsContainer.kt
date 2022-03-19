package com.gerija.giphy.data.api.dto

import com.google.gson.annotations.SerializedName

data class GifsContainer(
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()
)