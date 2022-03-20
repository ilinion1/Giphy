package com.gerija.giphy.presentation

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gerija.giphy.R
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.databinding.ActivitySingleGifBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class SingleGifActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleGifBinding
    private lateinit var gestureDetector: GestureDetectorCompat

    @Inject
    lateinit var viewModelFactory: GifsViewModelFactory

    private val viewModel: GifsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GifsViewModel::class.java]
    }
    private val component by lazy {
        (application as MyApplication).component
    }

    companion object {
        const val MIN_DISTANCE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleGifBinding.inflate(layoutInflater)
        component.inject(this)
        setContentView(binding.root)
        startAndSetIntents() //получаю и устанавливаю значение из intent
        deleteGifs() //слушатель на кнопку удаление gif

        gestureDetector = GestureDetectorCompat(this, GestureListener())
        binding.imLogo.setOnClickListener { finish() }
    }


    /**
     * Получаю интент и задаю их значение
     */
    private fun startAndSetIntents(){
        if (viewModel.firstVisitSingleAct){
            viewModel.gifsPositionSingleAct = intent.getIntExtra("position", 0)
            viewModel.gifsListSingleAct = intent.getSerializableExtra("gifsList") as ArrayList<Data>
            Picasso.get().load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
                .images?.original?.url).into(binding.imSingleGif)
//            Glide.with(this).load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
//                .images?.original?.url).into(binding.imSingleGif)
            viewModel.firstVisitSingleAct = false
        } else {
            Picasso.get().load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
                .images?.original?.url).into(binding.imSingleGif)
//            Glide.with(this).load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
//                .images?.original?.url).into(binding.imSingleGif)
        }
    }

    /**
     * Устанавливаю слушатель на уделание картинки
     */
    private fun deleteGifs(){
        binding.imageDelete.setOnClickListener {
            viewModel.gifsListSingleAct.removeAt(viewModel.gifsPositionSingleAct)
            binding.imageDelete.setImageResource(R.drawable.ic_delete_precced)
            lifecycleScope.launch {
                delay(200)
                Picasso.get().load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
                    .images?.original?.url).into(binding.imSingleGif)
//                Glide.with(this@SingleGifActivity).load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
//                    .images?.original?.url).into(binding.imSingleGif)
                binding.imageDelete.setImageResource(R.drawable.ic_delete_normal)
            }
        }
    }

    /**
     * Класс для слайдера
     */
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        /**
         * Действия при скроле
         */
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            val diffX = e2.x - e1.x
            if (Math.abs(diffX) > MIN_DISTANCE) {

                if (e2.x > e1.x) {
                    //уменьшаю позицию с которой будет браться gif и меняю цвет стрелки для эффекта
                    if (viewModel.gifsPositionSingleAct != 0) {
                        viewModel.gifsPositionSingleAct--
                        binding.imLeft.setImageResource(R.drawable.ic_left_pressed)
                    }
                    Picasso.get().load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
                        .images?.original?.url).into(binding.imSingleGif)

//                            Glide.with(this@SingleGifActivity).load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
//                        .images?.original?.url).into(binding.imSingleGif)
                    //ставлю 2 мил/сек задержку, что бы отобразить изменение цвета на стрелке, при клике
                    lifecycleScope.launch {
                        delay(200)
                        binding.imLeft.setImageResource(R.drawable.ic_left_normal)
                    }
                } else {
                    //увеличиваю позицию с которой будет браться gif и меняю цвет стрелки дял эффекта
                    if (viewModel.gifsPositionSingleAct < viewModel.gifsListSingleAct.size - 1) {
                        viewModel.gifsPositionSingleAct++
                        binding.imRight.setImageResource(R.drawable.ic_right_pressed)
                    }
                    Picasso.get().load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
                        .images?.original?.url).into(binding.imSingleGif)
//                    Glide.with(this@SingleGifActivity).load(viewModel.gifsListSingleAct[viewModel.gifsPositionSingleAct]
//                        .images?.original?.url).into(binding.imSingleGif)
                    //ставлю 2 мил/сек задержку, что бы отобразить изменение цвета на стрелке, при клике
                    lifecycleScope.launch {
                        delay(200)
                        binding.imRight.setImageResource(R.drawable.ic_right_normal)
                    }
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}