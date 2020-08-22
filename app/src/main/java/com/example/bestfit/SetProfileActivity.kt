package com.example.bestfit

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.model.AccountDTO
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_set_profile.*
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_third.view.*
import java.security.AccessController.getContext

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

    private fun initToolbar() {
        activity_set_profile_toolbar.setNavigationOnClickListener {
            changeViewPage(true)
        }
    }

    private fun initViewPager() {
        activity_set_profile_viewpager.adapter = SetProfileFragmentPagerAdapter(this, 3)
        activity_set_profile_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> {
                        activity_set_profile_toolbar.navigationIcon = null
                        activity_set_profile_toolbar.menu.clear()
                    }
                    1 -> {
                        activity_set_profile_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        activity_set_profile_toolbar.menu.clear()
                    }
                    2 -> {
                        activity_set_profile_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        activity_set_profile_toolbar.inflateMenu(R.menu.menu_activity_set_profile)
                        activity_set_profile_toolbar.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.menu_activity_set_profile_submit -> {
                                    if (emptyCheckSetProfile())
                                        submitSetProfile()

                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
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

    fun changeViewPage(isPrev: Boolean, moveCount: Int = 1) {
        var newPosition = activity_set_profile_viewpager.currentItem
        if (isPrev)
            newPosition -= moveCount
        else
            newPosition += moveCount

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

    private fun changeViewPage(position: Int) {
        if (position < 0) {
            finish()
            return
        }
        else if (position > activity_set_profile_viewpager.adapter!!.itemCount)
            return

        activity_set_profile_viewpager.currentItem = position

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun submitSetProfile() {
        val firstFragment = (fragments[0] as SetProfileFirstFragment).fragmentView
        val secondFragment = (fragments[1] as SetProfileSecondFragment).fragmentView
        val thirdFragment = (fragments[2] as SetProfileThirdFragment).fragmentView

        val accountDTO = AccountDTO()
        accountDTO.nickname = firstFragment.fragment_set_profile_first_text_nickname.text.toString()
        accountDTO.sex = firstFragment.fragment_set_profile_first_group_gender.checkedButtonId == firstFragment.fragment_set_profile_first_btn_male.id
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

    fun emptyCheckSetProfile() : Boolean {
        val firstFragment = (fragments[0] as SetProfileFirstFragment).fragmentView
        val secondFragment = (fragments[1] as SetProfileSecondFragment).fragmentView

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)

        //firstFragment
        if (firstFragment.fragment_set_profile_first_text_nickname.text.isNullOrEmpty()) {
            changeViewPage(0)
            firstFragment.fragment_set_profile_first_error_nickname.visibility = View.VISIBLE
            firstFragment.fragment_set_profile_first_layout_text_nickname.animation = shake
            return false
        }
        if (firstFragment.fragment_set_profile_first_group_gender.checkedButtonId == View.NO_ID) {
            changeViewPage(0)
            firstFragment.fragment_set_profile_first_error_group_gender.visibility = View.VISIBLE
            return false
        }
        if (firstFragment.fragment_set_profile_first_actv_birth.text.isNullOrEmpty()) {
            changeViewPage(0)
            firstFragment.fragment_set_profile_first_error_birth.visibility = View.VISIBLE
            return false
        }

        //secondFragment
        if (!secondFragment.fragment_set_profile_second_text_height.text!!.isDigitsOnly()) {
            changeViewPage(1)
            secondFragment.fragment_set_profile_second_error_user_size.visibility = View.VISIBLE
            return false
        }
        if (!secondFragment.fragment_set_profile_second_text_weight.text!!.isDigitsOnly()) {
            changeViewPage(1)
            secondFragment.fragment_set_profile_second_error_user_size.visibility = View.VISIBLE
            return false
        }
        if (secondFragment.fragment_set_profile_second_text_top.tag == null) {
            changeViewPage(1)
            secondFragment.fragment_set_profile_second_error_clothes_size.visibility = View.VISIBLE
            return false
        }
        if (secondFragment.fragment_set_profile_second_text_bottom.tag == null) {
            changeViewPage(1)
            secondFragment.fragment_set_profile_second_error_clothes_size.visibility = View.VISIBLE
            return false
        }
        if (secondFragment.fragment_set_profile_second_text_shoes.tag == null) {
            changeViewPage(1)
            secondFragment.fragment_set_profile_second_error_clothes_size.visibility = View.VISIBLE
            return false
        }

        return true
    }
}
