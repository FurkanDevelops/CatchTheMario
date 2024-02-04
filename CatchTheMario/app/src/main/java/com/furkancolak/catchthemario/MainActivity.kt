package com.furkancolak.catchthemario

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.furkancolak.catchthemario.databinding.ActivityMainBinding
import java.util.Random

private lateinit var binding: ActivityMainBinding
var score = 0
var time = 10
var runnable = Runnable{}
var handler = Handler(Looper.getMainLooper())
private lateinit var sharedPref : SharedPreferences

class MainActivity : AppCompatActivity() {
    var imageArray = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // ImageArray
        imageArray.add(binding.img1)
        imageArray.add(binding.img2)
        imageArray.add(binding.img3)
        imageArray.add(binding.img4)
        imageArray.add(binding.img5)
        imageArray.add(binding.img6)
        imageArray.add(binding.img7)
        imageArray.add(binding.img8)
        imageArray.add(binding.img9)

        hideImages()
        saveData()

        // Timer
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textTime.text = "Time : ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                binding.textTime.text = "Time : 0"
                handler.removeCallbacks(runnable)
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }
                val alert = AlertDialog.Builder(this@MainActivity)
                alert.setTitle("Game Over")
                alert.setMessage("Restart The Game ? ")
                alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    // Restart
                    score = 0
                    binding.textScore.text = "Score: $score"
                    val intentFromMain = intent
                    finish()
                    startActivity(intentFromMain)
                })
                alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this@MainActivity, "Game Over", Toast.LENGTH_LONG).show()
                })
                alert.show()
            }
        }.start()
    }

    fun clickImg(view: View) {
        score++
        binding.textScore.text = "Score: $score"
        saveData() // Save the score when it's updated
    }

    fun hideImages() {
        runnable = object : Runnable {
            override fun run() {
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }
                val random = Random()
                val randomIndex = random.nextInt(9)
                imageArray[randomIndex].visibility = View.VISIBLE
                handler.postDelayed(runnable, 500)
            }
        }
        handler.post(runnable)
    }

    fun saveData() {
        val sharedPref = getSharedPreferences("com.furkancolak.catchthemario", Context.MODE_PRIVATE)
        val previousScore = sharedPref.getInt("Previous Score", -1)
        val currentScore = score

        if (previousScore == -1) {
            binding.textPrevious.text = "Previous Score: NOT AVAILABLE"
        } else {
            binding.textPrevious.text = "Previous Score: $previousScore"
        }

        with(sharedPref.edit()) {
            putInt("Previous Score", currentScore)
            apply()
        }
    }
}

