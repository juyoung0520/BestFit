package com.example.bestfit

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_set_profile.*
import kotlinx.android.synthetic.main.activity_signin.*

class SetProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        initToolbar()

        initViewPager()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                changeViewPage(true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(activity_set_profile_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                0 -> SetProfileFirstFragment()
                1 -> SetProfileSecondFragment()
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

    fun changeViewPage(isPrev: Boolean, position: Int = 1) {
        var newPosition = activity_set_profile_viewpager.currentItem
        if (isPrev)
            newPosition -= position
        else
            newPosition += position

        if (newPosition < 0) {
            finish()
            return
        }
        else if (newPosition > activity_set_profile_viewpager.adapter!!.count)
            return

        activity_set_profile_viewpager.currentItem = newPosition
    }
}
