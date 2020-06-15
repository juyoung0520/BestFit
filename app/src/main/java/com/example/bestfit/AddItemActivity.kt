
package com.example.bestfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
<<<<<<< Updated upstream
=======
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_set_profile.*
import kotlinx.android.synthetic.main.fragment_add_item.*
>>>>>>> Stashed changes

class AddItemActivity : AppCompatActivity() {
    val items = arrayOf("아우터", "상의", "바지", "치마", "원피스/세트")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        initViewPager()

<<<<<<< Updated upstream
//        activity_add_item_spinner_category.adapter = spinnerAdapter
//        activity_add_item_spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
=======
//        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, items)
//
//        fragment_add_item_spinner_category.adapter = spinnerAdapter
//        fragment_add_item_spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
>>>>>>> Stashed changes
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//                when(p2) {
//                    0 -> {
//
//                    }
//
//                    else -> {
//
//                    }
//                }
//            }
//
//        }
<<<<<<< Updated upstream
=======

    }

    private fun initViewPager() {
        val setProfile = intent.getBooleanExtra("setProfile", false)
        var initPosition = 0

        if (setProfile)
            initPosition = 1

        activity_add_item_viewpager.adapter = ProfileFragmentPagerAdapter(supportFragmentManager, 2)
        activity_add_item_viewpager.currentItem = initPosition
        activity_add_item_indicator.setViewPager(activity_add_item_viewpager)
    }

    inner class ProfileFragmentPagerAdapter(fm: FragmentManager, private val fragmentSize: Int) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
>>>>>>> Stashed changes

        override fun getPageTitle(position: Int): CharSequence {
            return "SetProfileFragments"
        }
    }
}