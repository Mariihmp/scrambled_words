package com.example.scrambledgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LevelsActivity : AppCompatActivity() {

    private lateinit var levels: List<Level>
    private lateinit var progressManager: ProgressManager

    // List of level button IDs
    private val levelButtonIds = listOf(
        R.id.card1, R.id.card2, R.id.card3, R.id.card4, R.id.card5,
        R.id.card6, R.id.card7, R.id.card8, R.id.card9
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)

        // Initialize ProgressManager

        window.statusBarColor = ContextCompat.getColor(this, R.color.very_light_purple)
        progressManager = ProgressManager(this)
        val totalScore = progressManager.getTotalScore()
        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = "$totalScore"

        // Load levels from JSON
        levels = loadLevelsFromJson()

        if (levels.isEmpty()) {
            Toast.makeText(this, "Error loading levels", Toast.LENGTH_SHORT).show()
            return
        }

        // Set up click listeners for level buttons
        setupLevelButtons()
    }

    private fun setupLevelButtons() {
        // Get the highest level unlocked
        val highestLevelUnlocked = progressManager.getHighestLevelUnlocked()

        for ((index, cardId) in levelButtonIds.withIndex()) {
            val level = index + 1 // Levels start from 1
            val card = findViewById<Button>(cardId)

            if (card == null) {
                Log.e("LevelsActivity", "Card $level not found in layout")
                Toast.makeText(this, "Card $level not found in layout", Toast.LENGTH_SHORT).show()
                continue
            }

            // Set button color and click listener
            if (level <= highestLevelUnlocked) {
                card.setBackgroundColor(getColor(R.color.purple_500_lighter)) // Unlocked color
                card.setOnClickListener {
                    startGameActivity(level)
                }
            } else {
                card.setBackgroundColor(getColor(R.color.dark_purple)) // Locked color
                card.setOnClickListener {
                    Toast.makeText(this, "Level $level is locked! Complete previous levels to unlock.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startGameActivity(level: Int) {
        try {
            val levelData = levels[level - 1]
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("LEVEL", Gson().toJson(levelData))
                putExtra("CURRENT_LEVEL", level) // Pass the current level number
            }
            startActivityForResult(intent, REQUEST_CODE_GAME)
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading level $level", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GAME && resultCode == RESULT_OK) {
            // Get the next level to unlock
            val nextLevel = data?.getIntExtra("NEXT_LEVEL", 1) ?: 1

            // Update the highest level unlocked
            progressManager.saveHighestLevelUnlocked(nextLevel)

            // Refresh the UI
            setupLevelButtons()
        }
    }

    private fun loadLevelsFromJson(): List<Level> {
        return try {
            val inputStream = resources.openRawResource(R.raw.levels)
            val json = inputStream.bufferedReader().use { it.readText() }
            val levelType = object : TypeToken<List<Level>>() {}.type
            Gson().fromJson(json, levelType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    companion object {
        private const val REQUEST_CODE_GAME = 1001
    }

    data class Level(
        val level: Int,
        val word: String,
        val scrambled_word: List<Char>
    )
}