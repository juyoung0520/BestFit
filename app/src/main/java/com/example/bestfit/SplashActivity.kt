package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bestfit.util.InitData
import kotlin.concurrent.timer

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        InitData.initData()

        println("start")

        timer(period = 300) {
            println("ing")
            if (InitData.initialization) {
                println("finish")
                cancel()

                val intent = Intent(this@SplashActivity, SignInActivity::class.java)
                startActivity(intent)

                finish()
            }
        }
    }
}