package com.example.scrambledgame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
private lateinit var progressManager: ProgressManager

class CelebrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_celebration)
        progressManager = ProgressManager(this)
        window.statusBarColor = ContextCompat.getColor(this, R.color.statues_win)


        // Initialize the LottieAnimationView
        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)
        val totalScore = progressManager.getTotalScore()
        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = "$totalScore"

        // Play the animation
        lottieAnimationView.playAnimation()

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, LevelsActivity::class.java))
            finish()

        }
    }
}