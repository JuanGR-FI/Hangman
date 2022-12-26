package com.example.hangman.view.activities

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        tryConnection()

    }

    fun tryConnection(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = Constants.getRetrofit().create(HangManApi::class.java).getWord()
            call.enqueue(object: Callback<Word> {
                override fun onResponse(call: Call<Word>, response: Response<Word>) {
                    Log.d(Constants.LOGTAG, "Respuesta del servidor: $response")
                    Log.d(Constants.LOGTAG, "Datos: ${response.body().toString()}")

                    palabra = response.body()!!.word.toString().lowercase()
                    categoria = response.body()!!.category.toString()

                    binding.pbConexion.visibility = View.GONE

                    inicializeGame()
                    showUI()

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

            misLetras += l

            var palabraPantalla = ""

            if(l !in palabra){//Error
                vidas--
                btn.setBackgroundColor(Color.parseColor("#F91212"))
            }else{
                btn.setBackgroundColor(Color.parseColor("#13C61E"))
            }


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
                openWinDialog()
                youWinSound()
            }else{
                if(l in palabra)
                    correctSound()
            }

            if(vidas == 0){ //Seguimos jugando y actualizamos la palabra
                Toast.makeText(this@MainActivity, "Perdiste :(", Toast.LENGTH_SHORT).show()
                openLoseDialog()
                youLoseSound()
            }else{
                if(l !in palabra)
                    incorrectSound()
            }

        }

    }

    fun inicializeGame(){
        var palabraPantalla = ""
        misLetras = ""
        vidas = 6
        binding.tvLives.text = vidas.toString()
        binding.tvCategory.text = categoria

        for(letra in palabra){
            palabraPantalla += '_'
            palabraPantalla += ' '
        }
        binding.tvWord.text = palabraPantalla

        inicializeButtons()

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

        verifyLetter(button.text.toString(), button)
    }

    fun openWinDialog(){
        val view = View.inflate(this@MainActivity, R.layout.dialog_win_view, null)

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val btn = view.findViewById<Button>(R.id.btn_confirm)
        btn.setOnClickListener {
            hideUI()
            binding.pbConexion.visibility = View.VISIBLE
            tryConnection()
            dialog.dismiss()
        }

    }

    fun openLoseDialog(){
        val view = View.inflate(this@MainActivity, R.layout.dialog_lose_view, null)

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val btn = view.findViewById<Button>(R.id.btn_confirm2)
        btn.setOnClickListener {
            hideUI()
            binding.pbConexion.visibility = View.VISIBLE
            tryConnection()
            dialog.dismiss()
        }
    }

    fun showUI(){
        binding.tvWord.visibility = View.VISIBLE
        binding.tvCategory.visibility = View.VISIBLE
        binding.tvLives.visibility = View.VISIBLE
        binding.ivHeart.visibility = View.VISIBLE
        binding.tlButtons.visibility = View.VISIBLE
    }

    fun hideUI(){
        binding.tvWord.visibility = View.GONE
        binding.tvCategory.visibility = View.GONE
        binding.tvLives.visibility = View.GONE
        binding.ivHeart.visibility = View.GONE
        binding.tlButtons.visibility = View.GONE
    }

    fun inicializeButtons(){
        with(binding){
            btnA.setBackgroundColor(Color.parseColor("#129BF9"))
            btnB.setBackgroundColor(Color.parseColor("#129BF9"))
            btnC.setBackgroundColor(Color.parseColor("#129BF9"))
            btnD.setBackgroundColor(Color.parseColor("#129BF9"))
            btnE.setBackgroundColor(Color.parseColor("#129BF9"))
            btnF.setBackgroundColor(Color.parseColor("#129BF9"))
            btnG.setBackgroundColor(Color.parseColor("#129BF9"))
            btnH.setBackgroundColor(Color.parseColor("#129BF9"))
            btnI.setBackgroundColor(Color.parseColor("#129BF9"))
            btnJ.setBackgroundColor(Color.parseColor("#129BF9"))
            btnK.setBackgroundColor(Color.parseColor("#129BF9"))
            btnL.setBackgroundColor(Color.parseColor("#129BF9"))
            btnM.setBackgroundColor(Color.parseColor("#129BF9"))
            btnN.setBackgroundColor(Color.parseColor("#129BF9"))
            btnN2.setBackgroundColor(Color.parseColor("#129BF9"))
            btnO.setBackgroundColor(Color.parseColor("#129BF9"))
            btnP.setBackgroundColor(Color.parseColor("#129BF9"))
            btnQ.setBackgroundColor(Color.parseColor("#129BF9"))
            btnR.setBackgroundColor(Color.parseColor("#129BF9"))
            btnS.setBackgroundColor(Color.parseColor("#129BF9"))
            btnT.setBackgroundColor(Color.parseColor("#129BF9"))
            btnU.setBackgroundColor(Color.parseColor("#129BF9"))
            btnV.setBackgroundColor(Color.parseColor("#129BF9"))
            btnW.setBackgroundColor(Color.parseColor("#129BF9"))
            btnX.setBackgroundColor(Color.parseColor("#129BF9"))
            btnY.setBackgroundColor(Color.parseColor("#129BF9"))
            btnZ.setBackgroundColor(Color.parseColor("#129BF9"))

        }
    }

}