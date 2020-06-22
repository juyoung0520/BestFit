
package com.example.bestfit

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_add_item.view.*
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*

class AddItemActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private val fragments = arrayListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

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
        setSupportActionBar(activity_add_item_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViewPager() {
        activity_add_item_viewpager.adapter = AddItemFragmentPagerAdapter(supportFragmentManager, 2)
        activity_add_item_indicator.setViewPager(activity_add_item_viewpager)
    }

    inner class AddItemFragmentPagerAdapter(fm: FragmentManager, private val fragmentSize: Int) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val fragment = when (position) {
                0 -> AddItemFirstFragment()
                1 -> AddItemSecondFragment()
                else -> Fragment()
            }

            fragments.add(fragment)
            return fragment
        }

        override fun getCount(): Int {
            return fragmentSize
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "AddItemFragments"
        }
    }

    fun changeViewPage(isPrev: Boolean, position: Int = 1) {
        var newPosition = activity_add_item_viewpager.currentItem
        if (isPrev)
            newPosition -= position
        else
            newPosition += position

        if (newPosition < 0) {
            finish()
            return
        }
        else if (newPosition > activity_add_item_viewpager.adapter!!.count)
            return

        activity_add_item_viewpager.currentItem = newPosition
    }

    fun submitAddItem() {
        val firstFragment = (fragments[0] as AddItemFirstFragment).fragmentView
        val secondFragment = (fragments[1] as AddItemSecondFragment).fragmentView

        val itemDTO = ItemDTO()
        itemDTO.timestamp = System.currentTimeMillis()
        itemDTO.categoryId = (firstFragment.fragment_add_item_first_actv_category.tag as CategoryDTO).id
        itemDTO.subCategoryId = firstFragment.fragment_add_item_first_actv_sub_category.tag as String
        itemDTO.name = firstFragment.fragment_add_item_first_text_item_name.text.toString()

        db.collection("items").add(itemDTO).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentId = task.result!!.id

                db.collection("accounts").document(currentUid).update("items", FieldValue.arrayUnion(documentId)).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }
}