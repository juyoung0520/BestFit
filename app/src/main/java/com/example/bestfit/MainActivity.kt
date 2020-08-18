package com.example.bestfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.DetailFragmentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: DataViewModel// by viewModels() // by activityViewModels()

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initViewPager()

        activity_main_bottom_nav.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            InitData.initData()

            // SetProfile Check
            checkSetProfile()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)

//        if (!viewModel.isInitialized()) {
//            viewModel.setInitializedState(true)
//
//            viewModel.getCategoryDTO()
//            viewModel.getSizeFormatDTO()
//        }

        // loading check?? 다 로딩되기 전에 settingsfragment 가면 죽음
    }

    private fun initViewPager() {
        activity_main_viewpager.adapter = NavigationPagerAdapter(this, 3)
        activity_main_viewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_home
                    1 -> activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_dressroom
                    2 -> activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_settings
                }
            }
        })
        activity_main_viewpager.isUserInputEnabled = false
    }

    inner class NavigationPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val fragmentSize: Int
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return fragmentSize
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Fragment(R.layout.navigation_home)
                1 -> Fragment(R.layout.navigation_dressroom)
                2 -> Fragment(R.layout.navigation_settings)
                else -> Fragment()
            }
        }
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
            R.id.menu_bottom_nav_action_home -> activity_main_viewpager.currentItem = 0
            R.id.menu_bottom_nav_action_dressroom -> activity_main_viewpager.currentItem = 1
            R.id.menu_bottom_nav_action_settings -> activity_main_viewpager.currentItem = 2
        }

        return true
    }
}