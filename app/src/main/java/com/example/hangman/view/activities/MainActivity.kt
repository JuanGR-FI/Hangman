package com.example.hangman.view.activities

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.hangman.R
import com.example.hangman.databinding.ActivityMainBinding
import com.example.hangman.model.HangManApi
import com.example.hangman.model.Word
import com.example.hangman.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mp: MediaPlayer
    private lateinit var palabra: String
    private lateinit var categoria: String
    private var misLetras = ""
    private var vidas = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        CoroutineScope(Dispatchers.IO).launch {
            val call = Constants.getRetrofit().create(HangManApi::class.java).getWord()
            call.enqueue(object: Callback<Word> {
                override fun onResponse(call: Call<Word>, response: Response<Word>) {
                    Log.d(Constants.LOGTAG, "Respuesta del servidor: $response")
                    Log.d(Constants.LOGTAG, "Datos: ${response.body().toString()}")

                    palabra = response.body()!!.word.toString().lowercase()
                    categoria = response.body()!!.category.toString()

                    inicializeGame()

                }

                override fun onFailure(call: Call<Word>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "ERROR DE CONEXION ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    override fun onStop() {
        super.onStop()
        mp.release()
    }

    fun verifyLetter(letra: String, btn: Button) {
        var fallas: Int

        val l = letra.lowercase()

        if(l in misLetras){
            Toast.makeText(this@MainActivity, "Letra elegida anteriormente", Toast.LENGTH_SHORT).show()

        }else{
            btn.setBackgroundColor(Color.parseColor("#151515"))
            misLetras += l

            var palabraPantalla = ""

            if(l !in palabra) //Error
                vidas--

            fallas = 0
            for(letra in palabra){
                if(letra in misLetras){
                    palabraPantalla += letra
                    palabraPantalla += ' '
                }else{
                    palabraPantalla += '_'
                    palabraPantalla += ' '
                    fallas += 1
                }
            }
            binding.tvWord.text = palabraPantalla
            binding.tvLives.text = vidas.toString()

            if (fallas == 0){
                Toast.makeText(this@MainActivity, "Ganaste!!", Toast.LENGTH_SHORT).show()
                youWinSound()
            }else{
                if(l in palabra)
                    correctSound()
            }

            if(vidas == 0){ //Seguimos jugando y actualizamos la palabra
                Toast.makeText(this@MainActivity, "Perdiste :(", Toast.LENGTH_SHORT).show()
                youLoseSound()
            }else{
                if(l !in palabra)
                    incorrectSound()
            }

        }

    }

    fun inicializeGame(){
        var palabraPantalla = ""
        binding.tvLives.text = vidas.toString()
        binding.tvCategory.text = categoria

        for(letra in palabra){
            palabraPantalla += '_'
            palabraPantalla += ' '
        }
        binding.tvWord.text = palabraPantalla

    }

    fun correctSound(){
        mp = MediaPlayer.create(this, R.raw.correct)
        mp.start()
    }

    fun incorrectSound(){
        mp = MediaPlayer.create(this, R.raw.answerwrong)
        mp.start()
    }

    fun youWinSound(){
        mp = MediaPlayer.create(this, R.raw.victory)
        mp.start()
    }

    fun youLoseSound(){
        mp = MediaPlayer.create(this, R.raw.supermariolifelost)
        mp.start()
    }

    fun keyPressed(view: View) {
        val button = view as Button
        /*if(binding.button2.id){
            Toast.makeText(this@MainActivity, "Se oprimio la tecla A", Toast.LENGTH_SHORT).show()
        }else if(view.id.equals("button3")){
            Toast.makeText(this@MainActivity, "Se oprimio la tecla B", Toast.LENGTH_SHORT).show()
        }*/
        //Toast.makeText(this@MainActivity, "Se oprimio la tecla ${button.text}", Toast.LENGTH_SHORT).show()
        verifyLetter(button.text.toString(), button)

    }

}