package com.example.bestfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class SetProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        val setProfile = intent.getBooleanExtra("setProfile", false)
        if (!setProfile)
            replaceFragment(SetProfileFragment())
        else
            replaceFragment(SetDetailProfileFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.activity_set_profile_layout_frame, fragment).commit()
    }
}
