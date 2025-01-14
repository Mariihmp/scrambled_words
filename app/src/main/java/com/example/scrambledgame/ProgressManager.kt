package com.example.scrambledgame
import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

    // Save the highest level unlocked
    fun saveHighestLevelUnlocked(level: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("highestLevelUnlocked", level) // Corrected key
        editor.apply()
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
    fun addScoreForWin() {
        val currentScore = getTotalScore()
        saveTotalScore(currentScore + 100)
    }
}