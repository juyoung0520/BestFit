package com.example.bestfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val auth = FirebaseAuth.getInstance()
    var currentNavigationIndex: Int = 0
    var currentNavigation: ArrayList<ArrayList<Fragment>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main_bottom_nav.setOnNavigationItemSelectedListener(this)

        // Navigation Init
        initNavigation()
    }

    fun initNavigation() {
        currentNavigation.clear()

        for (idx in 0..2)
            currentNavigation.add(arrayListOf())

        activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_dressroom
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.menu_bottom_nav_action_dressroom -> {
                changeNavigation(1)
            }

            R.id.menu_bottom_nav_action_menu -> {
                auth.signOut()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        return true
    }

    fun setToolbar(toolbar: Toolbar, setHomeButton: Boolean = false) {
        setSupportActionBar(toolbar)

        if (setHomeButton)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun changeNavigation(newNavigationIndex: Int, bundle: Bundle? = null) {
//        if (currentNavigationIndex == newNavigationIndex) {
//            var isTop = true
//
//            when (currentNavigationIndex) {
//                0 -> {
//                    if (currentNavigation[currentNavigationIndex].size == 1) {
//                        val currentFragment = currentNavigation[currentNavigationIndex].last() as MainHomeFragment
//                        isTop = currentFragment.scrollTop(currentFragment.view!!)
//                    }
//                }
//
//                1 -> {
//                    if (currentNavigation[currentNavigationIndex].size == 1) {
//                        val currentFragment = currentNavigation[currentNavigationIndex].last() as MainFriendFragment
//                        isTop = currentFragment.scrollTop()
//                    }
//                }
//
//                2 -> {
//                    if (currentNavigation[currentNavigationIndex].size == 1) {
//                        val currentFragment = currentNavigation[currentNavigationIndex].last() as AccountFragment
//                        isTop = currentFragment.scrollTop()
//                    }
//                }
//            }
//
//            if (!isTop)
//                return
//
//            for (cnt in 1..currentNavigation[currentNavigationIndex].size - 1) {
//                supportFragmentManager.beginTransaction().remove(currentNavigation[currentNavigationIndex].last())
//                    .commit()
//                currentNavigation[currentNavigationIndex].removeAt(currentNavigation[currentNavigationIndex].lastIndex)
//            }
//
//            supportFragmentManager.beginTransaction().show(currentNavigation[currentNavigationIndex].last())
//                .commit()
//
//            return
//        }

        if (currentNavigation[currentNavigationIndex].isNotEmpty()) {
            supportFragmentManager.beginTransaction().hide(currentNavigation[currentNavigationIndex].last())
                .commit()
        }

        if (currentNavigation[newNavigationIndex].isEmpty()) {
            var newFragment: Fragment? = null
            when (newNavigationIndex) {
                0 -> {
//                    newFragment = MainHomeFragment()
                }

                1 -> {
                    newFragment = DressroomFragment()
                }

                2 -> {
//                    newFragment = AccountFragment()
                }
            }

            currentNavigation[newNavigationIndex].add(newFragment!!)
            val currentFragment = currentNavigation[newNavigationIndex].last()
            currentFragment.arguments = bundle

            supportFragmentManager.beginTransaction().add(R.id.activity_main_layout_frame, currentFragment)
                .commit()

            currentNavigationIndex = newNavigationIndex
            return
        }

        val currentFragment = currentNavigation[newNavigationIndex].last()
        supportFragmentManager.beginTransaction().show(currentFragment)
            .commit()

        currentNavigationIndex = newNavigationIndex
    }

    fun changeFragment(newFragment: Fragment?, bundle: Bundle? = null, doRemove: Boolean = false) {
        // Remove 할 때 newFragment = null
        if (doRemove) {
            supportFragmentManager.beginTransaction().remove(currentNavigation[currentNavigationIndex].last())
                .commit()

            currentNavigation[currentNavigationIndex].removeAt(currentNavigation[currentNavigationIndex].lastIndex)

            supportFragmentManager.beginTransaction().show(currentNavigation[currentNavigationIndex].last())
                .commit()

            return
        }

        supportFragmentManager.beginTransaction().hide(currentNavigation[currentNavigationIndex].last())
            .commit()

        currentNavigation[currentNavigationIndex].add(newFragment!!)
        val currentFragment = currentNavigation[currentNavigationIndex].last()
        currentFragment.arguments = bundle

        supportFragmentManager.beginTransaction().add(R.id.activity_main_layout_frame, currentFragment)
            .commit()
    }
}
