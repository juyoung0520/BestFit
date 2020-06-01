package com.example.bestfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        replaceFragment(SignInFragment())
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
//            startMainActivity()
        }
    }

    fun setToolbar(toolbar: Toolbar, setHomeButton: Boolean = false) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        if (setHomeButton)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.activity_signin_layout_frame, fragment).commit()
    }
}
