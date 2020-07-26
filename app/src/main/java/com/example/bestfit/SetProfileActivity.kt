package com.example.bestfit

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.model.AccountDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_set_profile.*
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_third.view.*

class SetProfileActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private val fragments = arrayListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        initToolbar()

        initViewPager()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                changeViewPage(true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(activity_set_profile_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initViewPager() {
        activity_set_profile_viewpager.adapter = SetProfileFragmentPagerAdapter(this, 3)
        activity_set_profile_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    1, 2 -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
                    }
                }
            }
        })
        activity_set_profile_indicator.setViewPager(activity_set_profile_viewpager)
    }

    inner class SetProfileFragmentPagerAdapter(fragmentActivity: FragmentActivity, private val fragmentSize: Int) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return fragmentSize
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> SetProfileFirstFragment()
                1 -> SetProfileSecondFragment()
                2 -> SetProfileThirdFragment()
                else -> Fragment()
            }

            fragments.add(fragment)
            return fragment
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
        else if (newPosition > activity_set_profile_viewpager.adapter!!.itemCount)
            return

        activity_set_profile_viewpager.currentItem = newPosition

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun submitSetProfile() {
        val firstFragment = (fragments[0] as SetProfileFirstFragment).fragmentView
        val secondFragment = (fragments[1] as SetProfileSecondFragment).fragmentView
        val thirdFragment = (fragments[2] as SetProfileThirdFragment).fragmentView

        val accountDTO = AccountDTO()
        accountDTO.nickname = firstFragment.fragment_set_profile_first_text_nickname.text.toString()
        accountDTO.sex = firstFragment.fragment_set_profile_first_group_sex.checkedButtonId == firstFragment.fragment_set_profile_first_btn_male.id
        accountDTO.birth = firstFragment.fragment_set_profile_first_actv_birth.text.toString().toInt()
        accountDTO.height = secondFragment.fragment_set_profile_second_text_height.text.toString().toInt()
        accountDTO.weight = secondFragment.fragment_set_profile_second_text_weight.text.toString().toInt()
        accountDTO.topId = secondFragment.fragment_set_profile_second_text_top.tag as String
        accountDTO.bottomId = secondFragment.fragment_set_profile_second_text_bottom.tag as String
        accountDTO.shoesId = secondFragment.fragment_set_profile_second_text_shoes.tag as String
        accountDTO.message = thirdFragment.fragment_set_profile_third_text_message.text.toString()

        db.collection("accounts").document(currentUid).set(accountDTO).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println(accountDTO)
                finish()
            }
        }
    }
}
