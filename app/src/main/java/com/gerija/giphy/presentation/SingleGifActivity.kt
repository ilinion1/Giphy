package com.gerija.giphy.presentation

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gerija.giphy.R
import com.gerija.giphy.data.api.dto.Data
import com.gerija.giphy.databinding.ActivitySingleGifBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class SingleGifActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    lateinit var binding: ActivitySingleGifBinding
    lateinit var gestureDetector: GestureDetector
    private var position = 0

    var x1: Float = 0.0f
    var x2: Float = 0.0f

    companion object {
        const val MIN_DISTANCE = 150
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleGifBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gifUrlIntent = intent.getStringExtra("gifUrl").toString()
        position = intent.getIntExtra("position", 0)

        Glide.with(this).load(gifUrlIntent).into(binding.imSingleGif)

        gestureDetector = GestureDetector(this, this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event?.action) {
            0 -> {
                x1 = event.x
            }
            1 -> {
                x2 = event.x
                val valueX: Float = x2 - x1

                if (abs(valueX) > MIN_DISTANCE) {
                    val gifsList = intent.getSerializableExtra("gifsList") as ArrayList<Data>

                    if (x2 > x1) {
                        if (position != 0) {
                            position--
                            binding.imLeft.setImageResource(R.drawable.ic_left_pressed)
                        }
                        val leftSwipe = gifsList[position]
                        Glide.with(this).load(leftSwipe.images.original.url)
                            .into(binding.imSingleGif)
                        lifecycleScope.launch {
                            delay(200)
                            binding.imLeft.setImageResource(R.drawable.ic_left_normal)
                        }
                        Log.d("gifsList", "$position")
                    } else {
                        if (position < gifsList.size -1) {
                            position++
                            binding.imRight.setImageResource(R.drawable.ic_right_pressed)
                        }
                        val rightSwipe = gifsList[position]
                        Glide.with(this).load(rightSwipe.images.original.url)
                            .into(binding.imSingleGif)
                        lifecycleScope.launch {
                            delay(200)
                            binding.imRight.setImageResource(R.drawable.ic_right_normal)
                        }
                        Log.d("gifsList", "$position")
                        Log.d("gifsList", "${gifsList.size}")
                    }
                }
            }
        }

        return super.onTouchEvent(event)
    }


    override fun onDown(p0: MotionEvent?): Boolean {
//        TODO("Not yet implemented")
        return false
    }

    override fun onShowPress(p0: MotionEvent?) {
//        TODO("Not yet implemented")
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
//        TODO("Not yet implemented")
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
//        TODO("Not yet implemented")
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
//        TODO("Not yet implemented")
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
//        TODO("Not yet implemented")
        return false
    }

}