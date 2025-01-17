package com.example.scrambledgame
import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

    // Save the highest level unlocked (only if the new level is higher)
    fun saveHighestLevelUnlocked(newLevel: Int) {
        val currentHighestLevel = getHighestLevelUnlocked()
        if (newLevel > currentHighestLevel) {
            val editor = sharedPreferences.edit()
            editor.putInt("highestLevelUnlocked", newLevel)
            editor.apply()
        }
    }

    // Get the highest level unlocked
    fun getHighestLevelUnlocked(): Int {
        return sharedPreferences.getInt("highestLevelUnlocked", 1) // Default to level 1
    }

    // Save the total score
    private fun saveTotalScore(score: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("totalScore", score)
        editor.apply()
    }

    // Get the total score
    fun getTotalScore(): Int {
        return sharedPreferences.getInt("totalScore", 0) // Default to 0
    }

    // Add score for winning a level
    fun addScoreForWin() {
        val currentScore = getTotalScore()
        saveTotalScore(currentScore + 100)
    }
    fun deductPointsForHint() {
        val currentScore = getTotalScore()
        if (currentScore >= 100) { // Ensure the score doesn't go negative
            saveTotalScore(currentScore - 100)
        } else {
            // Handle the case where the score is less than 100 (optional)
            saveTotalScore(0)
        }
    }
}