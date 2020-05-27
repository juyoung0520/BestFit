package com.example.bestfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main_bottom_nav.setOnNavigationItemSelectedListener(this)
        activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_dressroom
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.menu_bottom_nav_action_dressroom -> {
                supportFragmentManager.beginTransaction().add(R.id.activity_main_layout_frame, FragmentDressroom())
                    .commit()
            }
        }

        return true
    }
}
