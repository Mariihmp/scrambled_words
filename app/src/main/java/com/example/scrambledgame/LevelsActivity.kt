package com.example.scrambledgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LevelsActivity : AppCompatActivity() {

    private lateinit var levels: List<Level>
    private var unlockedLevels = 1 // Start with level 1 unlocked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)

        // Load levels from JSON
        levels = loadLevelsFromJson()

        if (levels.isEmpty()) {
            Toast.makeText(this, "Error loading levels", Toast.LENGTH_SHORT).show()
            return
        }

        // Update unlocked levels based on game progress
        updateUnlockedLevels()

        // Set up click listeners for level buttons
        for (level in 1..levels.size) {
            val cardId = resources.getIdentifier("card$level", "id", packageName)
            val card = findViewById<Button>(cardId)

            if (card == null) {
                Log.e("LevelsActivity", "Card $level not found in layout")
                Toast.makeText(this, "Card $level not found in layout", Toast.LENGTH_SHORT).show()
                continue
            }

            // Set button color and click listener
            if (level <= unlockedLevels) {
                card.setBackgroundColor(getColor(R.color.unlocked_level_color)) // Unlocked color
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

    private fun updateUnlockedLevels() {
        // Example logic: Unlock levels based on game progress
        // For now, unlock levels 1 and 2 by default
        unlockedLevels = 2

        // Add more logic here to dynamically update `unlockedLevels` based on game progress
    }

    private fun startGameActivity(level: Int) {
        try {
            val levelData = levels[level - 1]
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("LEVEL", Gson().toJson(levelData))
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading level $level", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
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

    data class Level(
        val level: Int,
        val word: String,
        val scrambled_word: List<Char>
    )
}