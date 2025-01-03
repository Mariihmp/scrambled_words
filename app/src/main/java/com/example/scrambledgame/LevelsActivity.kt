package com.example.scrambledgame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LevelsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)
        val level1Button = findViewById<Button>(R.id.card1)
        val level2Button = findViewById<Button>(R.id.card2)
        val level3Button = findViewById<Button>(R.id.card3)

        // Handle level 1 button click
        level1Button.setOnClickListener {
            // Navigate to GameActivity for level 1
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("LEVEL_NUMBER", 1)
            startActivity(intent)
        }

        // Handle level 2 button click
        level2Button.setOnClickListener {
            // Navigate to GameActivity for level 2
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("LEVEL_NUMBER", 2)
            startActivity(intent)
        }

        // Handle level 3 button click
        level3Button.setOnClickListener {
            // Check if level 3 is unlocked
            if (isLevelUnlocked(3)) {
                // Navigate to GameActivity for level 3
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("LEVEL_NUMBER", 3)
                startActivity(intent)
            } else {
                // Display message if level 3 is locked
                Toast.makeText(this, "Level 3 is locked! Complete previous levels to unlock.", Toast.LENGTH_SHORT).show()
            }
        }

        // Get reference to the GridLayout where levels will be displayed
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)

        // Define total levels (9 levels in this case)
        val totalLevels = 9

        // Define how many levels are unlocked (e.g., 3 levels unlocked initially)
        val unlockedLevels = 3

        // Loop through each level and add dynamic icons and buttons based on the unlocked status
        for (level in 1..totalLevels) {
            // Create a layout for each button and its icon
            val levelLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec((level - 1) / 3) // Calculate row position
                    columnSpec = GridLayout.spec((level - 1) % 3) // Calculate column position
                    marginStart = 8
                    marginEnd = 8
                    topMargin = 8
                    bottomMargin = 8
                }
                gravity = android.view.Gravity.CENTER
            }

            // Create the Button for each level
            val button = Button(this).apply {
                text = "Card $level"
                textSize = 16f
                setPadding(8, 8, 8, 8)
                isEnabled = level <= unlockedLevels // Enable button only for unlocked levels
            }

            // Create the icon for each level
            val icon = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    marginEnd = 8
                    bottomMargin = 8
                }

                // Set the icon based on the level's unlocked status
                if (level <= unlockedLevels) {
                    // No icon for unlocked levels
                } else {
                    setImageResource(R.drawable.ic_level_locked) // Display locked icon for locked levels
                }
            }

            // Add the Button and Icon to the layout container
            levelLayout.addView(button)
            levelLayout.addView(icon)

            // Add the layout (containing both the button and icon) to the GridLayout
            gridLayout.addView(levelLayout)

            // Button click listener for each level button
            button.setOnClickListener {
                if (level == 1 || level == 2 || isLevelUnlocked(level)) {
                    // Navigate to GameActivity with the selected level
                    val intent = Intent(this@LevelsActivity, GameActivity::class.java)
                    intent.putExtra("LEVEL_NUMBER", level)
                    startActivity(intent)
                } else {
                    // Show message if the level is locked
                    Toast.makeText(this, "Level $level is locked! Complete previous levels to unlock.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to check if a level is unlocked (based on shared preferences)
    private fun isLevelUnlocked(level: Int): Boolean {
        val sharedPreferences = getSharedPreferences("GameProgress", MODE_PRIVATE)
        return sharedPreferences.getBoolean("LEVEL_$level", false)
    }

    // Function to unlock a level (call this function when the player completes a level)
    private fun unlockLevel(level: Int) {
        val sharedPreferences = getSharedPreferences("GameProgress", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("LEVEL_$level", true)
        editor.apply()
    }

    }

