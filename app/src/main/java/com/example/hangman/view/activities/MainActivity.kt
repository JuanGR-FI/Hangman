package com.example.hangman.view.activities

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.blogspot.atifsoftwares.animatoolib.Animatoo
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
import kotlin.concurrent.thread

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

    private fun tryConnection(){
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
                    binding.pbConexion.visibility = View.GONE
                    binding.ivError.visibility = View.VISIBLE
                    binding.btnReload.visibility = View.VISIBLE
                    Toast.makeText(this@MainActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@MainActivity, getString(R.string.repeated_letter), Toast.LENGTH_SHORT).show()

        }else{

            misLetras += l

            var palabraPantalla = ""

            if(l !in palabra){//Error
                vidas--
                //btn.setBackgroundColor(Color.parseColor("#F91212"))
                btn.setBackgroundColor(getColor(R.color.red))
            }else{
                //btn.setBackgroundColor(Color.parseColor("#13C61E"))
                btn.setBackgroundColor(getColor(R.color.green))
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
                //Toast.makeText(this@MainActivity, "Ganaste!!", Toast.LENGTH_SHORT).show()
                openWinDialog()
                youWinSound()


            }else{
                if(l in palabra)
                    correctSound()
            }

            if(vidas == 0){ //Seguimos jugando y actualizamos la palabra
                //Toast.makeText(this@MainActivity, "Perdiste :(", Toast.LENGTH_SHORT).show()
                updateSprite()
                youLoseSound()
                openLoseDialog()


            }else{
                updateSprite()
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
        updateSprite()
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
        binding.ivSprite.visibility = View.VISIBLE
    }

    fun hideUI(){
        binding.tvWord.visibility = View.GONE
        binding.tvCategory.visibility = View.GONE
        binding.tvLives.visibility = View.GONE
        binding.ivHeart.visibility = View.GONE
        binding.tlButtons.visibility = View.GONE
        binding.ivSprite.visibility = View.GONE
    }

    fun updateSprite(){
        when(vidas){
            6 -> { binding.ivSprite.setImageResource(R.drawable.hangman_sprite_uno) }
            5 -> { binding.ivSprite.setImageResource(R.drawable.hangman_sprite_dos) }
            4 -> { binding.ivSprite.setImageResource(R.drawable.hangman_sprite_tres) }
            3 -> { binding.ivSprite.setImageResource(R.drawable.hangman_sprite_cuatro) }
            2 -> { binding.ivSprite.setImageResource(R.drawable.hangman_sprite_siete) }
            1 -> { binding.ivSprite.setImageResource(R.drawable.hangman_sprite_cinco) }
            0 -> { binding.ivSprite.setImageResource(R.drawable.hangman_sprite_seis ) }
        }
    }

    fun inicializeButtons(){
        with(binding){
            btnA.setBackgroundColor(getColor(R.color.btnColor))
            btnB.setBackgroundColor(getColor(R.color.btnColor))
            btnC.setBackgroundColor(getColor(R.color.btnColor))
            btnD.setBackgroundColor(getColor(R.color.btnColor))
            btnE.setBackgroundColor(getColor(R.color.btnColor))
            btnF.setBackgroundColor(getColor(R.color.btnColor))
            btnG.setBackgroundColor(getColor(R.color.btnColor))
            btnH.setBackgroundColor(getColor(R.color.btnColor))
            btnI.setBackgroundColor(getColor(R.color.btnColor))
            btnJ.setBackgroundColor(getColor(R.color.btnColor))
            btnK.setBackgroundColor(getColor(R.color.btnColor))
            btnL.setBackgroundColor(getColor(R.color.btnColor))
            btnM.setBackgroundColor(getColor(R.color.btnColor))
            btnN.setBackgroundColor(getColor(R.color.btnColor))
            btnN2.setBackgroundColor(getColor(R.color.btnColor))
            btnO.setBackgroundColor(getColor(R.color.btnColor))
            btnP.setBackgroundColor(getColor(R.color.btnColor))
            btnQ.setBackgroundColor(getColor(R.color.btnColor))
            btnR.setBackgroundColor(getColor(R.color.btnColor))
            btnS.setBackgroundColor(getColor(R.color.btnColor))
            btnT.setBackgroundColor(getColor(R.color.btnColor))
            btnU.setBackgroundColor(getColor(R.color.btnColor))
            btnV.setBackgroundColor(getColor(R.color.btnColor))
            btnW.setBackgroundColor(getColor(R.color.btnColor))
            btnX.setBackgroundColor(getColor(R.color.btnColor))
            btnY.setBackgroundColor(getColor(R.color.btnColor))
            btnZ.setBackgroundColor(getColor(R.color.btnColor))

        }
    }

    fun reloadConnection(view: View) {
        binding.ivError.visibility = View.GONE
        view.visibility = View.GONE

        binding.pbConexion.visibility = View.VISIBLE

        tryConnection()

    }

}