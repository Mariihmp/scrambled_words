package com.example.scrambledgame
import android.content.Context
import android.media.MediaPlayer

class BackgroundMusicPlayer private constructor(context: Context) {
    private var mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.bg_music_ambient).apply {
        this?.isLooping = true // Loop the music
    }

    companion object {
        @Volatile
        private var instance: BackgroundMusicPlayer? = null

        fun getInstance(context: Context): BackgroundMusicPlayer {
            return instance ?: synchronized(this) {
                instance ?: BackgroundMusicPlayer(context).also { instance = it }
            }
        }
    }

    fun start() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    fun pause() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer?.pause()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        instance = null
    }
}