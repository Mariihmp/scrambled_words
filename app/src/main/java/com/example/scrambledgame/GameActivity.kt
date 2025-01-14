package com.example.scrambledgame

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface

import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.ActionMode
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem

import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.gson.Gson

import java.util.concurrent.TimeUnit

class GameActivity : AppCompatActivity() {


    private lateinit var timerTextView: TextView
    private lateinit var scrambledWordGrid: GridLayout
    private lateinit var checkButton: Button
    private lateinit var hearts: List<ImageView>
    private lateinit var placeholders: MutableList<EditText>
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var progressManager: ProgressManager


    private var currentWord: String = ""
    private var scrambledLetters: List<Char> = listOf()
    private var guessedWord: MutableList<Char> = mutableListOf()
    private var lives: Int = 3
    private var timeLeft: Long = 60000 // 60 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        window.statusBarColor = ContextCompat.getColor(this, R.color.very_light_purple)



        scrambledWordGrid = findViewById(R.id.scrambledWordGrid)
        timerTextView = findViewById(R.id.timerTextView)
        checkButton = findViewById(R.id.checkButton)



        // Initialize hearts
        hearts = listOfNotNull(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3),
            findViewById(R.id.heart4),
            findViewById(R.id.heart5)
        )

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

        // Clear any existing children in the GridLayout
        scrambledWordGrid.removeAllViews()

        // Calculate the number of columns (adjust as needed)
        val columns = 4 // You can make this dynamic based on word length
        scrambledWordGrid.columnCount = columns

        // Add scrambled letters to the GridLayout
        scrambledLetters.forEach { char ->
            val textView = TextView(this).apply {
                text = char.toString()
                textSize = 30f // Larger font size
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER // Center text inside the cell
                setBackgroundResource(R.drawable.rectangular_background) // Apply the drawable
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 200 // Increase width
                    height = 200 // Increase height
                    setMargins(8, 8, 8, 8) // Add margin between cells
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
                textSize = 30f // Larger font size
                gravity = Gravity.CENTER // Center text inside the placeholder
                inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Width
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Height
                ).apply {
                    setMargins(8, 8, 8, 8) // Add margin between placeholders
                }

                // Disable long press and paste
                setOnLongClickListener { true }
                customSelectionActionModeCallback = object : ActionMode.Callback {
                    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
                    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = false
                    override fun onDestroyActionMode(mode: ActionMode?) {}
                }

                // Add TextWatcher to restrict input to one character
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // Ensure only one character is allowed
                        if ((s?.length ?: 0) > 1) {
                            val text = s?.substring(0, 1) // Keep only the first character
                            setText(text)
                            setSelection(text?.length ?: 0) // Move cursor to the end
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Handle backspace and move focus
                        if (s.isNullOrEmpty() && i > 0) {
                            placeholders[i - 1].requestFocus() // Move focus to the previous placeholder
                        } else if (s?.length == 1) {
                            if (i < placeholders.size - 1) {
                                placeholders[i + 1].requestFocus() // Move focus to the next placeholder
                            } else {
                                // Dismiss the keyboard after the last character is entered
                                hideKeyboard()
                            }
                        }
                    }
                })
            }
            placeholders.add(placeholder)
            placeholderContainer.addView(placeholder)
        }
    }

    // Helper function to hide the keyboard
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(timeLeft, 1000) {
            @SuppressLint("SetTextI18n")
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
           //show glow here
            showWin()
            // Unlock the next level
            val currentLevel = intent.getIntExtra("CURRENT_LEVEL", 1)
            val nextLevel = currentLevel + 1
            val progressManager = ProgressManager(this)
            progressManager.addScoreForWin()

            // Save progress

            progressManager.saveHighestLevelUnlocked(nextLevel)

            // Return to LevelsActivity with the next level to unlock

            val resultIntent = Intent()
            resultIntent.putExtra("NEXT_LEVEL", nextLevel) // Pass the next level to unlock
            setResult(RESULT_OK, resultIntent)
            val totalScore = progressManager.getTotalScore()
            val intent = Intent(this, CelebrationActivity::class.java).apply {
                putExtra("TOTAL_SCORE", totalScore) // Pass the total score
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Incorrect. Try again.", Toast.LENGTH_SHORT).show()
            //you can add error effect of course in here
            loseLife()
        }
    }
    private  fun showWin(){
        val intent = Intent(this, CelebrationActivity::class.java)
        startActivity(intent)
        finish()
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