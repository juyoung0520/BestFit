package com.example.bestfit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.viewmodel.DataViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: DataViewModel

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private val startForResult =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val accountDTO = result.data!!.getParcelableExtra<AccountDTO>("accountDTO")!!
            viewModel.setAccountDTO(accountDTO)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()

        activity_main_bottom_nav.setOnNavigationItemSelectedListener(this)

//        if (savedInstanceState == null) {
//
//            // SetProfile Check
//            checkSetProfile()
//        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        val isInitializedObserver = Observer<Boolean> { isInitialized ->
            if (isInitialized) {
                viewModel.getAllItemDTOs()
                initViewPager()
            }
        }

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            if (accountDTO.nickname == null)
                checkSetProfile()
        }

        viewModel.isInitialized.observe(this, initObserver)
        viewModel.accountDTO.observe(this, accountDTOObserver)
    }

    private fun initViewPager() {
        activity_main_viewpager.offscreenPageLimit = 3
        activity_main_viewpager.adapter = NavigationPagerAdapter(this, 3)
        activity_main_viewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                var navController: NavController? = null
                when (position) {
                    0 -> {
                        navController = (supportFragmentManager.fragments[0].childFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment).navController
                        activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_home
                    }
                    1 -> {
                        navController = (supportFragmentManager.fragments[1].childFragmentManager.findFragmentById(R.id.nav_host_dressroom) as NavHostFragment).navController
                        activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_dressroom
                    }
                    2 -> {
                        navController = (supportFragmentManager.fragments[2].childFragmentManager.findFragmentById(R.id.nav_host_mypage) as NavHostFragment).navController
                        activity_main_bottom_nav.selectedItemId = R.id.menu_bottom_nav_action_mypage
                    }
                }

                onBackPressedDispatcher.addCallback {
                    navController!!.navigateUp()
                }
            }
        })
        activity_main_viewpager.isUserInputEnabled = false
    }

    class NavigationPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val fragmentSize: Int
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return fragmentSize
        }

        override fun createFragment(position: Int): Fragment {
            val bundle = Bundle()

            when (position) {
                0 -> bundle.putInt("layoutResource", R.layout.navigation_home)
                1 -> bundle.putInt("layoutResource", R.layout.navigation_dressroom)
                2 -> bundle.putInt("layoutResource", R.layout.navigation_mypage)
            }

            val fragment = ContainerFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    private fun checkSetProfile() {
        val intent = Intent(this, SetProfileActivity::class.java).putExtra("accountDTO", viewModel.accountDTO.value)
        startForResult.launch(intent)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.menu_bottom_nav_action_home -> activity_main_viewpager.currentItem = 0
            R.id.menu_bottom_nav_action_dressroom -> activity_main_viewpager.currentItem = 1
            R.id.menu_bottom_nav_action_mypage -> activity_main_viewpager.currentItem = 2
        }

        return true
    }
}