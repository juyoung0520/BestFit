
package com.example.bestfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_add_item.*

class AddItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        initViewPager()
    }

    private fun initViewPager() {
        activity_add_item_viewpager.adapter = AddItemFragmentPagerAdapter(supportFragmentManager, 2)
        activity_add_item_indicator.setViewPager(activity_add_item_viewpager)
    }

    inner class AddItemFragmentPagerAdapter(fm: FragmentManager, private val fragmentSize: Int) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> AddItemFirstFragment()
                1 -> AddItemFirstFragment()
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return fragmentSize
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "AddItemFragments"
        }
    }
}