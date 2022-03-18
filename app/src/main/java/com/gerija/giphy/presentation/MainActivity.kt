package com.gerija.giphy.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.data.repository.GifsRepositoryImpl
import com.gerija.giphy.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), GifsAdapter.GifOnClick {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: GifsViewModel
    private var searchText: String? = null
    lateinit var adapter: GifsAdapter
    lateinit var layoutManager: GridLayoutManager
    private var nextClick = false
    private var page = 0
    private var offset = 20
    var madeScroll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = GifsViewModelFactory(GifsRepositoryImpl())
        viewModel = ViewModelProvider(this, factory)[GifsViewModel::class.java]

        startTopGifs() //получаю данные с репозитория, подписавшись на них
        startSearch() // загружаю данные с апи, для поиска и если пустая строка для топ
        getDataViewModel() //подписываюсь на получение данных с viewModel
        secondStart() //прослушиваю скролл и когда долистал до конца

    }

    /**
     * Подписываюсь на получение данных с viewModel
     */
    private fun getDataViewModel(){
        viewModel._searchGifs.observe(this) {
            startAdapter(it.data)
        }
        viewModel._topGifs.observe(this) {
            startAdapter(it.data)
        }
    }

    /**
     * Прослушиваю скрол и когда долистал до конца
     */
    private fun secondStart() {
        binding.recyclerViewId.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                scrollDown(recyclerView, newState)
                scrollUp(recyclerView, newState)
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    /**
     * При скролле вниз показываю кнопку перейти на следующую страницу и реализация
     */
    private fun scrollDown(recyclerView: RecyclerView, newState: Int) {
        if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
            binding.nextPage.visibility = View.VISIBLE
            binding.nextPage.setOnClickListener {
                lifecycleScope.launch {
                    if (offset == 0) {
                        offset = 20
                    }
                    if ((searchText == null)) {
                        viewModel.getTopGifs(offset)
                        settingsScrollDown()

                    } else {
                        searchText?.let {
                            viewModel.getSearchGifs(it, offset)
                            settingsScrollDown()
                        }
                    }
                }
            }
        }
    }

    /**
     * Задаю настройки для правильного отображения данных в страницу при скроле вниз
     */
    private fun settingsScrollDown() {
        offset += 20
        nextClick = true
        binding.nextPage.visibility = View.GONE
        page++
    }

    /**
     * При скролле вверх показываю кнопку вернуться на предыдущую страницу и реализация
     */
    private fun scrollUp(recyclerView: RecyclerView, newState: Int) = with(binding) {
        if (recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (page != 0) {
                backPage.visibility = View.VISIBLE
                if (offset == 0){
                    backPage.visibility = View.GONE
                }
                backPage.setOnClickListener {
                    settingsScrollUp()

                    lifecycleScope.launch {
                        if ((searchText == null)) {
                            viewModel.getTopGifs(offset)
                            page--
                            backPage.visibility = View.GONE

                        } else {
                            searchText?.let {
                                viewModel.getSearchGifs(it, offset)
                                page--
                                backPage.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Проверка какие какие данные показать на странице при скролле вверх
     */
    private fun settingsScrollUp() {
        if (offset == 40) {
            offset = 0
        } else {
            if (nextClick) {
                offset -= 40
                nextClick = false
            } else {
                offset -= 20
            }
        }
    }

    /**
     * Запуск адаптера
     */
    private fun startAdapter(data: List<Data>) {
        adapter = GifsAdapter(this, this)
        if (!madeScroll) {
            adapter.submitList(data)
            binding.recyclerViewId.adapter = adapter
            layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewId.layoutManager = layoutManager
            madeScroll = true
        } else {
            adapter.submitList(data)
            binding.recyclerViewId.adapter = adapter
        }
    }

    /**
     * Получаю данные с viewModel, подписываюсь на них
     */
    private fun startTopGifs() {
        lifecycleScope.launch {
            viewModel.getTopGifs(0)
        }
    }

    /**
     * Интерфейс, открываю новую активити с нажатой гифкой
     */
    override fun onClickItem(data: Data, gifsList: List<Data>, position: Int) {
        val intent = Intent(this, SingleGifActivity::class.java)
        val gifUrl = data.images.original.url
        val gifsListContainer = ArrayList<List<Data>>()
        gifsListContainer.add(gifsList)
        intent.putExtra("gifUrl", gifUrl)
        intent.putExtra("gifsList", gifsListContainer)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    /**
     * Передаю в Api запрос поиска
     */
    private fun startSearch() = with(binding) {

        searchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                    if (nextClick) {
                        setSearchField(p0, offset)
                    } else {
                        setSearchField(p0, 0)
                    }
                return true
            }
        })
    }

    /**
     * Задаю данные в адаптер, полученные с поля поиска
     */
    private fun setSearchField(p0: String, offset: Int){
        lifecycleScope.launch {
            if (p0.isEmpty()) {
                searchText = null
                viewModel.getTopGifs(offset)
            } else {
                searchText = p0
                viewModel.getSearchGifs(p0, offset)
            }
        }
    }
}



