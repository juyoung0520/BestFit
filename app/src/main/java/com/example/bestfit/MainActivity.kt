package com.example.bestfit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.NavigationFragmentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private lateinit var navigationFragmentViewModel: NavigationFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationFragmentViewModel = ViewModelProvider(this).get(NavigationFragmentViewModel::class.java)

        val navigationIndexObserver = Observer<Int> { newNavigationIndex ->
            println("observe index -> $newNavigationIndex")
//            currentNavigationIndex = newNavigationIndex
        }

        val navigationObserver = Observer<ArrayList<ArrayList<Fragment>>> { newNavigationFragments ->
            println("observe fragment -> $newNavigationFragments")
//            currentNavigation = newNavigationFragments

            var isEmpty = true
            for (navigation in newNavigationFragments) {
                if (!navigation.isNullOrEmpty()) {
                    isEmpty = false
                    break
                }
            }

            if (isEmpty)
                activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_home
        }

        navigationFragmentViewModel.activatedNavigationIndex.observe(this, navigationIndexObserver)
        navigationFragmentViewModel.navigationFragments.observe(this, navigationObserver)

        activity_main_bottom_nav.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            InitData.initData()

            // Navigation Init
//            initNavigation()

            // SetProfile Check
            checkSetProfile()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("isInitialized", true)
    }

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

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.menu_bottom_nav_action_home -> {
                changeNavigation(0)
            }
            R.id.menu_bottom_nav_action_dressroom -> {
                changeNavigation(1)
            }
            R.id.menu_bottom_nav_action_settings -> {
                changeNavigation(2)
            }
        }

        return true
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

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        val pausedNavigation = navigationFragmentViewModel.navigationFragments.value!![navigationFragmentViewModel.activatedNavigationIndex.value!!]
        val activatedNavigation = navigationFragmentViewModel.navigationFragments.value!![newNavigationIndex]

        if (pausedNavigation.isNotEmpty()) {
            supportFragmentManager.beginTransaction().hide(pausedNavigation.last())
                .commit()
        }

        if (activatedNavigation.isEmpty()) {
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

            navigationFragmentViewModel.addNavigationFragment(newNavigationIndex, newFragment!!)
//            currentNavigation[newNavigationIndex].add(newFragment!!)
            val currentFragment = activatedNavigation.last()
            currentFragment.arguments = bundle

            supportFragmentManager.beginTransaction().add(R.id.activity_main_layout_frame, currentFragment)
                .commit()

            navigationFragmentViewModel.changeActivatedNavigation(newNavigationIndex)
//            currentNavigationIndex = newNavigationIndex
            return
        }

        val currentFragment = activatedNavigation.last()
        supportFragmentManager.beginTransaction().show(currentFragment)
            .commit()

        println(supportFragmentManager.fragments)
        navigationFragmentViewModel.changeActivatedNavigation(newNavigationIndex)
//        currentNavigationIndex = newNavigationIndex
    }

    fun changeFragment(newFragment: Fragment?, bundle: Bundle? = null, doRemove: Boolean = false) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        val activatedNavigationIndex = navigationFragmentViewModel.activatedNavigationIndex.value!!
        val activatedNavigation = navigationFragmentViewModel.navigationFragments.value!![activatedNavigationIndex]

        // Remove 할 때 newFragment = null
        if (doRemove) {
            supportFragmentManager.beginTransaction().remove(activatedNavigation.last())
                .commit()

            navigationFragmentViewModel.removeNavigationFragment(activatedNavigationIndex, activatedNavigation.lastIndex)
//            currentNavigation[currentNavigationIndex].removeAt(currentNavigation[currentNavigationIndex].lastIndex)

            supportFragmentManager.beginTransaction().show(activatedNavigation.last())
                .commit()

            return
        }

        supportFragmentManager.beginTransaction().hide(activatedNavigation.last())
            .commit()

        navigationFragmentViewModel.addNavigationFragment(activatedNavigationIndex, newFragment!!)
//        currentNavigation[currentNavigationIndex].add(newFragment!!)
        val currentFragment = activatedNavigation.last()
        currentFragment.arguments = bundle

        supportFragmentManager.beginTransaction().add(R.id.activity_main_layout_frame, currentFragment)
            .commit()
    }
}
