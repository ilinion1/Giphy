package com.gerija.giphy.presentation

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.gerija.giphy.R
import com.gerija.giphy.data.api.ApiFactory
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.data.api.dto.GifsContainer
import com.gerija.giphy.data.repository.GifsRepositoryImpl
import com.gerija.giphy.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), GifsAdapter.GifOnClick {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: GifsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[GifsViewModel::class.java]


        startNextPage() //загрузка следующей страницы(и первой при открытии)

        startSearchGif() //запуск по поиску, при нажатии
        loadData() //получаю данные с viewModel, подписавшись на них

    }

    /**
     * Запуск адаптера
     */
    private fun startAdapter(data: List<Data>) {
        val adapter = GifsAdapter(this, this)
        adapter.gifsList.addAll(data)
        binding.recyclerViewId.adapter = adapter
        binding.recyclerViewId.layoutManager = GridLayoutManager(this, 2)
    }

    /**
     * Получаю данные с viewModel, подписываюсь на них
     */
    private fun loadData() {
        viewModel._topGigs.observe(this) {
            startAdapter(it.data)
            Log.d("MyLog", "${it.data}")
        }

        viewModel._searchGigs.observe(this) {
            startAdapter(it.data)
        }
    }

    /**
     * Интерфейс, открываю новую активити с нажатой гифкой
     */
    override fun onClick(data: Data, gifsList: ArrayList<Data>, position: Int) {
        val intent = Intent(this, SingleGifActivity::class.java)
        val gifUrl = data.images.original.url
        intent.putExtra("gifUrl", gifUrl)
        intent.putExtra("gifsList", gifsList)
        intent.putExtra("position", position)
        startActivity(intent)

    }

    /**
     * загрузка следующей страницы
     */
    private fun startNextPage() = with(binding) {

        lifecycleScope.launch {
            viewModel.getTopGifs(0)

            tvPage1.setOnClickListener {
                startPage(it as TextView,0)
            }

            tvPage2.setOnClickListener {
                startPage(it as TextView,26)
            }

            tvPage3.setOnClickListener {
                startPage(it as TextView,52)
            }
        }


    }

    /**
     * Запускаю необходимый контект в gif, в зависимости от того, указано в поиске что-то или нет
     * Запускаю внутри функцию для изменения цвета страниц
     */
    private fun startPage(tv: TextView, offset: Int){
        setColorPage(tv)

        lifecycleScope.launch {
            if (binding.edSearch.text.isEmpty()) {
                viewModel.getTopGifs(offset)
            } else {
                viewModel.getSearchGifs(binding.edSearch.text.toString(), offset)
            }
        }
    }

    /**
     * Запускаю по клику на поиск запись гифок по запросу во ViewModel
     */
    private fun startSearchGif() = with(binding) {
        imSerch.setOnClickListener {
            imSerch.setImageResource(R.drawable.ic_search_pressed)
            if (edSearch.text.isNotEmpty()) {
                tvPage1.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.teal_300))
                tvPage2.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                tvPage3.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                lifecycleScope.launch {
                    viewModel.getSearchGifs(edSearch.text.toString(), 0)
                    imSerch.setImageResource(R.drawable.ic_search_normal)
                }
            } else {
                lifecycleScope.launch {
                    delay(200)
                    Toast.makeText(this@MainActivity, "Enter text into search", Toast.LENGTH_LONG).show()
                    imSerch.setImageResource(R.drawable.ic_search_normal)
                }

            }
        }
    }

    /**
     * Задаю цвет страницам
     */
    private fun setColorPage(textView: TextView) {
        when (textView) {
            binding.tvPage1 -> {
                textView.setTextColor(ContextCompat.getColor(this, R.color.teal_300))
                binding.tvPage2.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.tvPage3.setTextColor(ContextCompat.getColor(this, R.color.black))

            }
            binding.tvPage2 -> {
                textView.setTextColor(ContextCompat.getColor(this, R.color.teal_300))
                binding.tvPage1.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.tvPage3.setTextColor(ContextCompat.getColor(this, R.color.black))

            }
            binding.tvPage3 -> {
                textView.setTextColor(ContextCompat.getColor(this, R.color.teal_300))
                binding.tvPage1.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.tvPage2.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }
    }
}