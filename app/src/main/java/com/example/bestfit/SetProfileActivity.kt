package com.example.bestfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bestfit.model.CategoryDTO
import kotlinx.android.synthetic.main.activity_set_profile.*
import kotlinx.android.synthetic.main.fragment_dressroom.view.*

class SetProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        initViewPager()
    }

    private fun initViewPager() {
        val setProfile = intent.getBooleanExtra("setProfile", false)
        var initPosition = 0

        if (setProfile)
            initPosition = 1

        activity_set_profile_viewpager.adapter = SetProfileFragmentPagerAdapter(supportFragmentManager, 2)
        activity_set_profile_viewpager.currentItem = initPosition
        activity_set_profile_indicator.setViewPager(activity_set_profile_viewpager)
    }

    inner class SetProfileFragmentPagerAdapter(fm: FragmentManager, private val fragmentSize: Int) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SetProfileFragment()
                1 -> SetDetailProfileFragment()
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return fragmentSize
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "SetProfileFragments"
        }
    }
}
