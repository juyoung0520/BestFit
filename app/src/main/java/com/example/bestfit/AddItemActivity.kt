
package com.example.bestfit

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*
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
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    private fun initViewPager() {
        activity_add_item_viewpager.adapter = AddItemFragmentPagerAdapter(supportFragmentManager, 3)
        activity_add_item_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
                    1, 2 -> supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
                }
            }

        })
        activity_add_item_indicator.setViewPager(activity_add_item_viewpager)
    }

    inner class AddItemFragmentPagerAdapter(fm: FragmentManager, private val fragmentSize: Int) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val fragment = when (position) {
                0 -> AddItemFirstFragment()
                1 -> AddItemSecondFragment()
                2 -> AddItemThirdFragment()
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
        val firstFragment = fragments[0] as AddItemFirstFragment
        val firstFragmentView = firstFragment.fragmentView
        val secondFragment = fragments[1] as AddItemSecondFragment
        val secondFragmentView = secondFragment.fragmentView
        val thirdFragment = fragments[2] as AddItemThirdFragment
        val thirdFragmentView = thirdFragment.fragmentView

        val itemDTO = ItemDTO()
        itemDTO.timestamp = System.currentTimeMillis()
        itemDTO.categoryId = (firstFragmentView.fragment_add_item_first_actv_category.tag as CategoryDTO).id
        itemDTO.subCategoryId = firstFragmentView.fragment_add_item_first_actv_sub_category.tag as String
//        itemDTO.brandId
        itemDTO.name = secondFragmentView.fragment_add_item_second_text_item_name.text.toString()
//        itemDTO.size = thirdFragmentView.fragment_add_item_third_text_size.text.toString()
        itemDTO.review = thirdFragmentView.fragment_add_item_third_text_review.text.toString()

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
}