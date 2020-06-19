package com.example.bestfit

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bestfit.model.AccountDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_set_profile.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.fragment_set_profile_first.*
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*

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
        activity_set_profile_viewpager.adapter = SetProfileFragmentPagerAdapter(supportFragmentManager, 3)
        activity_set_profile_indicator.setViewPager(activity_set_profile_viewpager)
    }

    inner class SetProfileFragmentPagerAdapter(fm: FragmentManager, private val fragmentSize: Int) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val fragment = when (position) {
                0 -> SetProfileFirstFragment()
                1 -> SetProfileSecondFragment()
                2 -> SetProfileThirdFragment()
                else -> Fragment()
            }

            fragments.add(fragment)
            return fragment
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

    fun submitSetProfile() {
        val firstFragment = (fragments[0] as SetProfileFirstFragment).fragmentView
        val secondFragment = (fragments[1] as SetProfileSecondFragment).fragmentView
        val thirdFragment = (fragments[2] as SetProfileThirdFragment).fragmentView

        val accountDTO = AccountDTO()
        accountDTO.nickname = firstFragment.fragment_set_profile_first_text_nickname.text.toString()
        accountDTO.sex = firstFragment.fragment_set_profile_first_btn_male.isChecked
        accountDTO.birth = firstFragment.fragment_set_profile_first_np_birth.value
        accountDTO.height = secondFragment.fragment_set_profile_second_np_height.value
        accountDTO.weight = secondFragment.fragment_set_profile_second_np_weight.value

        db.collection("accounts").document(currentUid).set(accountDTO).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println(accountDTO)
                finish()
            }
        }
    }
}
