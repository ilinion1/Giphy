package com.gerija.giphy.data.repository


import com.gerija.giphy.data.api.ApiService
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.data.database.GifsDao
import com.gerija.giphy.domain.GifsRepository
import javax.inject.Inject

class GifsRepositoryImpl @Inject constructor(private val api: ApiService, private val dao: GifsDao)
    : GifsRepository {

    /**
     * Получаю рейтинговые gif
     */
    override suspend fun getTopGifs(offset: Int): List<Data> {
        return dao.readData(offset)
    }

    /**
     * Получаю gif по запросу поиска
     */
    override suspend fun getSearchGifs(searchQuery: String, offset: Int): List<Data> {
        return dao.searchDataBase(searchQuery, offset)
    }

    /**
     * Удаляю с базы gif
     */
    override fun delete(data: Data) {
        dao.delete(data)
    }

    /**
     * Загружаю данные с Api в базу
     * В бесплатной версии Api лимит 50шт за раз, делаю через цикл, максимум дает 3950шт
     */
    override suspend fun loadData() {
        var count = 0
        while (true){
            dao.insertData(api.getGifsAreTrending(offset = count).data)
            count += 50
            if (count == 3950) break
        }
    }
}