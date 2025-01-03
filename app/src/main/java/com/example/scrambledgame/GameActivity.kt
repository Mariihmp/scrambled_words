package com.example.scrambledgame
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private val word = "SCRAMBLE" // Example word
    private lateinit var hearts: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val placeholderContainer = findViewById<LinearLayout>(R.id.placeholderContainer)
        val shuffledLettersGrid = findViewById<GridLayout>(R.id.shuffledLettersGrid)

        // Add placeholders dynamically based on word length
        word.forEach { _ ->
            val placeholder = TextView(this).apply {
                text = "_"
                textSize = 24f
                setPadding(16, 8, 16, 8)
            }
            placeholderContainer.addView(placeholder)
        }

        // Shuffle the letters and add buttons dynamically
        val letters = word.toCharArray().toMutableList()
        letters.shuffle()

        letters.forEach { letter ->
            val button = Button(this).apply {
                text = letter.toString()
                textSize = 18f
                setPadding(16, 8, 16, 8)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    marginStart = 4
                    marginEnd = 4
                    topMargin = 4
                    bottomMargin = 4

                }
            }

            // Add click listener to the buttons
            button.setOnClickListener {
                // Handle button click, e.g., updating placeholders
            }

            shuffledLettersGrid.addView(button)
        }

        // Initialize hearts
        hearts = listOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3),
            findViewById(R.id.heart4),
            findViewById(R.id.heart5)
        )
    }
}
