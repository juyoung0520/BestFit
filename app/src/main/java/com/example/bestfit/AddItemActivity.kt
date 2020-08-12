
package com.example.bestfit

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_add_item.view.*
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*
import kotlinx.android.synthetic.main.fragment_add_item_fourth.view.*
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.File

class AddItemActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val fragments = arrayListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        initToolbar()

        initViewPager()
    }

    private fun initToolbar() {
        activity_add_item_toolbar.setNavigationOnClickListener {
            changeViewPage(true)
        }
    }

    private fun initViewPager() {
        activity_add_item_viewpager.adapter = AddItemFragmentPagerAdapter(this, 4)
        activity_add_item_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> {
                        activity_add_item_toolbar.setNavigationIcon(R.drawable.ic_close)
                        activity_add_item_toolbar.menu.clear()
                    }
                    1, 2 -> {
                        activity_add_item_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        activity_add_item_toolbar.menu.clear()
                    }
                    3 -> {
                        activity_add_item_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        activity_add_item_toolbar.inflateMenu(R.menu.menu_activity_add_item)
                        activity_add_item_toolbar.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.menu_activity_add_item_submit -> {
                                    submitAddItem()

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
        activity_add_item_indicator.setViewPager(activity_add_item_viewpager)
    }

    inner class AddItemFragmentPagerAdapter(fragmentActivity: FragmentActivity, private val fragmentSize: Int) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return fragmentSize
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> AddItemFirstFragment()
                1 -> AddItemSecondFragment()
                2 -> AddItemThirdFragment()
                3 -> AddItemFourthFragment()
                else -> Fragment()
            }

            fragments.add(fragment)
            return fragment
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
        else if (newPosition > activity_add_item_viewpager.adapter!!.itemCount)
            return

        activity_add_item_viewpager.currentItem = newPosition

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun submitAddItem() {
        val firstFragment = fragments[0] as AddItemFirstFragment
        val firstFragmentView = firstFragment.fragmentView
        val secondFragment = fragments[1] as AddItemSecondFragment
        val secondFragmentView = secondFragment.fragmentView
        val thirdFragment = fragments[2] as AddItemThirdFragment
        val thirdFragmentView = thirdFragment.fragmentView
        val fourthFragment = fragments[3] as AddItemFourthFragment
        val fourthFragmentView = fourthFragment.fragmentView

        val itemDTO = ItemDTO()
        itemDTO.timestamp = System.currentTimeMillis()
        itemDTO.uid = currentUid
        itemDTO.categoryId = (firstFragmentView.fragment_add_item_first_actv_category.tag as CategoryDTO).id
        itemDTO.subCategoryId = firstFragmentView.fragment_add_item_first_actv_sub_category.tag as String
//        itemDTO.brandId
        itemDTO.name = secondFragmentView.fragment_add_item_second_text_item_name.text.toString()
        itemDTO.sizeFormatId = thirdFragment.selectedSizeFormatId
        itemDTO.sizeId = thirdFragment.selectedSizeId
        itemDTO.sizeReview = thirdFragmentView.fragment_add_item_third_group_size_review.tag as Int
        itemDTO.review = fourthFragmentView.fragment_add_item_fourth_text_review.text.toString()
        itemDTO.searchKeywords = getSearchKeywords(itemDTO.name.toString())

        val imageUris = arrayListOf<Uri>()
        for (image in firstFragment.itemImages) {
            val uri = FileProvider.getUriForFile(this, "com.jinu.imagepickerlib.fileprovider", File(image))
            imageUris.add(uri)
        }

        db.collection("items").add(itemDTO).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentId = task.result!!.id

                if (imageUris.size > 0) {
                    val imageUrls = arrayOfNulls<String>(imageUris.size)
                    var cnt = 0

                    for ((idx, imageUri) in imageUris.withIndex()) {
                        val storageRef = storage.reference.child("items").child(documentId).child(idx.toString())
                        storageRef.putFile(imageUri).continueWithTask {
                            return@continueWithTask storageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                imageUrls[idx] = task.result.toString()
                                cnt += 1

                                if (cnt >= imageUris.size) {
                                    db.collection("items").document(documentId).update("images", imageUrls.asList())

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
                }
                else {
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

    fun getSearchKeywords(itemName: String): ArrayList<String> {
        var nameString = itemName.toLowerCase()
        val words = nameString.split(" ")
        val keywords = ArrayList<String>()

        for (word in words) {
            var appendString = ""

            for (position in nameString.indices) {
                appendString += nameString[position]
                keywords.add(appendString)
            }

            nameString = nameString.replace("$word ", "")
        }

        return keywords
    }
}