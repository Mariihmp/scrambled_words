package com.example.scrambledgame
import android.media.MediaPlayer;
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContentView(R.layout.activity_main) // Set the layout for this activity
          ///it's depricated flag fullscreen so make changes to this
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_start)


        val splashText: TextView = findViewById(R.id.idTVText)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        splashText.startAnimation(slideAnimation)

        // Use a Handler to delay the transition to the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an intent to navigate to the StartActivity
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent) // Start the StartActivity
            finish() // Close the current activity to prevent going back to it
        }, 4000) // 2 seconds delay
    }
}