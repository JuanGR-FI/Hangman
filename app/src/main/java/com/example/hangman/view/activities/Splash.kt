package com.example.hangman.view.activities

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.hangman.R
import com.example.hangman.databinding.ActivitySplashBinding
import kotlin.concurrent.thread

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mp = MediaPlayer.create(this, R.raw.cartoon_jump)
        mp.start()

        thread{
            Thread.sleep(3500)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Animatoo.animateZoom(this@Splash)
            finish()
        }
    }
}