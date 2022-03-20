package com.gerija.giphy.presentation

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            binding.recyclerViewId.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.recyclerViewId.layoutManager = GridLayoutManager(this, 3)
        }

        setFromApiTopInViewModel() //получаю данные с репозитория, подписавшись на них
        getFieldSearch() // загружаю данные с апи, для поиска и если пустая строка для топ
        scrollEnd() // вторая выгрузка данных, как дошло до конца скролла
        getDataViewModel() //подписываюсь на обновления данных с viewModel
    }

    /**
     * Запуск адаптера
     */
    private fun startAdapter(data: ArrayList<Data>) {
        adapter.gifsList.addAll(data)
        adapter.notifyDataSetChanged()
    }

    /**
     * Задаю данные полученные с Api топ gif во viewModel
     */
    private fun setFromApiTopInViewModel() {
        if (viewModel.firstVisitMyAct){
            lifecycleScope.launch {
                viewModel.getTopGifs(0)
                viewModel.firstVisitMyAct = false
            }
        } else {
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
            viewModel._searchGifs.observe(this) {
            startAdapter(it.data)
            }
            viewModel._topGifs.observe(this) {
            startAdapter(it.data)
            }
    }

    /**
     * Передаю данные полученные с поля ввода searchVIew во viewModel с нужной позиции
     */
    private fun getFieldSearch() = with(binding) {

        searchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                lifecycleScope.launch {

                    if (p0.isEmpty()) {
                        searchText = null
                        adapter.gifsList.clear()
                        viewModel.getTopGifs(0)
                    } else {
                        adapter.gifsList.clear()
                        searchText = p0
                        viewModel.getSearchGifs(p0, 0)
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

    /**
     * Слушатель клика при нажатии на кнопку "nextPage"
     */
    private fun nextPage() {
        binding.nextPage.visibility = View.VISIBLE
        binding.nextPage.setOnClickListener {
            lifecycleScope.launch {
                if (viewModel.offsetMyAct == 0) {
                    viewModel.offsetMyAct = 20
                }
                if (searchText == null) {
                    viewModel.getTopGifs(viewModel.offsetMyAct)
                    settingsClickNextPage()

                } else {
                    searchText?.let {
                        viewModel.getSearchGifs(it, viewModel.offsetMyAct)
                        settingsClickNextPage()
                    }
                }
            }
        }
    }

    /**
     * Задаю настройки с какой позиции взять данные при нажатии на следующую страницу
     */
    private suspend fun settingsClickNextPage() {
        viewModel.offsetMyAct += 20
        viewModel.nextClickMyAct = true
        binding.nextPage.visibility = View.GONE
        binding.progressBarId.visibility = View.VISIBLE
        delay(1000)
        binding.progressBarId.visibility = View.GONE
        viewModel.pageMyAct++
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
        adapter.gifsList.removeAt(position)
        lifecycleScope.launch {
            adapter.notifyItemRemoved(position)
            delay(1000)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.gifsListMyAct = adapter.gifsList
    }
}
