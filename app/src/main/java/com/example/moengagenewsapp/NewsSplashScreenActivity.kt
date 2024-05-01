package com.example.moengagenewsapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class NewsSplashScreenActivity : AppCompatActivity(){

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this@NewsSplashScreenActivity, MainActivity::class.java))
        finish()

    }
}