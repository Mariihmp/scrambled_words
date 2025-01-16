package com.example.scrambledgame

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BackgroundMusicPlayer.getInstance(this).start()
    }
}