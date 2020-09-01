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

        val initObserver = Observer<Boolean> { isInit ->
            if (isInit) {
                viewModel.getItemDTOs()
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
                2 -> bundle.putInt("layoutResource", R.layout.navigation_settings)
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
            R.id.menu_bottom_nav_action_settings -> activity_main_viewpager.currentItem = 2
        }

        return true
    }
}