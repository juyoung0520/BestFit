package com.example.bestfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bestfit.R
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportFragmentManager.beginTransaction().replace(R.id.activity_signup_layout_frame, SignupFragment()).commit()
    }
}
