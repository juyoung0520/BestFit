package com.example.bestfit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.bestfit.util.InitData
import com.example.bestfit.util.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

// BottomNavigationView.OnNavigationItemSelectedListener
class MainActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    var currentNavigationIndex: Int = 0
    var currentNavigation: ArrayList<ArrayList<Fragment>> = arrayListOf()
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

//        activity_main_bottom_nav.setOnNavigationItemSelectedListener(this)

        InitData.initData()

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
//        activity_main_toolbar.setupWithNavController(navHostFragment.navController, AppBarConfiguration(setOf(R.id.homeFragment, R.id.dressroomFragment, R.id.settingsFragment)))
        // Navigation Init
//        NavigationUI.setupWithNavController(activity_main_bottom_nav, navHostFragment.navController)
//        initNavigation()

        // SetProfile Check
        checkSetProfile()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.nav_home, R.navigation.nav_dressroom, R.navigation.nav_settings)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = activity_main_bottom_nav.setupWithNavController(navGraphIds, supportFragmentManager, R.id.nav_host_fragment_container, intent)

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            activity_main_toolbar.setupWithNavController(navController)
//            setupActionBarWithNavController(navController)
        })

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

//    private fun initNavigation() {
//        currentNavigation.clear()
//
//        for (idx in 0..2)
//            currentNavigation.add(arrayListOf())
//
//        activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_dressroom
//    }

    private fun checkSetProfile() {
        db.collection("accounts").document(currentUid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result?.data == null) {
                    val intent = Intent(this, SetProfileActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    fun setToolbar(toolbar: Toolbar, setHomeButton: Boolean = false) {
        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (setHomeButton) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
    }

//    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
//        when (p0.itemId) {
//            R.id.menu_bottom_nav_action_home -> {
//                changeNavigation(0)
//            }
//            R.id.menu_bottom_nav_action_dressroom -> {
//                changeNavigation(1)
//            }
//            R.id.menu_bottom_nav_action_menu -> {
//                changeNavigation(2)
//            }
//        }
//
//        return true
//    }

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

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentNavigation[currentNavigationIndex].isNotEmpty()) {
            supportFragmentManager.beginTransaction().hide(currentNavigation[currentNavigationIndex].last())
                .commit()
        }

        if (currentNavigation[newNavigationIndex].isEmpty()) {
            var newFragment: Fragment? = null
            when (newNavigationIndex) {
                0 -> {
                    newFragment = HomeFragment()
                }

                1 -> {
                    newFragment = DressroomFragment()
                }

                2 -> {
                    newFragment = SettingsFragment()
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

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
