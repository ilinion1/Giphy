package com.gerija.giphy.data.api.dto


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "gifs_table", indices = [Index(value = ["id"], unique = true)])
data class Data(
    @SerializedName("id") var id: String,
    @PrimaryKey(autoGenerate = true) var key: Int = 0,
    @Embedded
    @SerializedName("images") var images: Images? = Images(),
    @SerializedName("title") var title: String? = null
): Serializable
