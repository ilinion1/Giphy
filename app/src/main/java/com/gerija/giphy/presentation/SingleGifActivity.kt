package com.gerija.giphy.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.gerija.giphy.R
import com.gerija.giphy.databinding.ActivitySingleGifBinding

class SingleGifActivity : AppCompatActivity() {
    lateinit var binding: ActivitySingleGifBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleGifBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gifIntent = intent.getStringExtra("gifUrl")
        Glide.with(this).load(gifIntent).into(binding.imSingleGif)
    }
}