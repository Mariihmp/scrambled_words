package com.example.scrambledgame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class GameActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var scrambledWordTextView: TextView
    private lateinit var checkButton: Button
    private lateinit var hearts: List<ImageView>
    private lateinit var placeholders: MutableList<EditText>
    private lateinit var countdownTimer: CountDownTimer

    private var currentWord: String = ""
    private var scrambledLetters: List<Char> = listOf()
    private var guessedWord: MutableList<Char> = mutableListOf()
    private var lives: Int = 3
    private var timeLeft: Long = 60000 // 60 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize views
        timerTextView = findViewById(R.id.timerTextView)
        scrambledWordTextView = findViewById(R.id.scrambledWordGrid)
        checkButton = findViewById(R.id.checkButton)

        // Initialize hearts
        hearts = listOfNotNull(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3),
            findViewById(R.id.heart4),
            findViewById(R.id.heart5),


        )

        // Check if all required views are initialized
//        if (timerTextView == null || scrambledWordTextView == null || checkButton == null || hearts.size < 5) {
//            Log.e("GameActivity", "One or more views are missing in the layout.")
//            Toast.makeText(this, "Error: Missing required views. Please check the layout.", Toast.LENGTH_LONG).show()
//            finish()
//            return
//        }

        // Set click listener for the check button
        checkButton.setOnClickListener { checkUserInput() }

        // Get level data from intent
        val levelJson = intent.getStringExtra("LEVEL")
        if (levelJson != null) {
            try {
                val level = Gson().fromJson(levelJson, Level::class.java)
                startLevel(level)
            } catch (e: Exception) {
                Log.e("GameActivity", "Error parsing level data: ${e.message}", e)
                Toast.makeText(this, "Error loading level data. Please try again.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Log.e("GameActivity", "No level data found in intent.")
            Toast.makeText(this, "Error: No level data found.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startLevel(level: Level) {
        currentWord = level.word
        scrambledLetters = level.scrambled_word
        guessedWord = MutableList(currentWord.length) { '_' }

        setupPlaceholders()

        // Find the GridLayout
        val scrambledWordGrid = findViewById<GridLayout>(R.id.scrambledWordGrid)

        // Clear any existing children
        scrambledWordGrid.removeAllViews()

        // Calculate the number of columns (adjust as needed)
        val columns = 4 // You can make this dynamic based on word length
        scrambledWordGrid.columnCount = columns

        // Add scrambled letters to the GridLayout
        scrambledLetters.forEach { char ->
            val textView = TextView(this).apply {
                text = char.toString()
                textSize = 20f
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.rectangular_background) // Use drawable for the rectangular shape
                setPadding(16, 16, 16, 16) // Padding inside the rectangle
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 100 // Set width for each rectangle
                    height = 100 // Set height for each rectangle
                    setMargins(8, 8, 8, 8) // Margins around the rectangle
                }
            }
            scrambledWordGrid.addView(textView)
        }

        startCountdownTimer()
    }



    private fun setupPlaceholders() {
        val placeholderContainer = findViewById<LinearLayout>(R.id.placeholderContainer)
        if (placeholderContainer == null) {
            Log.e("GameActivity", "placeholderContainer not found in layout.")
            Toast.makeText(this, "Error: Missing placeholder container.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        placeholderContainer.removeAllViews()
        placeholders = mutableListOf()

        for (i in currentWord.indices) {
            val placeholder = EditText(this).apply {
                hint = "_"
                textSize = 18f
                inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (s?.length == 1 && i < placeholders.size - 1) {
                            placeholders[i + 1].requestFocus()
                        }
                    }
                })
            }
            placeholders.add(placeholder)
            placeholderContainer.addView(placeholder)
        }
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                timerTextView.text = "${timeLeft / 1000}s"
            }

            override fun onFinish() {
                Toast.makeText(this@GameActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                loseLife()
            }
        }.start()
    }

    private fun checkUserInput() {
        val userInput = placeholders.joinToString("") { it.text.toString() }
        if (userInput.equals(currentWord, true)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            finish() // Return to LevelsActivity
        } else {
            Toast.makeText(this, "Incorrect. Try again.", Toast.LENGTH_SHORT).show()
            loseLife()
        }
    }

    private fun loseLife() {
        lives--
        updateHearts()
        if (lives <= 0) {
            Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun updateHearts() {
        hearts.forEachIndexed { index, imageView ->
            imageView.setImageResource(if (index < lives) R.drawable.ic_heart else R.drawable.ic_empty_heart)
        }
    }

    data class Level(val level: Int, val word: String, val scrambled_word: List<Char>)
}