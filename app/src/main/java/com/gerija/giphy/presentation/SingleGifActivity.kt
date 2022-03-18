package com.gerija.giphy.presentation

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gerija.giphy.R
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.databinding.ActivitySingleGifBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SingleGifActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleGifBinding
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var gifsListContainer: ArrayList<List<Data>>
    private val gifsList = ArrayList<Data>()
    private var position = 0

    companion object {
        const val MIN_DISTANCE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleGifBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startAndSetIntents()

        gestureDetector = GestureDetectorCompat(this, GestureListener())
        binding.imLogo.setOnClickListener {
            finish()
        }
    }

    /**
     * Получаю интент и задаю их значение
     */
    private fun startAndSetIntents(){
        val gifUrlIntent = intent.getStringExtra("gifUrl").toString()
        Glide.with(this).load(gifUrlIntent).into(binding.imSingleGif)
        position = intent.getIntExtra("position", 0)
        gifsListContainer = intent.getSerializableExtra("gifsList") as ArrayList<List<Data>>
        gifsListContainer.forEach {
            gifsList.addAll(it)
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
                    if (position != 0) {
                        position--
                        binding.imLeft.setImageResource(R.drawable.ic_left_pressed)
                    }
                    Glide.with(applicationContext).load(gifsList[position].images.original.url)
                        .into(binding.imSingleGif)
                    //ставлю 2 мил/сек задержку, что бы отобразить изменение цвета на стрелке, при клике
                    lifecycleScope.launch {
                        delay(200)
                        binding.imLeft.setImageResource(R.drawable.ic_left_normal)
                    }
                } else {
                    //увеличиваю позицию с которой будет браться gif и меняю цвет стрелки дял эффекта
                    if (position < gifsList.size - 1) {
                        position++
                        binding.imRight.setImageResource(R.drawable.ic_right_pressed)
                    }
                    Glide.with(applicationContext).load(gifsList[position].images.original.url)
                        .into(binding.imSingleGif)
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