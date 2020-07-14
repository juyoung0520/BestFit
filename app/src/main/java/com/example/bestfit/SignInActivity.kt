package com.example.bestfit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.bestfit.util.InitData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        if (auth.currentUser != null) {
            startMainActivity()
            return
        }

        initToolbar()

        replaceFragment(SignInFragment())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                replaceFragment(SignInFragment())
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(activity_signin_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.activity_signin_layout_frame, fragment).commit()
    }
}
