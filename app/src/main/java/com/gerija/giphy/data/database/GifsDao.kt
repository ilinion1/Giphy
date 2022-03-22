package com.gerija.giphy.data.database



import androidx.room.*
import com.gerija.giphy.data.api.dto.Data


@Dao
interface GifsDao {

    @Query("Select * From gifs_table WHERE `key` > :offset LIMIT 20")
    fun readData(offset: Int): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(gifsList: List<Data>)

    @Delete
    fun delete(data: Data)

    @Query("Select Distinct * FROM gifs_table WHERE title LIKE :searchQuery AND `key` > :offset LIMIT 20")
    fun searchDataBase(searchQuery: String, offset: Int): List<Data>

}