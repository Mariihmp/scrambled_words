package com.example.scrambledgame
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat

class StartActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_start)

        // Initialize the Start Game button
        val btnStartGame: Button = findViewById(R.id.btnStartGame)

        // Navigate to LevelsActivity on button click
        btnStartGame.setOnClickListener {
            val intent = Intent(this, LevelsActivity::class.java)
            startActivity(intent)
        }
    }
}