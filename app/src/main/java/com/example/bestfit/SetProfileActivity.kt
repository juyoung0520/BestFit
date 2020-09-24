package com.example.bestfit

import android.accounts.Account
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.SetProfileActivityViewModel
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
    private lateinit var viewModel: SetProfileActivityViewModel
    private val auth = FirebaseAuth.getInstance()
    private val fragments = arrayListOf<Fragment>()
    private lateinit var tempAccountDTO: AccountDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)
        tempAccountDTO = intent.getParcelableExtra<AccountDTO>("accountDTO")!!

        initViewModel()
        initToolbar()
        initViewPager()

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SetProfileActivityViewModel::class.java)

        val initializedObserver = Observer<Boolean> { initialized ->
            if (initialized) {
                if (tempAccountDTO.id == null) {
                    viewModel.setTempAccountDTO(AccountDTO())
                } else {
                    activity_set_profile_tv_toolbar_title.text = "프로필 수정"
                    viewModel.setTempAccountDTO(tempAccountDTO)
                }
            }
        }

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            setResult(RESULT_OK, Intent().putExtra("accountDTO", accountDTO))
            finish()
        }

        viewModel.initialized.observe(this, initializedObserver)
        viewModel.accountDTO.observe(this, accountDTOObserver)
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

                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity_set_profile_viewpager.windowToken, 0)

                when (position) {
                    0 -> {
                        if (tempAccountDTO.id == null) {
                            activity_set_profile_toolbar.navigationIcon = null
                            activity_set_profile_toolbar.menu.clear()
                        } else {
                            activity_set_profile_toolbar.setNavigationIcon(R.drawable.ic_close)
                            activity_set_profile_toolbar.menu.clear()
                        }
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
        val accountDTO = viewModel.getTempAccountDTO()!!
        if (tempAccountDTO.id != null) {
            submitModifyProfile()
            return
        }

        viewModel.submitSetProfile(accountDTO)
    }

    fun submitModifyProfile() {
        val accountDTO = viewModel.getTempAccountDTO()!!
        viewModel.submitModifyProfile(accountDTO)
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
