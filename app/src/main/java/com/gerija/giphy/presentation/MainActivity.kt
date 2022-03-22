package com.gerija.giphy.presentation

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
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
    private val component by lazy { (application as MyApplication).component }

    lateinit var adapter: GifsAdapter
    private var searchText: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        component.inject(this)
        setContentView(binding.root)
        adapter = GifsAdapter(this, this)
        binding.recyclerViewId.adapter = adapter

        setLayoutManager() //Задаю LayoutManager в зависимости от ориентации на телефоне
        setFromApiTopInViewModel() //получаю данные с репозитория, подписавшись на них
        getFieldSearch() // загружаю данные с апи, для поиска и если пустая строка для топ
        scrollEnd() // вторая выгрузка данных, как дошло до конца скролла
        getDataViewModel() //подписываюсь на обновления данных с viewModel
    }

    /**
     * Задаю LayoutManager в зависимости от ориентации на телефоне
     */
    private fun setLayoutManager() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.recyclerViewId.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.recyclerViewId.layoutManager = GridLayoutManager(this, 3)
        }
    }

    /**
     * Запуск адаптера
     */
    private fun startAdapter(data: List<Data>) {
        adapter.gifsList.addAll(data)
        adapter.notifyDataSetChanged()
    }

    /**
     * Задаю с какой позиции хочу получить данные при первом входе
     */
    private fun setFromApiTopInViewModel() {
        if (viewModel.firstVisitMyAct) {
            lifecycleScope.launch {
                delay(3000)
                viewModel.getTopGifs(viewModel.offsetTopMyAct)
                binding.progressBar2.visibility = View.GONE
                viewModel.firstVisitMyAct = false
            }
        } else {
            binding.progressBar2.visibility = View.GONE
            lifecycleScope.launch {
                delay(300)
                adapter.gifsList.clear()
                startAdapter(viewModel.gifsListMyAct)
            }
        }
    }

    /**
     * Подписываюсь на обновления данных с viewModel
     */
    private fun getDataViewModel() {
        viewModel._topGifs.observe(this) {
            viewModel.gifsListMyAct.addAll(it)
            it.forEach {
                viewModel.offsetTopMyAct = it.key
            }
            startAdapter(it)
        }
        viewModel._searchGifs.observe(this) {
            startAdapter(it)
        }
    }

    /**
     * Передаю данные полученные с поля ввода searchVIew во viewModel с нужной позиции
     */
    private fun getFieldSearch() = with(binding) {
        searchId.isSubmitButtonEnabled = true

        searchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                lifecycleScope.launch {

                    if (p0.isEmpty()) {
                        searchText = null
                        adapter.gifsList.clear()
                        viewModel.offsetSearchMyAct = 0
                        cleanSearch()
                    } else {
                        adapter.gifsList.clear()
                        searchText = p0
                        viewModel.getSearchGifs(p0, viewModel.offsetSearchMyAct)
                    }
                }
                return true
            }
        })
    }

    /**
     * Показывает кнопки "nextPage", "backPage" как дошло до конца скролла
     */
    private fun scrollEnd() {
        binding.recyclerViewId.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    nextPage()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun cleanSearch() {
        lifecycleScope.launch {
            delay(600)
            binding.nextPage.visibility = View.VISIBLE
        }
        binding.nextPage.setOnClickListener {
            viewModel.getTopGifs(0)
            lifecycleScope.launch {
                settingsClickNextPage()
            }
        }
    }

    /**
     * Слушатель клика при нажатии на кнопку "nextPage"
     */
    private fun nextPage() {
        binding.nextPage.visibility = View.VISIBLE
        binding.nextPage.setOnClickListener {
            lifecycleScope.launch {
                if (searchText == null) {
                    viewModel.getTopGifs(viewModel.offsetTopMyAct)
                    settingsClickNextPage()
                } else {
                    searchText?.let { it ->
                        adapter.gifsList.forEach { adkey ->
                            viewModel.offsetSearchMyAct = adkey.key
                        }
                        viewModel.getSearchGifs(it, viewModel.offsetSearchMyAct)
                        settingsClickNextPage()
                    }
                }
            }
        }
    }

    /**
     * При возврате с SingleGifActivity обновляет activity,что бы отобразить актуальные данные с базы
     */
    override fun onRestart() {
        super.onRestart()
        intent = intent
        finish()
        startActivity(intent)
    }

    /**
     * Задаю настройки с какой позиции взять данные при нажатии на следующую страницу
     */
    private suspend fun settingsClickNextPage() {
        binding.nextPage.visibility = View.GONE
        binding.progressBarId.visibility = View.VISIBLE
        delay(1000)
        binding.progressBarId.visibility = View.GONE
    }

    /**
     * Реализация интерфейса с адаптера, открываю новую активити с нажатой гифкой
     */
    override fun onClick(gifsList: ArrayList<Data>, position: Int) {
        val intent = Intent(this, SingleGifActivity::class.java)
        intent.putExtra("gifsList", gifsList)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    /**
     * Реализация интерфейса с адаптера, удаляю с активити гифку
     */
    override fun deleteItem(position: Int) {
        viewModel.deleteItem(adapter.gifsList[position])
        adapter.gifsList.removeAt(position)
        lifecycleScope.launch {
            adapter.notifyItemRemoved(position)
            delay(1000)
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Сохраняю данные которые были в адаптаре перед разрушением, после задам их снова
     */
    override fun onDestroy() {
        super.onDestroy()
        viewModel.gifsListMyAct = adapter.gifsList
    }
}
