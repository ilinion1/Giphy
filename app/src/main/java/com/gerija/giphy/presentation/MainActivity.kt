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
import com.gerija.giphy.MyApplication
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity(), GifsAdapter.GifOnClick {


    lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var viewModelFactory: GifsViewModelFactory

    private val viewModel: GifsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GifsViewModel::class.java]
    }
    private val component by lazy {
        (application as MyApplication).component
    }

    lateinit var adapter: GifsAdapter
    private var searchText: String? = null
    private var nextClick = false
    private var page = 0
    private var offset = 20
    var madeScroll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        component.inject(this)
        setContentView(binding.root)

        startTopGifs() //получаю данные с репозитория, подписавшись на них
        startSearch() // загружаю данные с апи, для поиска и если пустая строка для топ
        secondStart() // вторая выгрузка данных, как дошло до конца скролла
        getDataViewModel() //подписываюсь на обновления данных с viewModel

    }

    /**
     * Подписываюсь на обновления данных с viewModel
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
     * Вторая выгрузка данных, как дошло до конца скролла
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
    private fun scrollUp(recyclerView: RecyclerView, newState: Int) {
        if (recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (page != 0) {
                binding.backPage.visibility = View.VISIBLE
                if (offset == 0){
                    binding.backPage.visibility = View.GONE
                }
                binding.backPage.setOnClickListener {
                    settingsScrollUp()

                    lifecycleScope.launch {
                        if ((searchText == null)) {
                            viewModel.getTopGifs(offset)
                            page--
                            binding.backPage.visibility = View.GONE

                        } else {
                            searchText?.let {
                                viewModel.getSearchGifs(it, offset)
                                page--
                                binding.backPage.visibility = View.GONE
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
            adapter.gifsList.addAll(data)
            binding.recyclerViewId.adapter = adapter
            binding.recyclerViewId.layoutManager = GridLayoutManager(this, 2)
            madeScroll = true
        } else {
            adapter.gifsList.addAll(data)
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
    override fun onClick(dataUrl: String?, gifsList: ArrayList<Data>, position: Int) {
        val intent = Intent(this, SingleGifActivity::class.java)
        intent.putExtra("gifUrl", dataUrl)
        intent.putExtra("gifsList", gifsList)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    override fun deleteItem(position: Int) {
        adapter.gifsList.removeAt(position)
        lifecycleScope.launch {
            adapter.notifyItemRemoved(position)
            delay(1000)
            adapter.notifyDataSetChanged()
        }
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
